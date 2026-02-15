package com.vitaalert.data.mapper

import com.vitaalert.data.local.UserProfileEntity
import com.vitaalert.domain.model.UserProfile

/**
 * Maps UserProfileEntity to domain UserProfile.
 */
fun UserProfileEntity.toDomainProfile(): UserProfile = UserProfile(
    id = id,
    email = email,
    displayName = displayName,
    photoUri = photoUri,
    phoneNumber = phoneNumber,
    createdAt = createdAt,
    updatedAt = updatedAt
)

/**
 * Maps domain UserProfile to UserProfileEntity.
 */
fun UserProfile.toProfileEntity(): UserProfileEntity = UserProfileEntity(
    id = id,
    email = email,
    displayName = displayName,
    photoUri = photoUri,
    phoneNumber = phoneNumber,
    createdAt = createdAt,
    updatedAt = updatedAt
)
