package com.vitaalert.auth.repository

import com.vitaalert.auth.model.AuthTokensDto

/**
 * Repository for authentication actions.
 */
interface AuthRepository {
    /**
     * Attempts to log in with the provided credentials.
     */
    suspend fun login(email: String, password: String): AuthTokensDto

    /**
     * Attempts to refresh tokens using the provided refresh token.
     */
    suspend fun refresh(refreshToken: String): AuthTokensDto
}
