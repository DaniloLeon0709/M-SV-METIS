package com.vitaalert.mobile.dev.monitor

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.vitaalert.ble.BleMonitor
import com.vitaalert.mobile.dev.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Foreground service that keeps BLE monitoring alive.
 */
@AndroidEntryPoint
class MonitoringService : Service() {
    @Inject
    lateinit var bleMonitor: BleMonitor

    override fun onCreate() {
        super.onCreate()
        startForeground(NOTIFICATION_ID, createNotification())
        bleMonitor.startMonitoring()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        bleMonitor.stopMonitoring()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotification(): Notification {
        val channelId = createChannelIfNeeded()
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("VitaAlert")
            .setContentText("Monitoreo activo")
            .setSmallIcon(R.drawable.ic_notification)
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

    private companion object {
        const val NOTIFICATION_ID = 1001
    }
}
