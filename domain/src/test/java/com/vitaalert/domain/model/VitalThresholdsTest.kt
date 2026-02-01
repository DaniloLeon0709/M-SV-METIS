package com.vitaalert.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for VitalThresholds evaluation logic.
 * Tests all threshold ranges for each vital sign type.
 */
class VitalThresholdsTest {
    
    @Test
    fun `heart rate normal range returns NORMAL`() {
        val readings = listOf(60.0, 80.0, 100.0).map { hr ->
            VitalReading(
                type = VitalType.HEART_RATE,
                value = hr,
                unit = "bpm",
                timestamp = System.currentTimeMillis(),
                quality = 1.0
            )
        }
        
        readings.forEach { reading ->
            assertEquals(
                "HR ${ reading.value} should be NORMAL",
                VitalState.NORMAL,
                VitalThresholds.evaluateState(reading)
            )
        }
    }
    
    @Test
    fun `heart rate alert range returns ALERT`() {
        val readings = listOf(50.0, 55.0, 59.0, 101.0, 110.0, 120.0).map { hr ->
            VitalReading(
                type = VitalType.HEART_RATE,
                value = hr,
                unit = "bpm",
                timestamp = System.currentTimeMillis(),
                quality = 1.0
            )
        }
        
        readings.forEach { reading ->
            assertEquals(
                "HR ${reading.value} should be ALERT",
                VitalState.ALERT,
                VitalThresholds.evaluateState(reading)
            )
        }
    }
    
    @Test
    fun `heart rate critical range returns CRITICAL`() {
        val readings = listOf(45.0, 49.0, 121.0, 150.0).map { hr ->
            VitalReading(
                type = VitalType.HEART_RATE,
                value = hr,
                unit = "bpm",
                timestamp = System.currentTimeMillis(),
                quality = 1.0
            )
        }
        
        readings.forEach { reading ->
            assertEquals(
                "HR ${reading.value} should be CRITICAL",
                VitalState.CRITICAL,
                VitalThresholds.evaluateState(reading)
            )
        }
    }
    
    @Test
    fun `SpO2 normal range returns NORMAL`() {
        val readings = listOf(95.0, 97.0, 100.0).map { spo2 ->
            VitalReading(
                type = VitalType.SPO2,
                value = spo2,
                unit = "%",
                timestamp = System.currentTimeMillis(),
                quality = 1.0
            )
        }
        
        readings.forEach { reading ->
            assertEquals(
                "SpO2 ${reading.value} should be NORMAL",
                VitalState.NORMAL,
                VitalThresholds.evaluateState(reading)
            )
        }
    }
    
    @Test
    fun `SpO2 alert range returns ALERT`() {
        val readings = listOf(90.0, 92.0, 94.0).map { spo2 ->
            VitalReading(
                type = VitalType.SPO2,
                value = spo2,
                unit = "%",
                timestamp = System.currentTimeMillis(),
                quality = 1.0
            )
        }
        
        readings.forEach { reading ->
            assertEquals(
                "SpO2 ${reading.value} should be ALERT",
                VitalState.ALERT,
                VitalThresholds.evaluateState(reading)
            )
        }
    }
    
    @Test
    fun `SpO2 critical range returns CRITICAL`() {
        val readings = listOf(85.0, 89.0).map { spo2 ->
            VitalReading(
                type = VitalType.SPO2,
                value = spo2,
                unit = "%",
                timestamp = System.currentTimeMillis(),
                quality = 1.0
            )
        }
        
        readings.forEach { reading ->
            assertEquals(
                "SpO2 ${reading.value} should be CRITICAL",
                VitalState.CRITICAL,
                VitalThresholds.evaluateState(reading)
            )
        }
    }
    
    @Test
    fun `temperature normal range returns NORMAL`() {
        val readings = listOf(36.0, 36.5, 37.0, 37.4).map { temp ->
            VitalReading(
                type = VitalType.TEMPERATURE,
                value = temp,
                unit = "°C",
                timestamp = System.currentTimeMillis(),
                quality = 1.0
            )
        }
        
        readings.forEach { reading ->
            assertEquals(
                "Temperature ${reading.value} should be NORMAL",
                VitalState.NORMAL,
                VitalThresholds.evaluateState(reading)
            )
        }
    }
    
