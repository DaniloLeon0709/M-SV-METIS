package com.vitaalert.mobile.dev.service

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Datos del dispositivo conectado.
 */
data class ConnectedDevice(
    val id: String,
    val name: String
)

/**
 * Estado de la conexión para la UI.
 */
enum class ConnectionState {
    IDLE,
    SEARCHING,           // Buscando dispositivo
    DEVICE_FOUND,        // Dispositivo encontrado
    CONNECTING,          // Conectando al dispositivo
    DISCOVERING_SERVICES,// Buscando servicios
    WAITING_FOR_DATA,    // Esperando datos de signos vitales
    CONNECTED,           // Recibiendo datos
    DISCONNECTED,        // Desconectado
    ERROR
}

/**
 * Singleton que mantiene el estado global del monitoreo BLE.
 * Este estado persiste mientras la app esté en memoria.
 */
@Singleton
class MonitoringStateManager @Inject constructor() {

    private val _state = MutableStateFlow(ConnectionState.IDLE)
    val state: StateFlow<ConnectionState> = _state.asStateFlow()

    private val _statusMessage = MutableStateFlow("")
    val statusMessage: StateFlow<String> = _statusMessage.asStateFlow()

    private val _isMonitoring = MutableStateFlow(false)
    val isMonitoring: StateFlow<Boolean> = _isMonitoring.asStateFlow()

    private val _connectedDevice = MutableStateFlow<ConnectedDevice?>(null)
    val connectedDevice: StateFlow<ConnectedDevice?> = _connectedDevice.asStateFlow()

    private val _lastHeartRate = MutableStateFlow<Int?>(null)
    val lastHeartRate: StateFlow<Int?> = _lastHeartRate.asStateFlow()

    /**
     * Inicia la búsqueda de dispositivos.
     */
    fun startSearching() {
        _state.value = ConnectionState.SEARCHING
        _statusMessage.value = "Buscando dispositivo..."
        _isMonitoring.value = true
    }

    /**
     * Dispositivo encontrado.
     */
    fun deviceFound(deviceName: String) {
        _state.value = ConnectionState.DEVICE_FOUND
        _statusMessage.value = "Dispositivo encontrado: $deviceName"
    }

    /**
     * Conectando al dispositivo.
     */
    fun startConnecting() {
        _state.value = ConnectionState.CONNECTING
        _statusMessage.value = "Conectando al dispositivo..."
        _isMonitoring.value = true
    }

    /**
     * Buscando servicios en el dispositivo.
     */
    fun discoveringServices() {
        _state.value = ConnectionState.DISCOVERING_SERVICES
        _statusMessage.value = "Buscando servicios de salud..."
    }

    /**
     * Esperando datos de signos vitales.
     */
    fun waitingForData(deviceName: String) {
        _state.value = ConnectionState.WAITING_FOR_DATA
        _statusMessage.value = "Conectado a $deviceName, esperando datos..."
        _isMonitoring.value = true
    }

    /**
     * Marca como conectado y recibiendo datos.
     */
    fun setConnected(deviceId: String, deviceName: String) {
        _state.value = ConnectionState.CONNECTED
        _statusMessage.value = "Recibiendo datos de $deviceName"
        _isMonitoring.value = true
        _connectedDevice.value = ConnectedDevice(deviceId, deviceName)
    }

    /**
     * Marca como desconectado.
     */
    fun setDisconnected() {
        _state.value = ConnectionState.DISCONNECTED
        _statusMessage.value = "Conexión perdida, reconectando..."
    }

    /**
     * Detiene completamente el monitoreo.
     */
    fun stopMonitoring() {
        _state.value = ConnectionState.IDLE
        _statusMessage.value = ""
        _isMonitoring.value = false
        _connectedDevice.value = null
        _lastHeartRate.value = null
    }

    /**
     * Reporta un error.
     */
    fun setError(message: String = "Error de conexión") {
        _state.value = ConnectionState.ERROR
        _statusMessage.value = message
        _isMonitoring.value = false
    }

    /**
     * Actualiza la última lectura de frecuencia cardíaca.
     */
    fun updateHeartRate(hr: Int) {
        _lastHeartRate.value = hr
        if (_state.value != ConnectionState.CONNECTED) {
            _state.value = ConnectionState.CONNECTED
        }
    }
}
