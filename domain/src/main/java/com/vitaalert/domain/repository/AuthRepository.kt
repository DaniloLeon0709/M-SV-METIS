package com.vitaalert.domain.repository

import com.vitaalert.domain.model.AuthTokens

/**
 * Auth repository abstraction.
 */
interface AuthRepository {
    /**
     * Performs a login with [username] and [password].
     */
    suspend fun login(username: String, password: String): AuthTokens

    /**
     * Refreshes tokens using the current refresh token.
     */
    suspend fun refresh(): AuthTokens

    /**
     * Clears any stored credentials.
     */
    suspend fun logout()
}
