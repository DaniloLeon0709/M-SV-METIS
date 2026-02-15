package com.vitaalert.mobile.dev.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaalert.auth.repository.FirebaseAuthRepository
import com.vitaalert.data.repository.FirebaseProfileRepository
import com.vitaalert.domain.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para la pantalla de registro.
 */
@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: FirebaseAuthRepository,
    private val profileRepository: FirebaseProfileRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterUiState())
    val state: StateFlow<RegisterUiState> = _state.asStateFlow()

    /**
     * Registra un nuevo usuario con Firebase.
     */
    fun register(name: String, email: String, password: String, confirmPassword: String) {
        // Validaciones
        when {
            name.isBlank() -> {
                _state.value = _state.value.copy(errorMessage = "Por favor ingresa tu nombre")
                return
            }
            email.isBlank() -> {
                _state.value = _state.value.copy(errorMessage = "Por favor ingresa tu correo")
                return
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                _state.value = _state.value.copy(errorMessage = "Formato de email inválido")
                return
            }
            password.length < 8 -> {
                _state.value = _state.value.copy(errorMessage = "La contraseña debe tener al menos 8 caracteres")
                return
            }
            password != confirmPassword -> {
                _state.value = _state.value.copy(errorMessage = "Las contraseñas no coinciden")
                return
            }
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            try {
                // Crear cuenta en Firebase Auth
                authRepository.createAccount(email, password)

                // Crear perfil en Firestore
                val userId = authRepository.getCurrentUserId()
                if (userId != null) {
                    val profile = UserProfile(
                        id = userId,
                        email = email,
                        displayName = name,
                        photoUri = null,
                        phoneNumber = null,
                        createdAt = System.currentTimeMillis(),
                        updatedAt = System.currentTimeMillis()
                    )
                    profileRepository.saveProfile(profile)
                }

                // Cerrar sesión para que el usuario inicie sesión manualmente
                authRepository.signOut()

                _state.value = _state.value.copy(isLoading = false, isSuccess = true)
            } catch (ex: Exception) {
                val errorMessage = when {
                    ex.message?.contains("EMAIL_EXISTS") == true ||
                    ex.message?.contains("email-already-in-use") == true ->
                        "Este correo ya está registrado"
                    ex.message?.contains("WEAK_PASSWORD") == true ||
                    ex.message?.contains("weak-password") == true ->
                        "La contraseña es muy débil"
                    ex.message?.contains("INVALID_EMAIL") == true ->
                        "Formato de email inválido"
                    ex.message?.contains("NETWORK") == true ||
                    ex.message?.contains("network") == true ->
                        "Error de conexión. Verifica tu internet"
                    else -> "Error al crear la cuenta. Inténtalo de nuevo"
                }
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = errorMessage
                )
            }
        }
    }

    /**
     * Limpia el mensaje de error.
     */
    fun clearError() {
        _state.value = _state.value.copy(errorMessage = null)
    }
}

/**
 * Estado UI para la pantalla de registro.
 */
data class RegisterUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

