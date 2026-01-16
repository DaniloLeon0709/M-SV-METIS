package com.vitaalert.mobile.dev

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.vitaalert.mobile.dev.service.BleServiceConnector
import com.vitaalert.mobile.dev.worker.CleanupWorker
import com.vitaalert.mobile.dev.worker.SyncWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Application entry point for VitaAlert.
 */
@HiltAndroidApp
class VitaAlertApp : Application() {
    @Inject
    lateinit var bleServiceConnector: BleServiceConnector

    override fun onCreate() {
        super.onCreate()
        bleServiceConnector.initialize()
        if (BuildConfig.DEBUG) {
            scheduleWorkers()
        }
    }

    private fun scheduleWorkers() {
        val workManager = WorkManager.getInstance(this)
        val cleanupRequest = PeriodicWorkRequestBuilder<CleanupWorker>(1, TimeUnit.DAYS).build()
        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(30, TimeUnit.MINUTES).build()
        workManager.enqueueUniquePeriodicWork(
            CLEANUP_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            cleanupRequest
        )
        workManager.enqueueUniquePeriodicWork(
            SYNC_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }

    private companion object {
        const val CLEANUP_WORK_NAME = \"cleanup_vitals\"
        const val SYNC_WORK_NAME = \"sync_pending_uploads\"
    }
}
