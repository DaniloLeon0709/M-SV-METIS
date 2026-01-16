package com.vitaalert.auth.repository

import com.vitaalert.auth.api.AuthApi
import com.vitaalert.auth.model.AuthRequest
import com.vitaalert.domain.model.AuthTokens
import com.vitaalert.domain.repository.AuthRepository

/**
 * Retrofit-backed repository, intended for tests with MockWebServer.
 */
class RetrofitAuthRepository(
    private val api: AuthApi
) : AuthRepository {
    override suspend fun login(username: String, password: String): AuthTokens {
        val response = api.login(AuthRequest(username, password))
        return AuthTokens(response.accessToken, response.refreshToken)
    }

    override suspend fun refresh(): AuthTokens {
        val response = api.refresh(AuthRequest("refresh", "refresh"))
        return AuthTokens(response.accessToken, response.refreshToken)
    }

    override suspend fun logout() = Unit
}