    @Test
    fun `temperature alert range returns ALERT`() {
        val readings = listOf(35.5, 35.8, 37.5, 38.0, 38.4).map { temp ->
            VitalReading(
                type = VitalType.TEMPERATURE,
                value = temp,
                unit = "°C",
                timestamp = System.currentTimeMillis(),
                quality = 1.0
            )
        }
        
        readings.forEach { reading ->
            assertEquals(
                "Temperature ${reading.value} should be ALERT",
                VitalState.ALERT,
                VitalThresholds.evaluateState(reading)
            )
        }
    }
    
    @Test
    fun `temperature critical range returns CRITICAL`() {
        val readings = listOf(35.0, 35.4, 38.5, 39.0).map { temp ->
            VitalReading(
                type = VitalType.TEMPERATURE,
                value = temp,
                unit = "°C",
                timestamp = System.currentTimeMillis(),
                quality = 1.0
            )
        }
        
        readings.forEach { reading ->
            assertEquals(
                "Temperature ${reading.value} should be CRITICAL",
                VitalState.CRITICAL,
                VitalThresholds.evaluateState(reading)
            )
        }
    }
    
    @Test
    fun `blood pressure normal range returns NORMAL`() {
        val reading = VitalReading(
            type = VitalType.BLOOD_PRESSURE,
            value = 0.0, // Not used for BP
            unit = "mmHg",
            timestamp = System.currentTimeMillis(),
            quality = 1.0,
            systolicValue = 110.0,
            diastolicValue = 70.0
        )
        
        assertEquals(VitalState.NORMAL, VitalThresholds.evaluateState(reading))
    }
    
    @Test
    fun `blood pressure alert range returns ALERT`() {
        val readings = listOf(
            Triple(125.0, 70.0, "elevated systolic"),
            Triple(110.0, 85.0, "elevated diastolic"),
            Triple(85.0, 70.0, "low systolic")
        ).map { (sys, dia, desc) ->
            VitalReading(
                type = VitalType.BLOOD_PRESSURE,
                value = 0.0,
                unit = "mmHg",
                timestamp = System.currentTimeMillis(),
                quality = 1.0,
                systolicValue = sys,
                diastolicValue = dia
            ) to desc
        }
        
        readings.forEach { (reading, desc) ->
            assertEquals(
                "BP $desc should be ALERT",
                VitalState.ALERT,
                VitalThresholds.evaluateState(reading)
            )
        }
    }
    
    @Test
    fun `blood pressure critical range returns CRITICAL`() {
        val readings = listOf(
            Triple(150.0, 70.0, "high systolic"),
            Triple(110.0, 95.0, "high diastolic"),
            Triple(75.0, 70.0, "very low systolic")
        ).map { (sys, dia, desc) ->
            VitalReading(
                type = VitalType.BLOOD_PRESSURE,
                value = 0.0,
                unit = "mmHg",
                timestamp = System.currentTimeMillis(),
                quality = 1.0,
                systolicValue = sys,
                diastolicValue = dia
            ) to desc
        }
        
        readings.forEach { (reading, desc) ->
            assertEquals(
                "BP $desc should be CRITICAL",
                VitalState.CRITICAL,
                VitalThresholds.evaluateState(reading)
            )
        }
    }
    
    @Test
    fun `poor quality reading returns UNKNOWN`() {
        val reading = VitalReading(
            type = VitalType.HEART_RATE,
            value = 75.0,
            unit = "bpm",
            timestamp = System.currentTimeMillis(),
            quality = 0.3 // Poor quality
        )
        
        assertEquals(VitalState.UNKNOWN, VitalThresholds.evaluateState(reading))
    }
    
    @Test
    fun `withEvaluatedState updates reading state`() {
        val reading = VitalReading(
            type = VitalType.HEART_RATE,
            value = 75.0,
            unit = "bpm",
            timestamp = System.currentTimeMillis(),
            quality = 1.0,
            state = VitalState.UNKNOWN
        )
        
        val evaluated = VitalThresholds.withEvaluatedState(reading)
        
        assertEquals(VitalState.NORMAL, evaluated.state)
        assertEquals(75.0, evaluated.value, 0.01)
    }
}
