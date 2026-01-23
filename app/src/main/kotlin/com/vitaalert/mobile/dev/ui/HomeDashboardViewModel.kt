package com.vitaalert.mobile.dev.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaalert.designsystem.VitalState as UiVitalState
import com.vitaalert.domain.model.VitalReading
import com.vitaalert.domain.model.VitalType
import com.vitaalert.domain.repository.VitalRepository
import com.vitaalert.domain.service.VitalThresholds
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel for the home dashboard.
 */
@HiltViewModel
class HomeDashboardViewModel @Inject constructor(
    private val vitalRepository: VitalRepository,
    private val vitalThresholds: VitalThresholds
) : ViewModel() {
    private val tickFlow = flow {
        while (true) {
            emit(Instant.now())
            delay(60_000)
        }
    }

    /**
     * Latest heart rate card data.
     */
    val heartRateCard = vitalRepository.observeLatestByType(VitalType.HR)
        .map { reading -> reading?.toCardUi("HR", "bpm") }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    /**
     * Latest SpO2 card data.
     */
    val spo2Card = vitalRepository.observeLatestByType(VitalType.SPO2)
        .map { reading -> reading?.toCardUi("SpO2", "%") }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    /**
     * Heart rate readings for the last 15 minutes.
     */
    val heartRateSeries: Flow<List<VitalReading>> = tickFlow.flatMapLatest { now ->
        val from = now.minus(15, ChronoUnit.MINUTES)
        vitalRepository.observeRangeByType(VitalType.HR, from, now)
    }

    /**
     * Combined UI state for the dashboard.
     */
    val uiState = combine(heartRateCard, spo2Card) { hr, spo2 ->
        HomeDashboardState(heartRate = hr, spo2 = spo2)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeDashboardState())

    private fun VitalReading.toCardUi(label: String, unitOverride: String): VitalCardUi {
        val state = when (vitalThresholds.resolve(type, value)) {
            com.vitaalert.domain.model.VitalState.NORMAL -> UiVitalState.NORMAL
            com.vitaalert.domain.model.VitalState.ALERT -> UiVitalState.ALERT
            com.vitaalert.domain.model.VitalState.CRITICAL -> UiVitalState.CRITICAL
        }
        return VitalCardUi(
            value = value.toInt().toString(),
            label = "$label ($unitOverride)",
            state = state
        )
    }
}

/**
 * UI model for dashboard cards.
 */
data class VitalCardUi(
    val value: String,
    val label: String,
    val state: UiVitalState
)

/**
 * UI state for the dashboard.
 */
data class HomeDashboardState(
    val heartRate: VitalCardUi? = null,
    val spo2: VitalCardUi? = null
)
