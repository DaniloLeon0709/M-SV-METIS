package com.vitaalert.data.repository

import com.vitaalert.data.local.SensorDao
import com.vitaalert.domain.model.BloodPressureSample
import com.vitaalert.domain.model.HeartRateSample
import com.vitaalert.domain.model.Spo2Sample
import com.vitaalert.domain.model.TemperatureSample
import com.vitaalert.domain.repository.SensorRepository
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * Repository backed by Room for sensor data.
 */
class RoomSensorRepository @Inject constructor(
    private val sensorDao: SensorDao
) : SensorRepository {
    override suspend fun saveHeartRate(sample: HeartRateSample) {
        sensorDao.insertHeartRate(sample.toEntity())
    }

    override suspend fun saveSpo2(sample: Spo2Sample) {
        sensorDao.insertSpo2(sample.toEntity())
    }

    override suspend fun saveTemperature(sample: TemperatureSample) {
        sensorDao.insertTemperature(sample.toEntity())
    }

    override suspend fun saveBloodPressure(sample: BloodPressureSample) {
        sensorDao.insertBloodPressure(sample.toEntity())
    }

    override fun latestSummary(): Flow<String> {
        return combine(
            sensorDao.latestHeartRate(),
            sensorDao.latestSpo2(),
            sensorDao.latestTemperature(),
            sensorDao.latestBloodPressure()
        ) { hr, spo2, temp, bp ->
            val hrText = hr?.bpm?.let { "HR ${it}bpm" } ?: "HR --"
            val spo2Text = spo2?.percentage?.let { "SpO2 ${it}%" } ?: "SpO2 --"
            val tempText = temp?.celsius?.let { "Temp ${"%.1f".format(it)}°C" } ?: "Temp --"
            val bpText = bp?.let { "BP ${it.systolic}/${it.diastolic}" } ?: "BP --"
            "$hrText · $spo2Text · $tempText · $bpText"
        }
    }

    override suspend fun purgeOldSamples() {
        val threshold = Instant.now().minus(1, ChronoUnit.DAYS).toString()
        sensorDao.purgeHeartRate(threshold)
        sensorDao.purgeSpo2(threshold)
        sensorDao.purgeTemperature(threshold)
        sensorDao.purgeBloodPressure(threshold)
    }
}

private fun HeartRateSample.toEntity() = com.vitaalert.data.local.HeartRateEntity(
    bpm = bpm,
    recordedAt = recordedAt
)

private fun Spo2Sample.toEntity() = com.vitaalert.data.local.Spo2Entity(
    percentage = percentage,
    recordedAt = recordedAt
)

private fun TemperatureSample.toEntity() = com.vitaalert.data.local.TemperatureEntity(
    celsius = celsius,
    recordedAt = recordedAt
)

private fun BloodPressureSample.toEntity() = com.vitaalert.data.local.BloodPressureEntity(
    systolic = systolic,
    diastolic = diastolic,
    recordedAt = recordedAt
)
