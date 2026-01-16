package com.vitaalert.auth.repository

import com.vitaalert.auth.model.AuthTokensDto

/**
 * Fake auth repository with fixed credentials for local runtime.
 */
class FakeAuthRepository : AuthRepository {
    override suspend fun login(email: String, password: String): AuthTokensDto {
        if (email != VALID_EMAIL || password != VALID_PASSWORD) {
            throw UnauthorizedException("Invalid credentials")
        }
        return mockTokens()
    }

    override suspend fun refresh(refreshToken: String): AuthTokensDto {
        if (refreshToken.isBlank()) {
            throw UnauthorizedException("Missing refresh token")
        }
        return mockTokens()
    }

    private fun mockTokens(): AuthTokensDto = AuthTokensDto(
        accessToken = "mock-access-token",
        refreshToken = "mock-refresh-token",
        tokenType = "Bearer",
        expiresIn = 3600
    )

    private companion object {
        const val VALID_EMAIL = "user@test.com"
        const val VALID_PASSWORD = "Password123!"
    }
}

/**
 * Thrown when authentication fails.
 */
class UnauthorizedException(message: String) : IllegalStateException(message)
