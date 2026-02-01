package com.vitaalert.domain.model

/**
 * Contains medical threshold definitions and validation logic for vital signs.
 * 
 * Thresholds are based on standard medical guidelines:
 * - HR: normal 60-100 bpm; alert 50-59 or 101-120; critical <50 or >120
 * - SpO2: normal 95-100%; alert 90-94%; critical <90%
 * - Temp: normal 36.0-37.4°C; alert 35.5-35.9 or 37.5-38.4; critical <35.5 or >=38.5
 * - BP: normal sys 90-120 dia 60-80; alert sys 121-139 or dia 81-89 or sys 80-89; critical sys >=140 or dia >=90 or sys <80
 */
object VitalThresholds {
    
    /**
     * Evaluates a vital reading and determines its state based on medical thresholds.
     *
     * @param reading The vital reading to evaluate
     * @return The assessed VitalState (NORMAL, ALERT, CRITICAL, or UNKNOWN)
     */
    fun evaluateState(reading: VitalReading): VitalState {
        // If quality is too poor, mark as unknown
        if (reading.quality < 0.5) {
            return VitalState.UNKNOWN
        }
        
        return when (reading.type) {
            VitalType.HEART_RATE -> evaluateHeartRate(reading.value)
            VitalType.SPO2 -> evaluateSpO2(reading.value)
            VitalType.TEMPERATURE -> evaluateTemperature(reading.value)
            VitalType.BLOOD_PRESSURE -> evaluateBloodPressure(
                reading.systolicValue ?: 0.0,
                reading.diastolicValue ?: 0.0
            )
        }
    }
    
    /**
     * Evaluates heart rate reading.
     * Normal: 60-100 bpm
     * Alert: 50-59 or 101-120 bpm
     * Critical: <50 or >120 bpm
     */
    private fun evaluateHeartRate(value: Double): VitalState {
        return when {
            value < 50.0 || value > 120.0 -> VitalState.CRITICAL
            value in 50.0..59.0 || value in 101.0..120.0 -> VitalState.ALERT
            value in 60.0..100.0 -> VitalState.NORMAL
            else -> VitalState.UNKNOWN
        }
    }
    
    /**
     * Evaluates SpO2 (blood oxygen saturation) reading.
     * Normal: 95-100%
     * Alert: 90-94%
     * Critical: <90%
     */
    private fun evaluateSpO2(value: Double): VitalState {
        return when {
            value < 90.0 -> VitalState.CRITICAL
            value in 90.0..94.0 -> VitalState.ALERT
            value in 95.0..100.0 -> VitalState.NORMAL
            else -> VitalState.UNKNOWN
        }
    }
    
    /**
     * Evaluates body temperature reading.
     * Normal: 36.0-37.4°C
     * Alert: 35.5-35.9 or 37.5-38.4°C
     * Critical: <35.5 or >=38.5°C
     */
    private fun evaluateTemperature(value: Double): VitalState {
        return when {
            value < 35.5 || value >= 38.5 -> VitalState.CRITICAL
            value in 35.5..35.9 || value in 37.5..38.4 -> VitalState.ALERT
            value in 36.0..37.4 -> VitalState.NORMAL
            else -> VitalState.UNKNOWN
        }
    }
    
    /**
     * Evaluates blood pressure reading.
     * Normal: sys 90-120, dia 60-80 mmHg
     * Alert: sys 121-139 or dia 81-89 or sys 80-89 mmHg
     * Critical: sys >=140 or dia >=90 or sys <80 mmHg
     */
    private fun evaluateBloodPressure(systolic: Double, diastolic: Double): VitalState {
        return when {
            // Critical conditions
            systolic >= 140.0 || diastolic >= 90.0 || systolic < 80.0 -> VitalState.CRITICAL
            // Alert conditions
            systolic in 121.0..139.0 || diastolic in 81.0..89.0 || systolic in 80.0..89.0 -> VitalState.ALERT
            // Normal conditions
            systolic in 90.0..120.0 && diastolic in 60.0..80.0 -> VitalState.NORMAL
            else -> VitalState.UNKNOWN
        }
    }
    
    /**
     * Creates a new VitalReading with the state evaluated based on thresholds.
     *
     * @param reading The original reading
     * @return A new reading with the state field updated
     */
    fun withEvaluatedState(reading: VitalReading): VitalReading {
        return reading.copy(state = evaluateState(reading))
    }
}
