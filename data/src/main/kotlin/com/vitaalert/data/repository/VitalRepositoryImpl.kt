package com.vitaalert.data.repository

import android.util.Log
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
 * Room-backed implementation of [VitalRepository] with Firebase sync.
 */
class VitalRepositoryImpl(
    private val vitalReadingDao: VitalReadingDao,
    private val firebaseVitalRepository: FirebaseVitalRepository? = null
) : VitalRepository {

    companion object {
        private const val TAG = "VitalRepositoryImpl"
    }

    override suspend fun insert(reading: VitalReading) {
        // Save to local Room database
        vitalReadingDao.insert(reading.toEntity())
        Log.d(TAG, "Saved reading to Room: ${reading.type} = ${reading.value}")

        // Sync to Firebase if available
        firebaseVitalRepository?.let { firebaseRepo ->
            try {
                firebaseRepo.uploadReading(reading)
                Log.d(TAG, "Synced reading to Firebase: ${reading.type} = ${reading.value}")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to sync reading to Firebase", e)
                // Reading is saved locally, will sync later
            }
        }
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
