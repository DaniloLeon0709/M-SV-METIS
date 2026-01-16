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
        DeviceProfileEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(RoomConverters::class)
abstract class VitaAlertDatabase : RoomDatabase() {
    /**
     * Provides the DAO for vital readings.
     */
    abstract fun vitalReadingDao(): VitalReadingDao
}
