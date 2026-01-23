package com.vitaalert.mobile.dev.service

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.vitaalert.ble.service.MonitoringService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Starts the BLE monitoring foreground service.
 */
class MonitoringServiceStarter @Inject constructor(
    @ApplicationContext private val context: Context
) {
    /**
     * Starts the monitoring service.
     */
    fun start() {
        val intent = Intent(context, MonitoringService::class.java)
        ContextCompat.startForegroundService(context, intent)
    }
}
