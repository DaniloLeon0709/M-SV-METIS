package com.vitaalert.domain.service

import com.vitaalert.domain.model.VitalState
import com.vitaalert.domain.model.VitalType
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Unit tests for [VitalThresholds].
 */
class VitalThresholdsTest {
    private val thresholds = VitalThresholds()

    @Test
    fun `heart rate thresholds`() {
        assertEquals(VitalState.CRITICAL, thresholds.resolve(VitalType.HR, 49.0))
        assertEquals(VitalState.ALERT, thresholds.resolve(VitalType.HR, 50.0))
        assertEquals(VitalState.NORMAL, thresholds.resolve(VitalType.HR, 60.0))
        assertEquals(VitalState.NORMAL, thresholds.resolve(VitalType.HR, 100.0))
        assertEquals(VitalState.ALERT, thresholds.resolve(VitalType.HR, 101.0))
        assertEquals(VitalState.CRITICAL, thresholds.resolve(VitalType.HR, 121.0))
    }

    @Test
    fun `spo2 thresholds`() {
        assertEquals(VitalState.CRITICAL, thresholds.resolve(VitalType.SPO2, 89.0))
        assertEquals(VitalState.ALERT, thresholds.resolve(VitalType.SPO2, 90.0))
        assertEquals(VitalState.ALERT, thresholds.resolve(VitalType.SPO2, 94.0))
        assertEquals(VitalState.NORMAL, thresholds.resolve(VitalType.SPO2, 95.0))
        assertEquals(VitalState.NORMAL, thresholds.resolve(VitalType.SPO2, 100.0))
        assertEquals(VitalState.CRITICAL, thresholds.resolve(VitalType.SPO2, 101.0))
    }

    @Test
    fun `temperature thresholds`() {
        assertEquals(VitalState.CRITICAL, thresholds.resolve(VitalType.TEMP, 35.4))
        assertEquals(VitalState.ALERT, thresholds.resolve(VitalType.TEMP, 35.5))
        assertEquals(VitalState.NORMAL, thresholds.resolve(VitalType.TEMP, 36.0))
        assertEquals(VitalState.NORMAL, thresholds.resolve(VitalType.TEMP, 37.4))
        assertEquals(VitalState.ALERT, thresholds.resolve(VitalType.TEMP, 37.5))
        assertEquals(VitalState.ALERT, thresholds.resolve(VitalType.TEMP, 38.4))
        assertEquals(VitalState.CRITICAL, thresholds.resolve(VitalType.TEMP, 38.5))
    }

    @Test
    fun `blood pressure systolic thresholds`() {
        assertEquals(VitalState.CRITICAL, thresholds.resolve(VitalType.BP_SYS, 79.0))
        assertEquals(VitalState.ALERT, thresholds.resolve(VitalType.BP_SYS, 80.0))
        assertEquals(VitalState.NORMAL, thresholds.resolve(VitalType.BP_SYS, 90.0))
        assertEquals(VitalState.NORMAL, thresholds.resolve(VitalType.BP_SYS, 120.0))
        assertEquals(VitalState.ALERT, thresholds.resolve(VitalType.BP_SYS, 121.0))
        assertEquals(VitalState.CRITICAL, thresholds.resolve(VitalType.BP_SYS, 140.0))
    }

    @Test
    fun `blood pressure diastolic thresholds`() {
        assertEquals(VitalState.ALERT, thresholds.resolve(VitalType.BP_DIA, 59.0))
        assertEquals(VitalState.NORMAL, thresholds.resolve(VitalType.BP_DIA, 60.0))
        assertEquals(VitalState.NORMAL, thresholds.resolve(VitalType.BP_DIA, 80.0))
        assertEquals(VitalState.ALERT, thresholds.resolve(VitalType.BP_DIA, 81.0))
        assertEquals(VitalState.ALERT, thresholds.resolve(VitalType.BP_DIA, 89.0))
        assertEquals(VitalState.CRITICAL, thresholds.resolve(VitalType.BP_DIA, 90.0))
    }
}
