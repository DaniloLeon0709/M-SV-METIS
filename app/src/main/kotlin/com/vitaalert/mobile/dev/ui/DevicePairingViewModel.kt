package com.vitaalert.mobile.dev.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaalert.ble.model.BleDevice
import com.vitaalert.ble.scanner.BleScanner
import com.vitaalert.ble.service.BleVitalSource
import com.vitaalert.mobile.dev.service.BleServiceConnector
import com.vitaalert.mobile.dev.service.ConnectionState
import com.vitaalert.mobile.dev.service.MonitoringServiceStarter
import com.vitaalert.mobile.dev.service.MonitoringStateManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for BLE device pairing.
 */
@HiltViewModel
class DevicePairingViewModel @Inject constructor(
    private val bleScanner: BleScanner,
    private val bleServiceConnector: BleServiceConnector,
    private val monitoringServiceStarter: MonitoringServiceStarter,
    private val bleVitalSource: BleVitalSource,
    private val monitoringStateManager: MonitoringStateManager
) : ViewModel() {

    /**
     * Lista de dispositivos - filtrada para mostrar solo dispositivos relevantes.
     * Solo muestra dispositivos con nombre que contengan palabras clave de salud/wearables.
     */
    val devices: StateFlow<List<BleDevice>> = bleScanner.scan()
        .map { devices ->
            devices.filter { device ->
                val name = device.name?.lowercase() ?: return@filter false
                // Filtrar solo dispositivos que parezcan wearables de salud
                name.contains("watch") ||
                name.contains("band") ||
                name.contains("fit") ||
                name.contains("heart") ||
                name.contains("polar") ||
                name.contains("garmin") ||
                name.contains("huawei") ||
                name.contains("samsung") ||
                name.contains("mi band") ||
                name.contains("amazfit") ||
                name.contains("fitbit") ||
                name.contains("xiaomi")
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // Usa el estado global del MonitoringStateManager
    val isMonitoring: StateFlow<Boolean> = monitoringStateManager.isMonitoring

    val connectionStatus: StateFlow<ConnectionStatus> = monitoringStateManager.state
        .map { state ->
            when (state) {
                ConnectionState.CONNECTED -> ConnectionStatus.CONNECTED
                ConnectionState.SEARCHING,
                ConnectionState.DEVICE_FOUND,
                ConnectionState.CONNECTING,
                ConnectionState.DISCOVERING_SERVICES,
                ConnectionState.WAITING_FOR_DATA -> ConnectionStatus.CONNECTING
                ConnectionState.DISCONNECTED -> ConnectionStatus.DISCONNECTED
                ConnectionState.ERROR -> ConnectionStatus.DISCONNECTED
                ConnectionState.IDLE -> ConnectionStatus.DISCONNECTED
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ConnectionStatus.DISCONNECTED)

    // Mensaje de estado detallado
    val statusMessage: StateFlow<String> = monitoringStateManager.statusMessage

    // Dispositivo conectado actualmente
    val connectedDevice = monitoringStateManager.connectedDevice

    /**
     * Indica si Health Connect está instalado.
     */
    val isHealthConnectInstalled: Boolean
        get() = monitoringServiceStarter.isHealthConnectInstalled()

    /**
     * Indica si Health Connect necesita actualización.
     */
    val healthConnectNeedsUpdate: Boolean
        get() = monitoringServiceStarter.healthConnectNeedsUpdate()

    /**
     * Indica si Health Connect está disponible y listo.
     */
    val isHealthConnectAvailable: Boolean
        get() = monitoringServiceStarter.isHealthConnectAvailable()

    /**
     * Obtiene la URL de Play Store para Health Connect.
     */
    fun getHealthConnectPlayStoreUrl(): String = monitoringServiceStarter.getHealthConnectPlayStoreUrl()

    /**
     * Starts BLE scanning.
     */
    fun startScan() {
        bleScanner.startScan()
    }

    /**
     * Stops BLE scanning.
     */
    fun stopScan() {
        bleScanner.stopScan()
    }

    /**
     * Starts monitoring via BLE direct connection.
     */
    fun startMonitoring() {
        viewModelScope.launch {
            // Stop scanning before starting monitoring
            bleScanner.stopScan()

            // Start the service (state will be updated via callbacks)
            monitoringServiceStarter.start()
        }
    }

    /**
     * Starts monitoring via Health Connect.
     * This reads data from apps like Huawei Health, Samsung Health, etc.
     */
    fun startHealthConnectMonitoring() {
        viewModelScope.launch {
            bleScanner.stopScan()
            monitoringServiceStarter.startWithHealthConnect()
        }
    }

    /**
     * Stops monitoring.
     */
    fun stopMonitoring() {
        monitoringServiceStarter.stop()
    }

    override fun onCleared() {
        super.onCleared()
        bleScanner.stopScan()
    }
}
