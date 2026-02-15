package com.vitaalert.ble.scanner

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.vitaalert.ble.model.BleDevice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * BLE scanner that exposes discovered devices as a flow.
 */
class BleScanner(private val context: Context) {

    companion object {
        private const val TAG = "BleScanner"
    }

    private val _devices = MutableStateFlow<List<BleDevice>>(emptyList())
    private val discoveredDevices = mutableMapOf<String, BleDevice>()

    private val bluetoothManager: BluetoothManager? by lazy {
        context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
    }

    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        bluetoothManager?.adapter
    }

    private val scanner: BluetoothLeScanner? by lazy {
        bluetoothAdapter?.bluetoothLeScanner
    }

    private var isScanning = false

    private val scanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val device = result.device
            val deviceId = device.address
            val deviceName = try {
                device.name
            } catch (e: SecurityException) {
                null
            }

            val bleDevice = BleDevice(
                id = deviceId,
                name = deviceName,
                rssi = result.rssi
            )

            discoveredDevices[deviceId] = bleDevice
            _devices.value = discoveredDevices.values.toList()

            Log.d(TAG, "Found device: ${deviceName ?: "Unknown"} - $deviceId (RSSI: ${result.rssi})")
        }

        override fun onScanFailed(errorCode: Int) {
            Log.e(TAG, "BLE scan failed with error code: $errorCode")
            isScanning = false
        }
    }

    /**
     * Returns a flow of discovered BLE devices.
     */
    fun scan(): Flow<List<BleDevice>> = _devices.asStateFlow()

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
     * Gets bonded (paired) Bluetooth devices.
     */
    @SuppressLint("MissingPermission")
    fun getBondedDevices(): List<BleDevice> {
        if (!hasPermissions()) {
            Log.w(TAG, "Missing permissions to get bonded devices")
            return emptyList()
        }

        return try {
            bluetoothAdapter?.bondedDevices?.mapNotNull { device ->
                try {
                    BleDevice(
                        id = device.address,
                        name = device.name ?: "Unknown",
                        rssi = 0 // RSSI not available for bonded devices
                    )
                } catch (e: SecurityException) {
                    null
                }
            } ?: emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get bonded devices", e)
            emptyList()
        }
    }

    /**
     * Starts scanning for BLE devices and includes bonded devices.
     */
    @SuppressLint("MissingPermission")
    fun startScan() {
        if (isScanning) {
            Log.d(TAG, "Already scanning")
            return
        }

        if (!isBluetoothAvailable()) {
            Log.w(TAG, "Bluetooth not available or not enabled")
            return
        }

        if (!hasPermissions()) {
            Log.w(TAG, "Missing BLE permissions")
            return
        }

        try {
            discoveredDevices.clear()

            // First, add bonded devices
            val bondedDevices = getBondedDevices()
            bondedDevices.forEach { device ->
                discoveredDevices[device.id] = device
                Log.d(TAG, "Added bonded device: ${device.name} - ${device.id}")
            }
            _devices.value = discoveredDevices.values.toList()

            // Then start scanning for nearby devices
            val settings = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build()

            scanner?.startScan(null, settings, scanCallback)
            isScanning = true
            Log.d(TAG, "Started BLE scan (${bondedDevices.size} bonded devices)")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start BLE scan", e)
        }
    }

    /**
     * Stops scanning for BLE devices.
     */
    @SuppressLint("MissingPermission")
    fun stopScan() {
        if (!isScanning) return

        try {
            scanner?.stopScan(scanCallback)
            isScanning = false
            Log.d(TAG, "Stopped BLE scan")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stop BLE scan", e)
        }
    }

    /**
     * Clears discovered devices.
     */
    fun clearDevices() {
        discoveredDevices.clear()
        _devices.value = emptyList()
    }
}
