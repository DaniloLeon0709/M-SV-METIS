package com.vitaalert.data.repository

import com.vitaalert.data.local.VitalReadingDao
import com.vitaalert.data.mapper.toDomain
import com.vitaalert.data.mapper.toEntity
import com.vitaalert.domain.model.VitalReading
import com.vitaalert.domain.model.VitalType
import com.vitaalert.domain.repository.VitalRepository
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Room-backed implementation of [VitalRepository].
 */
class VitalRepositoryImpl(
    private val vitalReadingDao: VitalReadingDao
) : VitalRepository {
    override suspend fun insert(reading: VitalReading) {
        vitalReadingDao.insert(reading.toEntity())
    }

    override fun observeLatestByType(type: VitalType): Flow<VitalReading?> {
        return vitalReadingDao.observeLatestByType(type).map { entity -> entity?.toDomain() }
    }

    override fun observeRangeByType(
        type: VitalType,
        from: Instant,
        to: Instant
    ): Flow<List<VitalReading>> {
        return vitalReadingDao.observeRangeByType(type, from, to).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun cleanupOldReadings() {
        val threshold = Instant.now().minus(30, ChronoUnit.DAYS)
        vitalReadingDao.deleteOlderThan(threshold)
    }
}
