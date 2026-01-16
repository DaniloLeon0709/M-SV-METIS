package com.vitaalert.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

/**
 * Room entity for heart rate measurements.
 */
@Entity(tableName = "heart_rate_samples")
data class HeartRateEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bpm: Int,
    val recordedAt: Instant
)

/**
 * Room entity for SpO2 measurements.
 */
@Entity(tableName = "spo2_samples")
data class Spo2Entity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val percentage: Int,
    val recordedAt: Instant
)

/**
 * Room entity for temperature measurements.
 */
@Entity(tableName = "temperature_samples")
data class TemperatureEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val celsius: Double,
    val recordedAt: Instant
)

/**
 * Room entity for blood pressure measurements.
 */
@Entity(tableName = "blood_pressure_samples")
data class BloodPressureEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val systolic: Int,
    val diastolic: Int,
    val recordedAt: Instant
)
