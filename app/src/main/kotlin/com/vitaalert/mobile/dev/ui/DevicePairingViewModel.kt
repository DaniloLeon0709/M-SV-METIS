package com.vitaalert.mobile.dev.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaalert.ble.model.BleDevice
import com.vitaalert.ble.scanner.BleScanner
import com.vitaalert.mobile.dev.service.BleServiceConnector
import com.vitaalert.mobile.dev.service.MonitoringServiceStarter
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for BLE device pairing and demo mode.
 */
@HiltViewModel
class DevicePairingViewModel @Inject constructor(
    private val bleScanner: BleScanner,
    private val bleServiceConnector: BleServiceConnector,
    private val monitoringServiceStarter: MonitoringServiceStarter
) : ViewModel() {
    /**
     * List of discovered BLE devices.
     */
    val devices: StateFlow<List<BleDevice>> = bleScanner.scan()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /**
     * Indicates whether demo mode is enabled.
     */
    val demoMode: StateFlow<Boolean> = bleServiceConnector.demoMode

    /**
     * Enables or disables demo mode.
     */
    fun setDemoMode(enabled: Boolean) {
        bleServiceConnector.setDemoMode(enabled)
    }

    /**
     * Starts monitoring via the foreground service.
     */
    fun startMonitoring() {
        viewModelScope.launch {
            monitoringServiceStarter.start()
        }
    }
}
