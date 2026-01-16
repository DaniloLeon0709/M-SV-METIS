package com.vitaalert.domain.model

import java.time.Instant

/**
 * Supported vital reading types.
 */
enum class VitalType {
    HR,
    SPO2,
    TEMP,
    BP_SYS,
    BP_DIA
}

/**
 * Quality assessment for a vital reading.
 */
enum class VitalQuality {
    VALID,
    POOR_SIGNAL,
    OUT_OF_RANGE,
    DEVICE_ERROR,
    UNKNOWN
}

/**
 * Interpreted state of a vital reading.
 */
enum class VitalState {
    NORMAL,
    ALERT,
    CRITICAL
}

/**
 * Represents a single vital reading captured during monitoring.
 */
data class VitalReading(
    val id: String,
    val type: VitalType,
    val value: Double,
    val unit: String,
    val timestamp: Instant,
    val quality: VitalQuality,
    val sourceDeviceId: String,
    val sessionId: String?
)
