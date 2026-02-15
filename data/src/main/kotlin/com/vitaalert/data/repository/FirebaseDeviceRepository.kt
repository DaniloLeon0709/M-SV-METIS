package com.vitaalert.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Data class for device stored in Firebase.
 */
data class DeviceInfo(
    val id: String,
    val userId: String,
    val macAddress: String,
    val name: String,
    val model: String,
    val lastSeen: Long
)

/**
 * Repository para gestionar dispositivos BLE en Firebase Firestore.
 */
class FirebaseDeviceRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    companion object {
        private const val TAG = "FirebaseDeviceRepo"
        private const val COLLECTION_DEVICES = "devices"
    }

    private val currentUserId: String?
        get() = auth.currentUser?.uid

    /**
     * Guarda o actualiza un dispositivo en Firestore.
     */
    suspend fun saveDevice(macAddress: String, name: String?, model: String = "BLE Heart Rate Monitor") {
        val userId = currentUserId ?: run {
            Log.w(TAG, "User not logged in, cannot save device")
            return
        }

        try {
            // Check if device already exists for this user
            val existingDoc = firestore.collection(COLLECTION_DEVICES)
                .whereEqualTo("userId", userId)
                .whereEqualTo("macAddress", macAddress)
                .get()
                .await()

            val data = hashMapOf(
                "userId" to userId,
                "macAddress" to macAddress,
                "name" to (name ?: "Unknown Device"),
                "model" to model,
                "lastSeen" to System.currentTimeMillis()
            )

            if (existingDoc.documents.isNotEmpty()) {
                // Update existing device
                val docId = existingDoc.documents.first().id
                firestore.collection(COLLECTION_DEVICES)
                    .document(docId)
                    .update(data as Map<String, Any>)
                    .await()
                Log.d(TAG, "Updated device: $name - $macAddress")
            } else {
                // Create new device
                firestore.collection(COLLECTION_DEVICES)
                    .add(data)
                    .await()
                Log.d(TAG, "Saved new device: $name - $macAddress")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save device", e)
        }
    }

    /**
     * Obtiene todos los dispositivos del usuario.
     */
    suspend fun getDevices(): List<DeviceInfo> {
        val userId = currentUserId ?: return emptyList()

        return try {
            val snapshot = firestore.collection(COLLECTION_DEVICES)
                .whereEqualTo("userId", userId)
                .orderBy("lastSeen", Query.Direction.DESCENDING)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                try {
                    DeviceInfo(
                        id = doc.id,
                        userId = doc.getString("userId") ?: return@mapNotNull null,
                        macAddress = doc.getString("macAddress") ?: return@mapNotNull null,
                        name = doc.getString("name") ?: "Unknown",
                        model = doc.getString("model") ?: "",
                        lastSeen = doc.getLong("lastSeen") ?: 0
                    )
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get devices", e)
            emptyList()
        }
    }

    /**
     * Elimina un dispositivo de Firestore.
     */
    suspend fun deleteDevice(deviceId: String) {
        try {
            firestore.collection(COLLECTION_DEVICES)
                .document(deviceId)
                .delete()
                .await()
            Log.d(TAG, "Deleted device: $deviceId")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete device", e)
        }
    }
}


