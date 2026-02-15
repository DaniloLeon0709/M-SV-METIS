package com.vitaalert.ble.service

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.ParcelUuid
import android.util.Log
import androidx.core.content.ContextCompat
import com.vitaalert.ble.gatt.BleGattClient
import com.vitaalert.domain.model.VitalQuality
import com.vitaalert.domain.model.VitalReading
import com.vitaalert.domain.model.VitalType
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.time.Instant
import java.util.UUID

/**
 * Callback for device connection events.
 */
interface BleConnectionListener {
    fun onSearching()
    fun onDeviceFound(macAddress: String, name: String?)
    fun onConnecting(macAddress: String, name: String?)
    fun onDiscoveringServices(macAddress: String, name: String?)
    fun onWaitingForData(macAddress: String, name: String?)
    fun onDeviceConnected(macAddress: String, name: String?)
    fun onDeviceDisconnected(macAddress: String)
    fun onError(message: String)
}

/**
 * BLE vital source that connects to heart rate monitors and collects readings.
 */
class BleVitalSource(
    private val context: Context,
    private val gattClient: BleGattClient = BleGattClient()
) : VitalSource {

    companion object {
        private const val TAG = "BleVitalSource"
        private val CLIENT_CHARACTERISTIC_CONFIG: UUID =
            UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
    }

    private val bluetoothManager: BluetoothManager? by lazy {
        context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
    }

    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        bluetoothManager?.adapter
    }

    private val scanner: BluetoothLeScanner? by lazy {
        bluetoothAdapter?.bluetoothLeScanner
    }

    private var connectedGatt: BluetoothGatt? = null

    /**
     * Listener for device connection events.
     */
    var connectionListener: BleConnectionListener? = null

    /**
     * Checks if Bluetooth is available and enabled.
     */
    fun isBluetoothAvailable(): Boolean {
        return bluetoothAdapter?.isEnabled == true
    }

    /**
     * Checks if the required BLE permissions are granted.
     */
    fun hasPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) ==
                PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) ==
                PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH) ==
                PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * Gets bonded devices that might support Heart Rate monitoring.
     */
    @SuppressLint("MissingPermission")
    private fun getBondedWearables(): List<BluetoothDevice> {
        if (!hasPermissions()) return emptyList()

        return try {
            bluetoothAdapter?.bondedDevices?.filter { device ->
                val name = device.name?.lowercase() ?: return@filter false
                name.contains("watch") ||
                name.contains("band") ||
                name.contains("fit") ||
                name.contains("heart") ||
                name.contains("polar") ||
                name.contains("garmin") ||
                name.contains("huawei") ||
                name.contains("samsung") ||
                name.contains("mi band") ||
                name.contains("amazfit") ||
                name.contains("fitbit") ||
                name.contains("xiaomi")
            } ?: emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting bonded devices", e)
            emptyList()
        }
    }

    @SuppressLint("MissingPermission")
    override fun readings(): Flow<VitalReading> = callbackFlow {
        if (!isBluetoothAvailable()) {
            Log.w(TAG, "Bluetooth not available or not enabled")
            close()
            return@callbackFlow
        }

        if (!hasPermissions()) {
            Log.w(TAG, "Missing BLE permissions")
            close()
            return@callbackFlow
        }

        var currentDevice: BluetoothDevice? = null
        var isConnected = false

        val gattCallback = object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                val deviceName = try { gatt.device?.name } catch (e: Exception) { null }
                val deviceAddress = gatt.device?.address ?: "unknown"

                Log.d(TAG, "onConnectionStateChange: status=$status, newState=$newState, device=$deviceName")

                when (newState) {
                    BluetoothProfile.STATE_CONNECTED -> {
                        Log.d(TAG, "Connected to GATT server: $deviceName")
                        connectedGatt = gatt
                        isConnected = true

                        // Notify: discovering services
                        connectionListener?.onDiscoveringServices(deviceAddress, deviceName)

                        // Discover services
                        gatt.discoverServices()
                    }
                    BluetoothProfile.STATE_CONNECTING -> {
                        Log.d(TAG, "Connecting to GATT server: $deviceName")
                        // Este estado es transitorio, solo log
                    }
                    BluetoothProfile.STATE_DISCONNECTING -> {
                        Log.d(TAG, "Disconnecting from GATT server: $deviceName")
                    }
                    BluetoothProfile.STATE_DISCONNECTED -> {
                        Log.d(TAG, "Disconnected from GATT server, status=$status")
                        isConnected = false
                        connectedGatt = null

                        // Check if connection failed
                        if (status != BluetoothGatt.GATT_SUCCESS) {
                            Log.e(TAG, "Connection failed with status: $status")
                            connectionListener?.onError("Error de conexión (código: $status)")
                        } else {
                            // Notify listener
                            connectionListener?.onDeviceDisconnected(deviceAddress)
                        }

                        // Try to reconnect after a delay
                        currentDevice?.let { device ->
                            try {
                                Log.d(TAG, "Attempting to reconnect...")
                                connectionListener?.onConnecting(device.address, try { device.name } catch (e: Exception) { null })
                                device.connectGatt(context, true, this)
                            } catch (e: Exception) {
                                Log.e(TAG, "Failed to reconnect", e)
                                connectionListener?.onError("Error al reconectar: ${e.message}")
                            }
                        }
                    }
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                val deviceName = try { gatt.device?.name } catch (e: Exception) { null }
                val deviceAddress = gatt.device?.address ?: "unknown"

                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Log.d(TAG, "Services discovered, looking for Heart Rate service...")

                    // List all services for debugging
                    gatt.services.forEach { service ->
                        Log.d(TAG, "Found service: ${service.uuid}")
                    }

                    // Find Heart Rate service
                    val hrService = gatt.getService(gattClient.heartRateService)
                    if (hrService != null) {
                        Log.d(TAG, "Heart Rate Service found!")
                        val hrCharacteristic = hrService.getCharacteristic(gattClient.heartRateMeasurement)
                        if (hrCharacteristic != null) {
                            Log.d(TAG, "Heart Rate Measurement characteristic found, enabling notifications...")
                            // Notify: waiting for data
                            connectionListener?.onWaitingForData(deviceAddress, deviceName)
                            enableNotifications(gatt, hrCharacteristic)
                        } else {
                            Log.w(TAG, "Heart Rate Measurement characteristic not found")
                            connectionListener?.onError("Característica HR no encontrada en $deviceName")
                        }
                    } else {
                        Log.w(TAG, "Heart Rate Service NOT found on this device")
                        connectionListener?.onError("Servicio Heart Rate no disponible en $deviceName")
                        // Try other known HR service UUIDs
                        gatt.services.forEach { service ->
                            service.characteristics.forEach { char ->
                                Log.d(TAG, "  Characteristic: ${char.uuid}")
                            }
                        }
                    }
                } else {
                    Log.w(TAG, "Service discovery failed with status: $status")
                    connectionListener?.onError("Error al descubrir servicios")
                }
            }

            @Deprecated("Deprecated in API 33")
            override fun onCharacteristicChanged(
                gatt: BluetoothGatt,
                characteristic: BluetoothGattCharacteristic
            ) {
                handleCharacteristicChange(characteristic)
            }

            override fun onCharacteristicChanged(
                gatt: BluetoothGatt,
                characteristic: BluetoothGattCharacteristic,
                value: ByteArray
            ) {
                handleCharacteristicChange(characteristic, value)
            }

            private fun handleCharacteristicChange(
                characteristic: BluetoothGattCharacteristic,
                value: ByteArray? = null
            ) {
                when (characteristic.uuid) {
                    gattClient.heartRateMeasurement -> {
                        @Suppress("DEPRECATION")
                        val bytes = value ?: characteristic.value ?: return
                        val heartRate = gattClient.parseHeartRate(bytes)
                        if (heartRate != null) {
                            val deviceName = try {
                                currentDevice?.name ?: "BLE Device"
                            } catch (e: Exception) {
                                "BLE Device"
                            }

                            val reading = VitalReading(
                                id = UUID.randomUUID().toString(),
                                type = VitalType.HR,
                                value = heartRate.toDouble(),
                                unit = "bpm",
                                timestamp = Instant.now(),
                                quality = VitalQuality.VALID,
                                sourceDeviceId = currentDevice?.address ?: "unknown",
                                sessionId = null
                            )
                            trySend(reading)
                            Log.d(TAG, "Heart Rate: $heartRate bpm from $deviceName")
                        }
                    }
                }
            }

            private fun enableNotifications(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
                try {
                    gatt.setCharacteristicNotification(characteristic, true)

                    val descriptor = characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG)
                    if (descriptor != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            gatt.writeDescriptor(descriptor, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
                        } else {
                            @Suppress("DEPRECATION")
                            descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                            @Suppress("DEPRECATION")
                            gatt.writeDescriptor(descriptor)
                        }
                        Log.d(TAG, "Notifications enabled for ${characteristic.uuid}")
                    } else {
                        Log.w(TAG, "CCCD descriptor not found")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to enable notifications", e)
                }
            }
        }

        // First, try to connect to bonded wearables
        val bondedWearables = getBondedWearables()
        Log.d(TAG, "Found ${bondedWearables.size} bonded wearables")

        // Notify: searching for devices
        connectionListener?.onSearching()

        if (bondedWearables.isNotEmpty()) {
            // Try to connect to the first bonded wearable
            val device = bondedWearables.first()
            val deviceName = try { device.name } catch (e: Exception) { "Unknown" }
            Log.d(TAG, "Attempting to connect to bonded device: $deviceName (${device.address})")

            // Notify: device found
            connectionListener?.onDeviceFound(device.address, deviceName)

            currentDevice = device
            try {
                // Notify: connecting
                connectionListener?.onConnecting(device.address, deviceName)
                device.connectGatt(context, false, gattCallback)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to connect to bonded device", e)
                connectionListener?.onError("Error al conectar: ${e.message}")
            }
        } else {
            // If no bonded wearables, scan for HR devices
            Log.d(TAG, "No bonded wearables found, starting BLE scan...")

            val scanCallback = object : ScanCallback() {
                override fun onScanResult(callbackType: Int, result: ScanResult) {
                    val device = result.device
                    val deviceName = try {
                        device.name
                    } catch (e: SecurityException) {
                        null
                    }

                    Log.d(TAG, "Found device: ${deviceName ?: "Unknown"} - ${device.address}")

                    // Connect to the first device that advertises Heart Rate service
                    if (currentDevice == null) {
                        // Notify: device found
                        connectionListener?.onDeviceFound(device.address, deviceName)

                        currentDevice = device
                        scanner?.stopScan(this)

                        try {
                            // Notify: connecting
                            connectionListener?.onConnecting(device.address, deviceName)
                            device.connectGatt(context, false, gattCallback)
                        } catch (e: Exception) {
                            Log.e(TAG, "Failed to connect to device", e)
                            connectionListener?.onError("Error al conectar: ${e.message}")
                        }
                    }
                }

                override fun onScanFailed(errorCode: Int) {
                    Log.e(TAG, "BLE scan failed with error code: $errorCode")
                    connectionListener?.onError("Error en escaneo BLE: código $errorCode")
                }
            }

            // Start scanning for Heart Rate devices
            try {
                val filters = listOf(
                    ScanFilter.Builder()
                        .setServiceUuid(ParcelUuid(gattClient.heartRateService))
                        .build()
                )

                val settings = ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .build()

                scanner?.startScan(filters, settings, scanCallback)
                Log.d(TAG, "Started BLE scan for Heart Rate devices")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start BLE scan", e)
                connectionListener?.onError("Error al iniciar escaneo: ${e.message}")
            }
        }

        awaitClose {
            try {
                connectedGatt?.close()
                connectedGatt = null
                currentDevice = null
                Log.d(TAG, "BLE resources cleaned up")
            } catch (e: Exception) {
                Log.e(TAG, "Error cleaning up BLE resources", e)
            }
        }
    }

    /**
     * Disconnects from any connected device.
     */
    @SuppressLint("MissingPermission")
    fun disconnect() {
        try {
            connectedGatt?.disconnect()
            connectedGatt?.close()
            connectedGatt = null
        } catch (e: Exception) {
            Log.e(TAG, "Error disconnecting", e)
        }
    }
}
