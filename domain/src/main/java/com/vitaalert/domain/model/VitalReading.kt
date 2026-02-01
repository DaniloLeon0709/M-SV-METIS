package com.vitaalert.domain.model

/**
 * Represents a single vital sign reading with all its associated metadata.
 *
 * @property type The type of vital sign (HR, SpO2, Temperature, BP)
 * @property value The numerical value of the reading
 * @property unit The unit of measurement (bpm, %, °C, mmHg)
 * @property timestamp The time when the reading was captured (Unix timestamp in milliseconds)
 * @property quality The quality/confidence of the reading (0.0 to 1.0, where 1.0 is best)
 * @property sourceDeviceId Optional identifier of the device that captured this reading
 * @property sessionId Optional identifier for grouping readings from the same monitoring session
 * @property state The assessed state of the reading (NORMAL, ALERT, CRITICAL, UNKNOWN)
 * @property systolicValue For blood pressure readings, the systolic (higher) value
 * @property diastolicValue For blood pressure readings, the diastolic (lower) value
 */
data class VitalReading(
    val type: VitalType,
    val value: Double,
    val unit: String,
    val timestamp: Long,
    val quality: Double = 1.0,
    val sourceDeviceId: String? = null,
    val sessionId: String? = null,
    val state: VitalState = VitalState.UNKNOWN,
    // For blood pressure specifically
    val systolicValue: Double? = null,
    val diastolicValue: Double? = null
)
