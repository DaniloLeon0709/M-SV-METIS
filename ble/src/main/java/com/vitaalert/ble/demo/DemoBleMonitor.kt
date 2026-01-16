package com.vitaalert.ble.demo

import com.vitaalert.ble.BleMonitor
import com.vitaalert.core.dispatchers.DispatcherProvider
import com.vitaalert.domain.model.BloodPressureSample
import com.vitaalert.domain.model.HeartRateSample
import com.vitaalert.domain.model.Spo2Sample
import com.vitaalert.domain.model.TemperatureSample
import com.vitaalert.domain.repository.SensorRepository
import java.time.Instant
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * Demo BLE monitor that injects synthetic readings into Room.
 */
class DemoBleMonitor @Inject constructor(
    private val sensorRepository: SensorRepository,
    dispatcherProvider: DispatcherProvider
) : BleMonitor {
    private val scope = CoroutineScope(dispatcherProvider.io + SupervisorJob())
    private var demoJob: Job? = null
    private var bpJob: Job? = null
    private var tempJob: Job? = null

    override fun startMonitoring() {
        if (demoJob != null) return
        demoJob = scope.launch {
            while (isActive) {
                val now = Instant.now()
                sensorRepository.saveHeartRate(HeartRateSample(bpm = randomHr(), recordedAt = now))
                sensorRepository.saveSpo2(Spo2Sample(percentage = randomSpo2(), recordedAt = now))
                delay(1_000)
            }
        }
        tempJob = scope.launch {
            while (isActive) {
                val now = Instant.now()
                sensorRepository.saveTemperature(
                    TemperatureSample(celsius = randomTemp(), recordedAt = now)
                )
                delay(45_000)
            }
        }
        bpJob = scope.launch {
            while (isActive) {
                val now = Instant.now()
                sensorRepository.saveBloodPressure(
                    BloodPressureSample(
                        systolic = randomSystolic(),
                        diastolic = randomDiastolic(),
                        recordedAt = now
                    )
                )
                delay(120_000)
            }
        }
    }

    override fun stopMonitoring() {
        demoJob?.cancel()
        tempJob?.cancel()
        bpJob?.cancel()
        demoJob = null
        tempJob = null
        bpJob = null
    }

    private fun randomHr(): Int = Random.nextInt(60, 100)

    private fun randomSpo2(): Int = Random.nextInt(94, 100)

    private fun randomTemp(): Double = Random.nextDouble(36.0, 37.8)

    private fun randomSystolic(): Int = Random.nextInt(110, 130)

    private fun randomDiastolic(): Int = Random.nextInt(70, 85)
}
