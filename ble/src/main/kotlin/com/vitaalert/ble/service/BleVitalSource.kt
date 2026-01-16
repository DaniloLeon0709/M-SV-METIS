package com.vitaalert.ble.service

import com.vitaalert.domain.model.VitalReading
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

/**
 * Placeholder BLE vital source that emits no readings until a real GATT pipeline is wired.
 */
class BleVitalSource : VitalSource {
    override fun readings(): Flow<VitalReading> = emptyFlow()
}
