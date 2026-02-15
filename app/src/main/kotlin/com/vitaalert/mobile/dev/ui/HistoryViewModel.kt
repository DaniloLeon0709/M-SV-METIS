package com.vitaalert.mobile.dev.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaalert.domain.model.VitalReading
import com.vitaalert.domain.model.VitalType
import com.vitaalert.domain.repository.VitalRepository
import com.vitaalert.domain.service.VitalThresholds
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject

/**
 * UI state for vitals history.
 */
data class HistoryUiState(
    val isLoading: Boolean = true,
    val selectedType: VitalType = VitalType.HR,
    val selectedPeriod: HistoryPeriod = HistoryPeriod.TODAY,
    val readings: List<VitalReading> = emptyList(),
    val stats: VitalStats? = null
)

/**
 * Statistics for vital readings.
 */
data class VitalStats(
    val average: Double,
    val min: Double,
    val max: Double,
    val count: Int,
    val normalCount: Int,
    val alertCount: Int,
    val criticalCount: Int
)

/**
 * Time periods for history view.
 */
enum class HistoryPeriod(val label: String, val hours: Long) {
    TODAY("Hoy", 24),
    WEEK("7 días", 168),
    MONTH("30 días", 720)
}

/**
 * ViewModel for vitals history screen.
 */
@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val vitalRepository: VitalRepository,
    private val vitalThresholds: VitalThresholds
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    /**
     * Changes the selected vital type and reloads data.
     */
    fun selectType(type: VitalType) {
        _uiState.update { it.copy(selectedType = type) }
        loadHistory()
    }

    /**
     * Changes the selected time period and reloads data.
     */
    fun selectPeriod(period: HistoryPeriod) {
        _uiState.update { it.copy(selectedPeriod = period) }
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val now = Instant.now()
            val from = now.minus(_uiState.value.selectedPeriod.hours, ChronoUnit.HOURS)
            val type = _uiState.value.selectedType

            try {
                vitalRepository.observeRangeByType(type, from, now).collect { readings ->
                    val stats = calculateStats(readings, type)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            readings = readings.sortedByDescending { r -> r.timestamp },
                            stats = stats
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun calculateStats(readings: List<VitalReading>, type: VitalType): VitalStats? {
        if (readings.isEmpty()) return null

        val values = readings.map { it.value }
        var normalCount = 0
        var alertCount = 0
        var criticalCount = 0

        readings.forEach { reading ->
            when (vitalThresholds.resolve(type, reading.value)) {
                com.vitaalert.domain.model.VitalState.NORMAL -> normalCount++
                com.vitaalert.domain.model.VitalState.ALERT -> alertCount++
                com.vitaalert.domain.model.VitalState.CRITICAL -> criticalCount++
            }
        }

        return VitalStats(
            average = values.average(),
            min = values.minOrNull() ?: 0.0,
            max = values.maxOrNull() ?: 0.0,
            count = readings.size,
            normalCount = normalCount,
            alertCount = alertCount,
            criticalCount = criticalCount
        )
    }
}

