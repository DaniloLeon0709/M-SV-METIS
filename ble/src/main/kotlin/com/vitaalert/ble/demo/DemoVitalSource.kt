package com.vitaalert.ble.demo

import com.vitaalert.domain.model.VitalQuality
import com.vitaalert.domain.model.VitalReading
import com.vitaalert.domain.model.VitalType
import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * Demo generator that emits synthetic vital readings.
 */
class DemoVitalSource(
    private val dispatcher: CoroutineDispatcher
) {
    private val manualBloodPressure = MutableSharedFlow<List<VitalReading>>()

    /**
     * Returns a flow of synthetic vital readings for demo mode.
     */
    fun readings(): Flow<VitalReading> {
        return merge(
            heartRateFlow(),
            spo2Flow(),
            temperatureFlow(),
            manualBloodPressureFlow()
        )
    }

    /**
     * Emits a manual blood pressure reading pair.
     */
    suspend fun triggerBloodPressure(systolic: Int, diastolic: Int) {
        val now = Instant.now()
        val deviceId = "demo-device"
        val sessionId = UUID.randomUUID().toString()
        manualBloodPressure.emit(
            listOf(
                createReading(VitalType.BP_SYS, systolic.toDouble(), "mmHg", now, deviceId, sessionId),
                createReading(VitalType.BP_DIA, diastolic.toDouble(), "mmHg", now, deviceId, sessionId)
            )
        )
    }

    private fun heartRateFlow(): Flow<VitalReading> = flow {
        while (true) {
            emit(createReading(VitalType.HR, Random.nextInt(60, 100).toDouble(), "bpm"))
            delay(1_000)
        }
    }.flowOn(dispatcher)

    private fun spo2Flow(): Flow<VitalReading> = flow {
        while (true) {
            emit(createReading(VitalType.SPO2, Random.nextInt(94, 100).toDouble(), "%"))
            delay(1_000)
        }
    }.flowOn(dispatcher)

    private fun temperatureFlow(): Flow<VitalReading> = flow {
        while (true) {
            emit(createReading(VitalType.TEMP, Random.nextDouble(36.0, 37.8), "°C"))
            delay(45_000)
        }
    }.flowOn(dispatcher)

    private fun manualBloodPressureFlow(): Flow<VitalReading> = flow {
        manualBloodPressure.collect { list ->
            list.forEach { emit(it) }
        }
    }.flowOn(dispatcher)

    private fun createReading(
        type: VitalType,
        value: Double,
        unit: String,
        timestamp: Instant = Instant.now(),
        deviceId: String = "demo-device",
        sessionId: String? = null
    ): VitalReading = VitalReading(
        id = UUID.randomUUID().toString(),
        type = type,
        value = value,
        unit = unit,
        timestamp = timestamp,
        quality = VitalQuality.VALID,
        sourceDeviceId = deviceId,
        sessionId = sessionId
    )
}
