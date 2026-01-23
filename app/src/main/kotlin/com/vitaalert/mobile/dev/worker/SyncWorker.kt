package com.vitaalert.mobile.dev.worker

import android.content.Context
import androidx.room.Room
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.vitaalert.data.local.VitaAlertDatabase

/**
 * Worker that marks pending uploads as sent (stubbed for now).
 */
class SyncWorker(
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
            database.pendingUploadDao().markAllSent()
            Result.success()
        } finally {
            database.close()
        }
    }

    private companion object {
        const val DATABASE_NAME = "vitaalert.db"
    }
}
