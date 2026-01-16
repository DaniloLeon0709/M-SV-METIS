package com.vitaalert.mobile.dev.ui

import androidx.lifecycle.ViewModel
import com.vitaalert.auth.session.SessionManager
import com.vitaalert.auth.session.SessionState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.StateFlow

/**
 * ViewModel responsible for determining the initial navigation destination.
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val sessionManager: SessionManager
) : ViewModel() {
    /**
     * Exposes the current session state.
     */
    val sessionState: StateFlow<SessionState> = sessionManager.state

    /**
     * Initializes session state from storage.
     */
    fun initialize() {
        sessionManager.initialize()
    }
}
