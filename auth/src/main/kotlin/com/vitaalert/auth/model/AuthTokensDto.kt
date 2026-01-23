package com.vitaalert.auth.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO representing auth tokens from the backend.
 */
@Serializable
data class AuthTokensDto(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("token_type") val tokenType: String,
    @SerialName("expires_in") val expiresIn: Long
)
