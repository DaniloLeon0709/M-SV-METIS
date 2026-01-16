package com.vitaalert.domain.service

import com.vitaalert.domain.model.VitalState
import com.vitaalert.domain.model.VitalType

/**
 * Evaluates vital readings into their corresponding [VitalState].
 */
class VitalThresholds {
    /**
     * Resolves a [VitalState] for a given [type] and numeric [value].
     */
    fun resolve(type: VitalType, value: Double): VitalState {
        return when (type) {
            VitalType.HR -> resolveHeartRate(value)
            VitalType.SPO2 -> resolveSpo2(value)
            VitalType.TEMP -> resolveTemperature(value)
            VitalType.BP_SYS -> resolveBloodPressureSystolic(value)
            VitalType.BP_DIA -> resolveBloodPressureDiastolic(value)
        }
    }

    private fun resolveHeartRate(value: Double): VitalState = when {
        value < 50.0 -> VitalState.CRITICAL
        value in 50.0..59.0 -> VitalState.ALERT
        value in 60.0..100.0 -> VitalState.NORMAL
        value in 101.0..120.0 -> VitalState.ALERT
        else -> VitalState.CRITICAL
    }

    private fun resolveSpo2(value: Double): VitalState = when {
        value < 90.0 -> VitalState.CRITICAL
        value in 90.0..94.0 -> VitalState.ALERT
        value in 95.0..100.0 -> VitalState.NORMAL
        else -> VitalState.CRITICAL
    }

    private fun resolveTemperature(value: Double): VitalState = when {
        value < 35.5 || value >= 38.5 -> VitalState.CRITICAL
        value in 35.5..35.9 -> VitalState.ALERT
        value in 36.0..37.4 -> VitalState.NORMAL
        value in 37.5..38.4 -> VitalState.ALERT
        else -> VitalState.NORMAL
    }

    private fun resolveBloodPressureSystolic(value: Double): VitalState = when {
        value < 80.0 -> VitalState.CRITICAL
        value in 80.0..89.0 -> VitalState.ALERT
        value in 90.0..120.0 -> VitalState.NORMAL
        value in 121.0..139.0 -> VitalState.ALERT
        else -> VitalState.CRITICAL
    }

    private fun resolveBloodPressureDiastolic(value: Double): VitalState = when {
        value < 60.0 -> VitalState.ALERT
        value in 60.0..80.0 -> VitalState.NORMAL
        value in 81.0..89.0 -> VitalState.ALERT
        else -> VitalState.CRITICAL
    }
}
