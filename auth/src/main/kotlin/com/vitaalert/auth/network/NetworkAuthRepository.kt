package com.vitaalert.auth.network

import com.vitaalert.auth.model.AuthTokensDto
import com.vitaalert.auth.repository.AuthRepository

/**
 * Network-backed auth repository for remote authentication.
 */
class NetworkAuthRepository(
    private val authApi: AuthApi
) : AuthRepository {
    override suspend fun login(email: String, password: String): AuthTokensDto {
        return authApi.login(AuthRequest(email, password))
    }

    override suspend fun refresh(refreshToken: String): AuthTokensDto {
        return authApi.refresh(RefreshRequest(refreshToken))
    }
}
