package com.vitaalert.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vitaalert.domain.model.VitalType
import java.time.Instant
import kotlinx.coroutines.flow.Flow

/**
 * DAO for vital readings.
 */
@Dao
interface VitalReadingDao {
    /**
     * Inserts or replaces a vital reading.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reading: VitalReadingEntity)

    /**
     * Observes the latest reading for the given [type].
     */
    @Query("SELECT * FROM vital_readings WHERE type = :type ORDER BY timestamp DESC LIMIT 1")
    fun observeLatestByType(type: VitalType): Flow<VitalReadingEntity?>

    /**
     * Observes readings for [type] within the given time range.
     */
    @Query(
        "SELECT * FROM vital_readings WHERE type = :type AND timestamp BETWEEN :from AND :to ORDER BY timestamp ASC"
    )
    fun observeRangeByType(type: VitalType, from: Instant, to: Instant): Flow<List<VitalReadingEntity>>

    /**
     * Deletes readings older than the provided [threshold].
     */
    @Query("DELETE FROM vital_readings WHERE timestamp < :threshold")
    suspend fun deleteOlderThan(threshold: Instant)
}
