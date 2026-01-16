package com.vitaalert.domain.model

import java.time.Instant

/**
 * Heart rate sample.
 */
data class HeartRateSample(
    val bpm: Int,
    val recordedAt: Instant
)

/**
 * SpO2 sample.
 */
data class Spo2Sample(
    val percentage: Int,
    val recordedAt: Instant
)

/**
 * Temperature sample.
 */
data class TemperatureSample(
    val celsius: Double,
    val recordedAt: Instant
)

/**
 * Blood pressure sample.
 */
data class BloodPressureSample(
    val systolic: Int,
    val diastolic: Int,
    val recordedAt: Instant
)
