package com.vitaalert.auth.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Login or refresh request payload.
 */
@Serializable
data class AuthRequest(
    @SerialName("username") val username: String,
    @SerialName("password") val password: String
)

/**
 * Response payload containing tokens.
 */
@Serializable
data class AuthResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String
)
