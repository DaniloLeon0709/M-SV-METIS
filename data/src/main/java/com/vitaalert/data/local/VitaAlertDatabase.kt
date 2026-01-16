package com.vitaalert.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * Room database for VitaAlert.
 */
@Database(
    entities = [
        HeartRateEntity::class,
        Spo2Entity::class,
        TemperatureEntity::class,
        BloodPressureEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(InstantConverters::class)
abstract class VitaAlertDatabase : RoomDatabase() {
    /**
     * DAO for sensor tables.
     */
    abstract fun sensorDao(): SensorDao
}
