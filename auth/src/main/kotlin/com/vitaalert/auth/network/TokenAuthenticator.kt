package com.vitaalert.auth.network

import com.vitaalert.auth.repository.AuthRepository
import com.vitaalert.auth.repository.UnauthorizedException
import com.vitaalert.auth.session.SessionManager
import com.vitaalert.auth.storage.TokenStorage
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.HttpException

/**
 * OkHttp authenticator that refreshes tokens on 401 responses.
 */
class TokenAuthenticator(
    private val tokenStorage: TokenStorage,
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager,
    private val mutex: Mutex = Mutex()
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= MAX_ATTEMPTS) return null

        return runBlocking {
            mutex.withLock {
                val refreshToken = tokenStorage.getRefreshToken()
                    ?: return@withLock null

                sessionManager.onRefreshing()
                try {
                    val tokens = authRepository.refresh(refreshToken)
                    tokenStorage.saveTokens(tokens)
                    sessionManager.onAuthenticated(tokens)
                    response.request.newBuilder()
                        .header("Authorization", "Bearer ${tokens.accessToken}")
                        .build()
                } catch (ex: UnauthorizedException) {
                    sessionManager.onUnauthenticated()
                    null
                } catch (ex: HttpException) {
                    if (ex.code() == 401) {
                        sessionManager.onUnauthenticated()
                    }
                    null
                }
            }
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }

    private companion object {
        const val MAX_ATTEMPTS = 2
    }
}
