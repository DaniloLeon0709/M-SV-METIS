package com.vitaalert.domain.model

/**
 * Represents the state of a vital sign reading based on medical thresholds.
 */
enum class VitalState {
    /** Reading is within normal/healthy range */
    NORMAL,
    
    /** Reading is outside normal range but not critical - requires attention */
    ALERT,
    
    /** Reading is in critical range - requires immediate attention */
    CRITICAL,
    
    /** Reading quality is poor or state cannot be determined */
    UNKNOWN
}
