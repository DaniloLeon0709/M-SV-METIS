package com.vitaalert.data.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.vitaalert.data.local.RoomConverters
import com.vitaalert.data.local.VitaAlertDatabase
import com.vitaalert.domain.model.VitalQuality
import com.vitaalert.domain.model.VitalReading
import com.vitaalert.domain.model.VitalType
import java.time.Instant
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

/**
 * Unit tests for [VitalRepositoryImpl] using in-memory Room.
 */
class VitalRepositoryImplTest {
    private lateinit var database: VitaAlertDatabase
    private lateinit var repository: VitalRepositoryImpl

    @BeforeTest
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            VitaAlertDatabase::class.java
        )
            .addTypeConverter(RoomConverters())
            .allowMainThreadQueries()
            .build()
        repository = VitalRepositoryImpl(database.vitalReadingDao())
    }

    @AfterTest
    fun tearDown() {
        database.close()
    }

    @Test
    fun `insert and observe latest reading`() = runBlocking {
        val now = Instant.parse("2024-06-01T10:00:00Z")
        val reading = VitalReading(
            id = "1",
            type = VitalType.HR,
            value = 72.0,
            unit = "bpm",
            timestamp = now,
            quality = VitalQuality.VALID,
            sourceDeviceId = "device-1",
            sessionId = null
        )

        repository.insert(reading)

        val result = repository.observeLatestByType(VitalType.HR).first()
        assertEquals(reading, result)
    }

    @Test
    fun `cleanup deletes older readings`() = runBlocking {
        val oldReading = VitalReading(
            id = "old",
            type = VitalType.SPO2,
            value = 98.0,
            unit = "%",
            timestamp = Instant.now().minusSeconds(60L * 60 * 24 * 31),
            quality = VitalQuality.VALID,
            sourceDeviceId = "device-1",
            sessionId = null
        )
        val newReading = oldReading.copy(id = "new", timestamp = Instant.now())

        repository.insert(oldReading)
        repository.insert(newReading)
        repository.cleanupOldReadings()

        val result = repository.observeRangeByType(
            VitalType.SPO2,
            Instant.now().minusSeconds(60L * 60 * 24 * 90),
            Instant.now().plusSeconds(60)
        ).first()

        assertEquals(listOf(newReading), result)
    }
}
