package com.vitaalert.ble.service

import com.vitaalert.ble.demo.DemoVitalSource
import com.vitaalert.domain.model.VitalReading
import kotlinx.coroutines.flow.Flow

/**
 * Adapter exposing demo vital readings as a [VitalSource].
 */
class DemoVitalSourceAdapter(
    private val demoVitalSource: DemoVitalSource
) : VitalSource {
    override fun readings(): Flow<VitalReading> = demoVitalSource.readings()
}
