package com.vitaalert.auth.network

import com.vitaalert.auth.model.AuthTokensDto
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Retrofit API for auth endpoints.
 */
interface AuthApi {
    /**
     * Performs login with the provided [request].
     */
    @POST("/auth/login")
    suspend fun login(@Body request: AuthRequest): AuthTokensDto

    /**
     * Performs refresh with the provided [request].
     */
    @POST("/auth/refresh")
    suspend fun refresh(@Body request: RefreshRequest): AuthTokensDto
}

/**
 * Request payload for login.
 */
@Serializable
data class AuthRequest(
    val email: String,
    val password: String
)

/**
 * Request payload for refresh.
 */
@Serializable
data class RefreshRequest(
    val refreshToken: String
)
