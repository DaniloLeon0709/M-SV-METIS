package com.vitaalert.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.vitaalert.domain.model.UserProfile
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Repository para gestionar el perfil del usuario en Firebase.
 */
class FirebaseProfileRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth
) {

    private val currentUserId: String
        get() = auth.currentUser?.uid
            ?: throw IllegalStateException("User not logged in")

    /**
     * Obtiene el perfil del usuario actual desde Firestore.
     */
    suspend fun getProfile(): UserProfile? {
        val doc = firestore.collection("users")
            .document(currentUserId)
            .get()
            .await()

        return if (doc.exists()) {
            UserProfile(
                id = doc.id,
                email = doc.getString("email") ?: "",
                displayName = doc.getString("displayName") ?: "",
                photoUri = doc.getString("photoUrl"),
                phoneNumber = doc.getString("phoneNumber"),
                createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis(),
                updatedAt = doc.getLong("updatedAt") ?: System.currentTimeMillis()
            )
        } else {
            null
        }
    }

    /**
     * Crea o actualiza el perfil del usuario en Firestore.
     */
    suspend fun saveProfile(profile: UserProfile) {
        val data = hashMapOf(
            "email" to profile.email,
            "displayName" to profile.displayName,
            "photoUrl" to profile.photoUri,
            "phoneNumber" to profile.phoneNumber,
            "createdAt" to profile.createdAt,
            "updatedAt" to System.currentTimeMillis()
        )

        firestore.collection("users")
            .document(currentUserId)
            .set(data)
            .await()
    }

    /**
     * Sube una foto de perfil a Firebase Storage.
     * @return URL de descarga de la imagen.
     */
    suspend fun uploadProfilePhoto(uri: Uri): String {
        val ref = storage.reference
            .child("profile_photos")
            .child(currentUserId)
            .child("profile_${System.currentTimeMillis()}.jpg")

        ref.putFile(uri).await()
        return ref.downloadUrl.await().toString()
    }

    /**
     * Actualiza la contraseña del usuario.
     */
    suspend fun changePassword(newPassword: String) {
        auth.currentUser?.updatePassword(newPassword)?.await()
            ?: throw IllegalStateException("User not logged in")
    }

    /**
     * Crea el perfil inicial del usuario después del registro.
     */
    suspend fun createInitialProfile(email: String, displayName: String) {
        val profile = UserProfile(
            id = currentUserId,
            email = email,
            displayName = displayName,
            photoUri = null,
            phoneNumber = null,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        saveProfile(profile)
    }
}

