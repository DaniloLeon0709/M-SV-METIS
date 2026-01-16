package com.vitaalert.ble.service

import com.vitaalert.domain.model.VitalReading
import kotlinx.coroutines.flow.Flow

/**
 * Source of vital readings for monitoring.
 */
interface VitalSource {
    /**
     * Returns a flow of vital readings.
     */
    fun readings(): Flow<VitalReading>
}
