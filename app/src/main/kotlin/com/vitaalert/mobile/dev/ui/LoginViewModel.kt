package com.vitaalert.mobile.dev.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaalert.auth.repository.AuthRepository
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
     * Attempts to authenticate using Firebase Auth.
     */
    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _state.value = _state.value.copy(errorMessage = "Por favor ingresa email y contraseña")
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            try {
                val tokens = authRepository.login(email, password)
                sessionManager.onAuthenticated(tokens)
                _state.value = _state.value.copy(isLoading = false, isSuccess = true)
            } catch (ex: Exception) {
                val errorMessage = when {
                    ex.message?.contains("INVALID_LOGIN_CREDENTIALS") == true ||
                    ex.message?.contains("USER_NOT_FOUND") == true ||
                    ex.message?.contains("WRONG_PASSWORD") == true ||
                    ex.message?.contains("USER_DISABLED") == true ||
                    ex.message?.contains("disabled") == true ->
                        "Credenciales inválidas. Verifica tu email y contraseña"
                    ex.message?.contains("INVALID_EMAIL") == true ->
                        "Formato de email inválido"
                    ex.message?.contains("TOO_MANY_REQUESTS") == true ->
                        "Demasiados intentos. Intenta más tarde"
                    ex.message?.contains("NETWORK") == true ||
                    ex.message?.contains("network") == true ->
                        "Error de conexión. Verifica tu internet"
                    else -> "Error al iniciar sesión. Inténtalo de nuevo"
                }
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = errorMessage
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
