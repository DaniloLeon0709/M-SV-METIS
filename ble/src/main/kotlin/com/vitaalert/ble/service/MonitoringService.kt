package com.vitaalert.ble.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.vitaalert.domain.repository.VitalRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Foreground service that collects BLE or demo vitals and persists them.
 */
class MonitoringService : Service() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var job: Job? = null

    override fun onCreate() {
        super.onCreate()
        startForeground(NOTIFICATION_ID, createNotification())
        startCollection()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        job?.cancel()
        scope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startCollection() {
        val dependencies = BleServiceLocator.dependencies ?: return
        job?.cancel()
        job = scope.launch {
            dependencies.source.readings().collect { reading ->
                dependencies.repository.insert(reading)
            }
        }
    }

    private fun createNotification(): Notification {
        val channelId = createChannelIfNeeded()
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("VitaAlert")
            .setContentText("Monitoreo activo")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setOngoing(true)
            .build()
    }

    private fun createChannelIfNeeded(): String {
        val channelId = "vitaalert_monitor"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)
            val channel = NotificationChannel(
                channelId,
                "VitaAlert Monitoring",
                NotificationManager.IMPORTANCE_LOW
            )
            manager.createNotificationChannel(channel)
        }
        return channelId
    }
}

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
    val source: VitalSource
)

private const val NOTIFICATION_ID = 2001
