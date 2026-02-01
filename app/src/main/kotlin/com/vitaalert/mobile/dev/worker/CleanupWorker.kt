package com.vitaalert.mobile.dev.worker

import android.content.Context
import androidx.room.Room
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.vitaalert.data.local.VitaAlertDatabase
import java.time.Instant
import java.time.temporal.ChronoUnit

/**
 * Worker that cleans up vital readings older than 30 days.
 */
class CleanupWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val database = Room.databaseBuilder(
            applicationContext,
            VitaAlertDatabase::class.java,
            DATABASE_NAME
        ).build()
        return try {
            val threshold = Instant.now().minus(30, ChronoUnit.DAYS)
            database.vitalReadingDao().deleteOlderThan(threshold)
            Result.success()
        } finally {
            database.close()
        }
    }

    private companion object {
        const val DATABASE_NAME = "vitaalert.db"
    }
}
