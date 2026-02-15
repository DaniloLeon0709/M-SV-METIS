package com.vitaalert.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.vitaalert.domain.model.VitalQuality
import com.vitaalert.domain.model.VitalReading
import com.vitaalert.domain.model.VitalType
import com.vitaalert.domain.service.VitalThresholds
import kotlinx.coroutines.tasks.await
import java.time.Instant
import javax.inject.Inject

/**
 * Repository para sincronizar lecturas vitales con Firebase Firestore.
 */
class FirebaseVitalRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val vitalThresholds: VitalThresholds
) {

    private val currentUserId: String
        get() = auth.currentUser?.uid
            ?: throw IllegalStateException("User not logged in")

    /**
     * Sube una lectura vital a Firestore.
     */
    suspend fun uploadReading(reading: VitalReading) {
        val state = vitalThresholds.resolve(reading.type, reading.value)

        val data = hashMapOf(
            "userId" to currentUserId,
            "type" to reading.type.name,
            "value" to reading.value,
            "unit" to reading.unit,
            "timestamp" to reading.timestamp.toEpochMilli(),
            "quality" to reading.quality.name,
            "status" to state.name,
            "sourceDeviceId" to reading.sourceDeviceId,
            "sessionId" to reading.sessionId,
            "synced" to true
        )

        firestore.collection("vitals")
            .document(reading.id)
            .set(data)
            .await()
    }

    /**
     * Sube múltiples lecturas en batch.
     */
    suspend fun uploadReadings(readings: List<VitalReading>) {
        if (readings.isEmpty()) return

        val batch = firestore.batch()

        readings.forEach { reading ->
            val state = vitalThresholds.resolve(reading.type, reading.value)
            val ref = firestore.collection("vitals").document(reading.id)

            val data = hashMapOf(
                "userId" to currentUserId,
                "type" to reading.type.name,
                "value" to reading.value,
                "unit" to reading.unit,
                "timestamp" to reading.timestamp.toEpochMilli(),
                "quality" to reading.quality.name,
                "status" to state.name,
                "sourceDeviceId" to reading.sourceDeviceId,
                "sessionId" to reading.sessionId,
                "synced" to true
            )

            batch.set(ref, data)
        }

        batch.commit().await()
    }

    /**
     * Obtiene las últimas lecturas del usuario desde Firestore.
     */
    suspend fun getRecentReadings(type: VitalType, limit: Int = 100): List<VitalReading> {
        val snapshot = firestore.collection("vitals")
            .whereEqualTo("userId", currentUserId)
            .whereEqualTo("type", type.name)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(limit.toLong())
            .get()
            .await()

        return snapshot.documents.mapNotNull { doc ->
            try {
                VitalReading(
                    id = doc.id,
                    type = VitalType.valueOf(doc.getString("type") ?: return@mapNotNull null),
                    value = doc.getDouble("value") ?: return@mapNotNull null,
                    unit = doc.getString("unit") ?: "",
                    timestamp = Instant.ofEpochMilli(doc.getLong("timestamp") ?: 0),
                    quality = VitalQuality.valueOf(doc.getString("quality") ?: "UNKNOWN"),
                    sourceDeviceId = doc.getString("sourceDeviceId") ?: "",
                    sessionId = doc.getString("sessionId")
                )
            } catch (e: Exception) {
                null
            }
        }
    }

    /**
     * Obtiene todas las lecturas del usuario desde Firestore.
     */
    suspend fun getAllReadings(limit: Int = 500): List<VitalReading> {
        val snapshot = firestore.collection("vitals")
            .whereEqualTo("userId", currentUserId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(limit.toLong())
            .get()
            .await()

        return snapshot.documents.mapNotNull { doc ->
            try {
                VitalReading(
                    id = doc.id,
                    type = VitalType.valueOf(doc.getString("type") ?: return@mapNotNull null),
                    value = doc.getDouble("value") ?: return@mapNotNull null,
                    unit = doc.getString("unit") ?: "",
                    timestamp = Instant.ofEpochMilli(doc.getLong("timestamp") ?: 0),
                    quality = VitalQuality.valueOf(doc.getString("quality") ?: "UNKNOWN"),
                    sourceDeviceId = doc.getString("sourceDeviceId") ?: "",
                    sessionId = doc.getString("sessionId")
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}

