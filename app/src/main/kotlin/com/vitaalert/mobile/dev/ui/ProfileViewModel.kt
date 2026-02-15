package com.vitaalert.mobile.dev.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaalert.auth.session.SessionManager
import com.vitaalert.data.repository.FirebaseProfileRepository
import com.vitaalert.data.repository.UserProfileRepositoryImpl
import com.vitaalert.domain.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for the profile screen.
 */
data class ProfileUiState(
    val profile: UserProfile? = null,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isChangingPassword: Boolean = false,
    val passwordChangeSuccess: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

/**
 * ViewModel for managing user profile.
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepositoryImpl,
    private val firebaseProfileRepository: FirebaseProfileRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // First try to load from Firebase
                var profile = try {
                    firebaseProfileRepository.getProfile()
                } catch (e: Exception) {
                    null
                }

                // If not in Firebase, try local
                if (profile == null) {
                    profile = userProfileRepository.getProfile()
                }

                // If still no profile, create initial one
                if (profile == null) {
                    profile = userProfileRepository.createInitialProfile("user@test.com", "Usuario")
                    // Sync to Firebase
                    try {
                        firebaseProfileRepository.saveProfile(profile)
                    } catch (e: Exception) {
                        // Firebase sync failed, continue with local
                    }
                }

                _uiState.update { it.copy(profile = profile, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error al cargar el perfil: ${e.message}"
                    )
                }
            }
        }

        // Observe profile changes from local
        viewModelScope.launch {
            userProfileRepository.observeProfile().collect { profile ->
                if (profile != null) {
                    _uiState.update { it.copy(profile = profile) }
                }
            }
        }
    }

    /**
     * Updates the profile photo with the given URI.
     * Uploads to Firebase Storage and syncs URL.
     */
    fun updatePhoto(photoUri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                // Upload to Firebase Storage
                val downloadUrl = firebaseProfileRepository.uploadProfilePhoto(photoUri)

                // Update local and Firebase
                userProfileRepository.updatePhotoUri(downloadUrl)

                val currentProfile = _uiState.value.profile
                if (currentProfile != null) {
                    firebaseProfileRepository.saveProfile(currentProfile.copy(photoUri = downloadUrl))
                }

                _uiState.update {
                    it.copy(
                        isSaving = false,
                        successMessage = "Foto actualizada correctamente"
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = "Error al actualizar la foto: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Updates the display name.
     */
    fun updateDisplayName(displayName: String) {
        viewModelScope.launch {
            val currentProfile = _uiState.value.profile ?: return@launch
            _uiState.update { it.copy(isSaving = true) }
            try {
                val updatedProfile = currentProfile.copy(displayName = displayName)
                userProfileRepository.updateProfile(updatedProfile)

                // Sync to Firebase
                try {
                    firebaseProfileRepository.saveProfile(updatedProfile)
                } catch (e: Exception) {
                    // Firebase sync failed, continue
                }

                _uiState.update {
                    it.copy(
                        isSaving = false,
                        successMessage = "Nombre actualizado correctamente"
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = "Error al actualizar el nombre: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Updates the phone number with country code.
     */
    fun updatePhoneNumber(phoneNumber: String) {
        viewModelScope.launch {
            val currentProfile = _uiState.value.profile ?: return@launch
            _uiState.update { it.copy(isSaving = true) }
            try {
                val updatedProfile = currentProfile.copy(
                    phoneNumber = phoneNumber,
                    updatedAt = System.currentTimeMillis()
                )
                userProfileRepository.updateProfile(updatedProfile)

                // Sync to Firebase
                try {
                    firebaseProfileRepository.saveProfile(updatedProfile)
                } catch (e: Exception) {
                    // Firebase sync failed, continue
                }

                _uiState.update {
                    it.copy(
                        isSaving = false,
                        successMessage = "Teléfono actualizado correctamente"
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = "Error al actualizar el teléfono: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Changes the user password using Firebase Auth.
     */
    fun changePassword(currentPassword: String, newPassword: String, confirmPassword: String) {
        if (newPassword != confirmPassword) {
            _uiState.update { it.copy(errorMessage = "Las contraseñas no coinciden") }
            return
        }

        if (newPassword.length < 8) {
            _uiState.update { it.copy(errorMessage = "La nueva contraseña debe tener al menos 8 caracteres") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isChangingPassword = true, errorMessage = null) }
            try {
                // Use Firebase to change password
                firebaseProfileRepository.changePassword(newPassword)
                _uiState.update {
                    it.copy(
                        isChangingPassword = false,
                        passwordChangeSuccess = true,
                        successMessage = "Contraseña cambiada correctamente"
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isChangingPassword = false,
                        errorMessage = "Error al cambiar la contraseña: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Clears any displayed messages.
     */
    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null, passwordChangeSuccess = false) }
    }

    /**
     * Logs out the user.
     */
    fun logout() {
        viewModelScope.launch {
            sessionManager.onUnauthenticated()
        }
    }
}

