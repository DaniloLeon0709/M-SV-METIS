package com.vitaalert.domain.model

/**
 * Represents authentication tokens.
 */
data class AuthTokens(
    val accessToken: String,
    val refreshToken: String
)
