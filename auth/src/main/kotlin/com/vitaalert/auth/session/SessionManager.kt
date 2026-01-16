package com.vitaalert.auth.session

import com.vitaalert.auth.model.AuthTokensDto
import com.vitaalert.auth.storage.TokenStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Holds the authentication session state for the current user.
 */
class SessionManager(
    private val tokenStorage: TokenStorage
) {
    private val _state = MutableStateFlow<SessionState>(SessionState.Unauthenticated)

    /**
     * Public read-only state flow for observers.
     */
    val state: StateFlow<SessionState> = _state.asStateFlow()

    /**
     * Initializes the state based on stored tokens.
     */
    fun initialize() {
        val accessToken = tokenStorage.getAccessToken()
        _state.value = if (accessToken.isNullOrBlank()) {
            SessionState.Unauthenticated
        } else {
            SessionState.Authenticated(accessToken)
        }
    }

    /**
     * Updates the state for newly obtained [tokens].
     */
    fun onAuthenticated(tokens: AuthTokensDto) {
        tokenStorage.saveTokens(tokens)
        _state.value = SessionState.Authenticated(tokens.accessToken)
    }

    /**
     * Updates the state to refreshing.
     */
    fun onRefreshing() {
        _state.value = SessionState.Refreshing
    }

    /**
     * Clears tokens and marks the session unauthenticated.
     */
    fun onUnauthenticated() {
        tokenStorage.clear()
        _state.value = SessionState.Unauthenticated
    }
}

/**
 * State of the auth session.
 */
sealed class SessionState {
    /**
     * User is not authenticated.
     */
    data object Unauthenticated : SessionState()

    /**
     * User is authenticated with an access token.
     */
    data class Authenticated(val accessToken: String) : SessionState()

    /**
     * Session is refreshing tokens.
     */
    data object Refreshing : SessionState()
}
