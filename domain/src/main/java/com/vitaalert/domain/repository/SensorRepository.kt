package com.vitaalert.domain.repository

import com.vitaalert.domain.model.BloodPressureSample
import com.vitaalert.domain.model.HeartRateSample
import com.vitaalert.domain.model.Spo2Sample
import com.vitaalert.domain.model.TemperatureSample
import kotlinx.coroutines.flow.Flow

/**
 * Repository for sensor measurements.
 */
interface SensorRepository {
    /**
     * Saves a heart rate sample.
     */
    suspend fun saveHeartRate(sample: HeartRateSample)

    /**
     * Saves a SpO2 sample.
     */
    suspend fun saveSpo2(sample: Spo2Sample)

    /**
     * Saves a temperature sample.
     */
    suspend fun saveTemperature(sample: TemperatureSample)

    /**
     * Saves a blood pressure sample.
     */
    suspend fun saveBloodPressure(sample: BloodPressureSample)

    /**
     * Emits a summary text of the latest sensor readings.
     */
    fun latestSummary(): Flow<String>

    /**
     * Purges older samples for maintenance.
     */
    suspend fun purgeOldSamples()
}
