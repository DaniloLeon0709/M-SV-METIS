package com.vitaalert.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * Room database for VitaAlert persistence.
 */
@Database(
    entities = [
        VitalReadingEntity::class,
        DeviceEntity::class,
        PendingUploadEntity::class,
        DeviceProfileEntity::class,
        UserProfileEntity::class
    ],
    version = 3,
    exportSchema = true
)
@TypeConverters(RoomConverters::class)
abstract class VitaAlertDatabase : RoomDatabase() {
    /**
     * Provides the DAO for vital readings.
     */
    abstract fun vitalReadingDao(): VitalReadingDao

    /**
     * Provides the DAO for pending uploads.
     */
    abstract fun pendingUploadDao(): PendingUploadDao

    /**
     * Provides the DAO for user profile.
     */
    abstract fun userProfileDao(): UserProfileDao
}
