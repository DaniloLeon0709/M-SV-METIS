package com.vitaalert.ble.demo

import com.vitaalert.domain.model.VitalType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest

/**
 * Unit tests for [DemoVitalSource].
 */
@OptIn(ExperimentalCoroutinesApi::class)
class DemoVitalSourceTest {
    @Test
    fun `manual blood pressure emits systolic and diastolic readings`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val demoSource = DemoVitalSource(dispatcher)

        val deferred = async {
            demoSource.readings()
                .filter { it.type == VitalType.BP_SYS || it.type == VitalType.BP_DIA }
                .take(2)
                .toList()
        }

        demoSource.triggerBloodPressure(systolic = 120, diastolic = 80)

        val readings = deferred.await()
        val types = readings.map { it.type }.sortedBy { it.name }

        assertEquals(listOf(VitalType.BP_DIA, VitalType.BP_SYS), types)
    }
}
