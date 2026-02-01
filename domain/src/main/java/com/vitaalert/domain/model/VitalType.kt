package com.vitaalert.domain.model

/**
 * Represents the different types of vital signs that can be monitored.
 */
enum class VitalType {
    /** Heart Rate in beats per minute (bpm) */
    HEART_RATE,
    
    /** Blood Oxygen Saturation as percentage (%) */
    SPO2,
    
    /** Body Temperature in Celsius (°C) */
    TEMPERATURE,
    
    /** Blood Pressure with systolic and diastolic values in mmHg */
    BLOOD_PRESSURE
}
