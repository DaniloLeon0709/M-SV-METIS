package com.vitaalert.ble.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.vitaalert.domain.repository.VitalRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Foreground service that collects BLE vitals and persists them.
 */
class MonitoringService : Service() {

    companion object {
        private const val TAG = "MonitoringService"
        private const val NOTIFICATION_ID = 2001
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var job: Job? = null

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service onCreate")
        startForeground(NOTIFICATION_ID, createNotification())
        startCollection()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service onStartCommand")
        return START_STICKY
    }

    override fun onDestroy() {
        Log.d(TAG, "Service onDestroy")
        job?.cancel()
        scope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startCollection() {
        val dependencies = BleServiceLocator.dependencies ?: run {
            Log.e(TAG, "Dependencies not set!")
            return
        }

        var hasReceivedData = false
        var connectedDeviceName: String? = null
        var connectedDeviceId: String? = null

        // Configurar el listener de conexión si el source es BleVitalSource
        val source = dependencies.source
        if (source is BleVitalSource) {
            source.connectionListener = object : BleConnectionListener {
                override fun onSearching() {
                    Log.d(TAG, "Searching for devices...")
                    dependencies.onStateChange?.invoke(MonitoringState.SEARCHING, null)
                    updateNotification("Buscando dispositivo...")
                }

                override fun onDeviceFound(macAddress: String, name: String?) {
                    Log.d(TAG, "Device found: $name ($macAddress)")
                    connectedDeviceId = macAddress
                    connectedDeviceName = name ?: "Dispositivo BLE"
                    dependencies.onStateChange?.invoke(
                        MonitoringState.DEVICE_FOUND,
                        ConnectedDeviceInfo(macAddress, name ?: "Dispositivo")
                    )
                    updateNotification("Dispositivo encontrado: ${name ?: "desconocido"}")
                }

                override fun onConnecting(macAddress: String, name: String?) {
                    Log.d(TAG, "Connecting to: $name ($macAddress)")
                    dependencies.onStateChange?.invoke(
                        MonitoringState.CONNECTING,
                        ConnectedDeviceInfo(macAddress, name ?: "Dispositivo")
                    )
                    updateNotification("Conectando a ${name ?: "dispositivo"}...")
                }

                override fun onDiscoveringServices(macAddress: String, name: String?) {
                    Log.d(TAG, "Discovering services on: $name")
                    dependencies.onStateChange?.invoke(
                        MonitoringState.DISCOVERING_SERVICES,
                        ConnectedDeviceInfo(macAddress, name ?: "Dispositivo")
                    )
                    updateNotification("Buscando servicios de salud...")
                }

                override fun onWaitingForData(macAddress: String, name: String?) {
                    Log.d(TAG, "Waiting for data from: $name")
                    connectedDeviceId = macAddress
                    connectedDeviceName = name ?: "Dispositivo BLE"
                    dependencies.onStateChange?.invoke(
                        MonitoringState.WAITING_FOR_DATA,
                        ConnectedDeviceInfo(macAddress, name ?: "Dispositivo")
                    )
                    updateNotification("Esperando datos de ${name ?: "dispositivo"}...")
                }

                override fun onDeviceConnected(macAddress: String, name: String?) {
                    Log.d(TAG, "Device connected: $name ($macAddress)")
                    connectedDeviceId = macAddress
                    connectedDeviceName = name ?: "Dispositivo BLE"
                }

                override fun onDeviceDisconnected(macAddress: String) {
                    Log.d(TAG, "Device disconnected: $macAddress")
                    dependencies.onStateChange?.invoke(MonitoringState.DISCONNECTED, null)
                    hasReceivedData = false
                    updateNotification("Desconectado - reconectando...")
                }

                override fun onError(message: String) {
                    Log.e(TAG, "Error: $message")
                    dependencies.onErrorMessage?.invoke(message)
                    dependencies.onStateChange?.invoke(MonitoringState.ERROR, null)
                    updateNotification("Error: $message")
                }
            }
        }

        job?.cancel()
        job = scope.launch {
            Log.d(TAG, "Starting vital collection...")


            try {
                dependencies.source.readings().collect { reading ->
                    Log.d(TAG, "Received reading: ${reading.type} = ${reading.value} ${reading.unit}")

                    // Guardar en el repositorio
                    dependencies.repository.insert(reading)

                    // Primera lectura - ahora sí estamos realmente conectados y recibiendo datos
                    if (!hasReceivedData) {
                        hasReceivedData = true
                        val deviceId = connectedDeviceId ?: reading.sourceDeviceId
                        val deviceName = connectedDeviceName ?: "Dispositivo BLE"
                        dependencies.onStateChange?.invoke(
                            MonitoringState.CONNECTED,
                            ConnectedDeviceInfo(deviceId, deviceName)
                        )
                        Log.d(TAG, "First reading received - now fully connected!")
                    }

                    // Actualizar la notificación con el último valor
                    updateNotification("❤️ ${reading.value.toInt()} ${reading.unit}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error collecting readings", e)
                dependencies.onStateChange?.invoke(MonitoringState.DISCONNECTED, null)
            }
        }
    }

    private fun updateNotification(value: String) {
        val notification = createNotification(value)
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, notification)
    }

    private fun createNotification(value: String = "Iniciando..."): Notification {
        val channelId = createChannelIfNeeded()
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("VitaAlert - Monitoreo activo")
            .setContentText(value)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun createChannelIfNeeded(): String {
        val channelId = "vitaalert_monitor"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)
            val channel = NotificationChannel(
                channelId,
                "VitaAlert Monitoreo",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Monitoreo de signos vitales en segundo plano"
            }
            manager.createNotificationChannel(channel)
        }
        return channelId
    }
}

/**
 * Estado del monitoreo.
 */
enum class MonitoringState {
    IDLE,
    SEARCHING,            // Buscando dispositivo
    DEVICE_FOUND,         // Dispositivo encontrado
    CONNECTING,           // Conectando
    DISCOVERING_SERVICES, // Descubriendo servicios
    WAITING_FOR_DATA,     // Esperando datos
    CONNECTED,            // Recibiendo datos
    DISCONNECTED,
    ERROR
}

/**
 * Información del dispositivo conectado.
 */
data class ConnectedDeviceInfo(
    val id: String,
    val name: String
)

/**
 * Provides dependencies needed by [MonitoringService].
 */
object BleServiceLocator {
    /**
     * Dependencies for the service.
     */
    var dependencies: Dependencies? = null
}

/**
 * Container for service dependencies.
 */
class Dependencies(
    val repository: VitalRepository,
    val source: VitalSource,
    val onStateChange: ((MonitoringState, ConnectedDeviceInfo?) -> Unit)? = null,
    val onErrorMessage: ((String) -> Unit)? = null
)
