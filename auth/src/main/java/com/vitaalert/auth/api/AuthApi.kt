package com.vitaalert.auth.api

import com.vitaalert.auth.model.AuthRequest
import com.vitaalert.auth.model.AuthResponse
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Retrofit API for auth endpoints.
 */
interface AuthApi {
    /**
     * Performs login for the provided credentials.
     */
    @POST("/auth/login")
    suspend fun login(@Body request: AuthRequest): AuthResponse

    /**
     * Refreshes tokens.
     */
    @POST("/auth/refresh")
    suspend fun refresh(@Body request: AuthRequest): AuthResponse
}
