package com.vitaalert.domain.model

/**
 * Represents the user profile information.
 */
data class UserProfile(
    val id: String,
    val email: String,
    val displayName: String,
    val photoUri: String? = null,
    val phoneNumber: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

