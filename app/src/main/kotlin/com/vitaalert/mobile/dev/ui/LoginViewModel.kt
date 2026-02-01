package com.vitaalert.mobile.dev.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaalert.auth.repository.AuthRepository
import com.vitaalert.auth.repository.UnauthorizedException
import com.vitaalert.auth.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the login screen.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _state = MutableStateFlow(LoginUiState())

    /**
     * UI state for the login screen.
     */
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    /**
     * Attempts to authenticate using the provided credentials.
     */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            try {
                val tokens = authRepository.login(email, password)
                sessionManager.onAuthenticated(tokens)
                _state.value = _state.value.copy(isLoading = false, isSuccess = true)
            } catch (ex: UnauthorizedException) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Credenciales inválidas"
                )
            }
        }
    }
}

/**
 * UI model for login.
 */
data class LoginUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)
