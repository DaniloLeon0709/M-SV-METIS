package com.vitaalert.data.repository

import com.vitaalert.data.local.UserProfileDao
import com.vitaalert.data.mapper.toDomainProfile
import com.vitaalert.data.mapper.toProfileEntity
import com.vitaalert.domain.model.UserProfile
import com.vitaalert.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of UserProfileRepository using Room.
 */
@Singleton
class UserProfileRepositoryImpl @Inject constructor(
    private val userProfileDao: UserProfileDao
) : UserProfileRepository {

    override fun observeProfile(): Flow<UserProfile?> {
        return userProfileDao.observeProfile().map { entity ->
            entity?.toDomainProfile()
        }
    }

    override suspend fun getProfile(): UserProfile? {
        return userProfileDao.getProfile()?.toDomainProfile()
    }

    override suspend fun updateProfile(profile: UserProfile) {
        userProfileDao.upsertProfile(profile.copy(updatedAt = System.currentTimeMillis()).toProfileEntity())
    }

    override suspend fun updatePhotoUri(photoUri: String) {
        userProfileDao.updatePhotoUri(photoUri)
    }

    override suspend fun changePassword(currentPassword: String, newPassword: String): Result<Unit> {
        // Mock implementation - in real app this would call backend API
        return if (currentPassword == "Password123!") {
            // Simulate password validation and change
            if (newPassword.length >= 8) {
                Result.success(Unit)
            } else {
                Result.failure(IllegalArgumentException("La nueva contraseña debe tener al menos 8 caracteres"))
            }
        } else {
            Result.failure(IllegalArgumentException("La contraseña actual es incorrecta"))
        }
    }

    override suspend fun clearProfile() {
        userProfileDao.clearProfile()
    }

    /**
     * Creates initial profile for a new user.
     */
    suspend fun createInitialProfile(email: String, displayName: String = "Usuario"): UserProfile {
        val profile = UserProfile(
            id = java.util.UUID.randomUUID().toString(),
            email = email,
            displayName = displayName
        )
        userProfileDao.upsertProfile(profile.toProfileEntity())
        return profile
    }
}

