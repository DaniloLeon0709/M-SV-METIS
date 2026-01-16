package com.vitaalert.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * DAO for sensor data.
 */
@Dao
interface SensorDao {
    /**
     * Inserts a heart rate record.
     */
    @Insert
    suspend fun insertHeartRate(sample: HeartRateEntity)

    /**
     * Inserts a SpO2 record.
     */
    @Insert
    suspend fun insertSpo2(sample: Spo2Entity)

    /**
     * Inserts a temperature record.
     */
    @Insert
    suspend fun insertTemperature(sample: TemperatureEntity)

    /**
     * Inserts a blood pressure record.
     */
    @Insert
    suspend fun insertBloodPressure(sample: BloodPressureEntity)

    /**
     * Emits the latest heart rate entity.
     */
    @Query("SELECT * FROM heart_rate_samples ORDER BY recordedAt DESC LIMIT 1")
    fun latestHeartRate(): Flow<HeartRateEntity?>

    /**
     * Emits the latest SpO2 entity.
     */
    @Query("SELECT * FROM spo2_samples ORDER BY recordedAt DESC LIMIT 1")
    fun latestSpo2(): Flow<Spo2Entity?>

    /**
     * Emits the latest temperature entity.
     */
    @Query("SELECT * FROM temperature_samples ORDER BY recordedAt DESC LIMIT 1")
    fun latestTemperature(): Flow<TemperatureEntity?>

    /**
     * Emits the latest blood pressure entity.
     */
    @Query("SELECT * FROM blood_pressure_samples ORDER BY recordedAt DESC LIMIT 1")
    fun latestBloodPressure(): Flow<BloodPressureEntity?>

    /**
     * Purges old heart rate records.
     */
    @Query("DELETE FROM heart_rate_samples WHERE recordedAt < :threshold")
    suspend fun purgeHeartRate(threshold: String)

    /**
     * Purges old SpO2 records.
     */
    @Query("DELETE FROM spo2_samples WHERE recordedAt < :threshold")
    suspend fun purgeSpo2(threshold: String)

    /**
     * Purges old temperature records.
     */
    @Query("DELETE FROM temperature_samples WHERE recordedAt < :threshold")
    suspend fun purgeTemperature(threshold: String)

    /**
     * Purges old blood pressure records.
     */
    @Query("DELETE FROM blood_pressure_samples WHERE recordedAt < :threshold")
    suspend fun purgeBloodPressure(threshold: String)
}
