package com.vitaalert.domain.repository

import com.vitaalert.domain.model.VitalReading
import com.vitaalert.domain.model.VitalType
import java.time.Instant
import kotlinx.coroutines.flow.Flow

/**
 * Repository for accessing vital readings.
 */
interface VitalRepository {
    /**
     * Inserts a new [reading].
     */
    suspend fun insert(reading: VitalReading)

    /**
     * Observes the latest reading for a given [type].
     */
    fun observeLatestByType(type: VitalType): Flow<VitalReading?>

    /**
     * Observes readings for [type] within the given time range.
     */
    fun observeRangeByType(type: VitalType, from: Instant, to: Instant): Flow<List<VitalReading>>

    /**
     * Deletes readings older than the retention window.
     */
    suspend fun cleanupOldReadings()
}
