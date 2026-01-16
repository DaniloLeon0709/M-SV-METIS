package com.vitaalert.mobile.dev

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.vitaalert.mobile.dev.work.TelemetryUploadWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit

/**
 * Application class that initializes Hilt and background work.
 */
@HiltAndroidApp
class VitaAlertApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val request = PeriodicWorkRequestBuilder<TelemetryUploadWorker>(12, TimeUnit.HOURS)
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "telemetry_upload",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
}
