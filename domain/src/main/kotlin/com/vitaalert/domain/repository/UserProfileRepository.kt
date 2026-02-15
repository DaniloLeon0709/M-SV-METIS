package com.vitaalert.domain.repository

import com.vitaalert.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for user profile operations.
 */
interface UserProfileRepository {
    /**
     * Returns the current user profile as a Flow.
     */
    fun observeProfile(): Flow<UserProfile?>

    /**
     * Gets the current user profile.
     */
    suspend fun getProfile(): UserProfile?

    /**
     * Updates the user profile.
     */
    suspend fun updateProfile(profile: UserProfile)

    /**
     * Updates the profile photo URI.
     */
    suspend fun updatePhotoUri(photoUri: String)

    /**
     * Changes the user password (mock implementation).
     * @return Result indicating success or failure with message.
     */
    suspend fun changePassword(currentPassword: String, newPassword: String): Result<Unit>

    /**
     * Clears the profile data (on logout).
     */
    suspend fun clearProfile()
}

