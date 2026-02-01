package com.vitaalert.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * DAO for pending uploads.
 */
@Dao
interface PendingUploadDao {
    /**
     * Inserts a pending upload entry.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(upload: PendingUploadEntity)

    /**
     * Marks all pending uploads as sent.
     */
    @Query("UPDATE pending_uploads SET sent = 1 WHERE sent = 0")
    suspend fun markAllSent()
}
