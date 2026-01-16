package com.vitaalert.mobile.dev.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaalert.domain.repository.SensorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel exposing a basic sensor summary to the UI.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    sensorRepository: SensorRepository
) : ViewModel() {
    val uiState: StateFlow<MainUiState> = sensorRepository.latestSummary()
        .map { summary -> MainUiState(summary = summary) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MainUiState("Esperando lecturas...")
        )
}

/**
 * UI state for the main screen.
 */
data class MainUiState(
    val summary: String
)
