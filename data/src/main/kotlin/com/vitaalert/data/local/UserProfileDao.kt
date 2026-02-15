package com.vitaalert.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * DAO for user profile operations.
 */
@Dao
interface UserProfileDao {
    /**
     * Observes the user profile.
     */
    @Query("SELECT * FROM user_profile LIMIT 1")
    fun observeProfile(): Flow<UserProfileEntity?>

    /**
     * Gets the user profile.
     */
    @Query("SELECT * FROM user_profile LIMIT 1")
    suspend fun getProfile(): UserProfileEntity?

    /**
     * Inserts or updates the user profile.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProfile(profile: UserProfileEntity)

    /**
     * Updates only the photo URI.
     */
    @Query("UPDATE user_profile SET photoUri = :photoUri, updatedAt = :updatedAt WHERE id = (SELECT id FROM user_profile LIMIT 1)")
    suspend fun updatePhotoUri(photoUri: String, updatedAt: Long = System.currentTimeMillis())

    /**
     * Clears all profile data.
     */
    @Query("DELETE FROM user_profile")
    suspend fun clearProfile()
}

