package com.vitaalert.auth.repository

import com.vitaalert.core.security.SecureStorage
import com.vitaalert.domain.model.AuthTokens
import com.vitaalert.domain.repository.AuthRepository
import javax.inject.Inject
import kotlinx.coroutines.delay

/**
 * In-memory auth repository for runtime without backend.
 */
class InMemoryAuthRepository @Inject constructor(
    private val secureStorage: SecureStorage
) : AuthRepository {
    override suspend fun login(username: String, password: String): AuthTokens {
        delay(300)
        val tokens = AuthTokens(
            accessToken = "demo-access-$username",
            refreshToken = "demo-refresh-$username"
        )
        save(tokens)
        return tokens
    }

    override suspend fun refresh(): AuthTokens {
        val currentRefresh = secureStorage.get(KEY_REFRESH) ?: "demo-refresh"
        val tokens = AuthTokens(
            accessToken = "demo-access-${currentRefresh.takeLast(6)}",
            refreshToken = currentRefresh
        )
        save(tokens)
        return tokens
    }

    override suspend fun logout() {
        secureStorage.remove(KEY_ACCESS)
        secureStorage.remove(KEY_REFRESH)
    }

    private fun save(tokens: AuthTokens) {
        secureStorage.put(KEY_ACCESS, tokens.accessToken)
        secureStorage.put(KEY_REFRESH, tokens.refreshToken)
    }

    private companion object {
        const val KEY_ACCESS = "auth_access"
        const val KEY_REFRESH = "auth_refresh"
    }
}
