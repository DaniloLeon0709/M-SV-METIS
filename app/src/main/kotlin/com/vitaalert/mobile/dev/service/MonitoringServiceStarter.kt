package com.vitaalert.mobile.dev.service

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import com.vitaalert.ble.service.BleServiceLocator
import com.vitaalert.ble.service.BleVitalSource
import com.vitaalert.ble.service.Dependencies
import com.vitaalert.ble.service.MonitoringService
import com.vitaalert.domain.repository.VitalRepository
import com.vitaalert.mobile.dev.health.CombinedVitalSource
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import com.vitaalert.ble.service.MonitoringState as BleMonitoringState

/**
 * Starts the BLE monitoring foreground service.
 * Supports both BLE direct connection and Health Connect.
 */
@Singleton
class MonitoringServiceStarter @Inject constructor(
    @ApplicationContext private val context: Context,
    private val vitalRepository: VitalRepository,
    private val bleVitalSource: BleVitalSource,
    private val combinedVitalSource: CombinedVitalSource,
    private val monitoringStateManager: MonitoringStateManager
) {
    companion object {
        private const val TAG = "MonitoringServiceStarter"
    }

    /**
     * Indica si Health Connect está instalado.
     */
    fun isHealthConnectInstalled(): Boolean = combinedVitalSource.isHealthConnectInstalled()

    /**
     * Indica si Health Connect necesita actualización.
     */
    fun healthConnectNeedsUpdate(): Boolean = combinedVitalSource.healthConnectNeedsUpdate()

    /**
     * Indica si Health Connect está disponible y listo para usar.
     */
    fun isHealthConnectAvailable(): Boolean = combinedVitalSource.isHealthConnectAvailable()

    /**
     * Obtiene la URL de Play Store para Health Connect.
     */
    fun getHealthConnectPlayStoreUrl(): String = combinedVitalSource.getHealthConnectPlayStoreUrl()

    /**
     * Starts the monitoring service.
     */
    fun start() {
        Log.d(TAG, "Starting monitoring service...")

        // Configure dependencies before starting the service
        BleServiceLocator.dependencies = Dependencies(
            repository = vitalRepository,
            source = bleVitalSource,
            onStateChange = { state, deviceInfo ->
                Log.d(TAG, "State changed: $state, device: ${deviceInfo?.name}")
                when (state) {
                    BleMonitoringState.SEARCHING -> {
                        monitoringStateManager.startSearching()
                    }
                    BleMonitoringState.DEVICE_FOUND -> {
                        deviceInfo?.let {
                            monitoringStateManager.deviceFound(it.name)
                        }
                    }
                    BleMonitoringState.CONNECTING -> {
                        monitoringStateManager.startConnecting()
                    }
                    BleMonitoringState.DISCOVERING_SERVICES -> {
                        monitoringStateManager.discoveringServices()
                    }
                    BleMonitoringState.WAITING_FOR_DATA -> {
                        deviceInfo?.let {
                            monitoringStateManager.waitingForData(it.name)
                        }
                    }
                    BleMonitoringState.CONNECTED -> {
                        deviceInfo?.let {
                            monitoringStateManager.setConnected(it.id, it.name)
                        }
                    }
                    BleMonitoringState.DISCONNECTED -> {
                        monitoringStateManager.setDisconnected()
                    }
                    BleMonitoringState.ERROR -> {
                        // El mensaje de error se maneja en onErrorMessage
                    }
                    BleMonitoringState.IDLE -> {
                        monitoringStateManager.stopMonitoring()
                    }
                }
            },
            onErrorMessage = { message ->
                Log.e(TAG, "Error received: $message")
                monitoringStateManager.setError(message)
            }
        )

        val intent = Intent(context, MonitoringService::class.java)
        ContextCompat.startForegroundService(context, intent)
    }

    /**
     * Starts monitoring using the combined source (Health Connect + BLE).
     * This allows reading data from any health app that syncs with Health Connect.
     */
    fun startWithHealthConnect() {
        Log.d(TAG, "Starting monitoring with Health Connect...")

        // Use combined source that includes Health Connect
        BleServiceLocator.dependencies = Dependencies(
            repository = vitalRepository,
            source = combinedVitalSource,
            onStateChange = { state, deviceInfo ->
                Log.d(TAG, "State changed: $state, device: ${deviceInfo?.name}")
                when (state) {
                    BleMonitoringState.SEARCHING -> {
                        monitoringStateManager.startSearching()
                    }
                    BleMonitoringState.DEVICE_FOUND -> {
                        deviceInfo?.let {
                            monitoringStateManager.deviceFound(it.name)
                        }
                    }
                    BleMonitoringState.CONNECTING -> {
                        monitoringStateManager.startConnecting()
                    }
                    BleMonitoringState.DISCOVERING_SERVICES -> {
                        monitoringStateManager.discoveringServices()
                    }
                    BleMonitoringState.WAITING_FOR_DATA -> {
                        deviceInfo?.let {
                            monitoringStateManager.waitingForData(it.name)
                        }
                    }
                    BleMonitoringState.CONNECTED -> {
                        deviceInfo?.let {
                            monitoringStateManager.setConnected(it.id, it.name)
                        }
                    }
                    BleMonitoringState.DISCONNECTED -> {
                        monitoringStateManager.setDisconnected()
                    }
                    BleMonitoringState.ERROR -> {
                        // Error message handled separately
                    }
                    BleMonitoringState.IDLE -> {
                        monitoringStateManager.stopMonitoring()
                    }
                }
            },
            onErrorMessage = { message ->
                Log.e(TAG, "Error received: $message")
                monitoringStateManager.setError(message)
            }
        )

        // Update state for Health Connect
        monitoringStateManager.waitingForData("Health Connect")

        val intent = Intent(context, MonitoringService::class.java)
        ContextCompat.startForegroundService(context, intent)
    }

    /**
     * Stops the monitoring service.
     */
    fun stop() {
        Log.d(TAG, "Stopping monitoring service...")
        val intent = Intent(context, MonitoringService::class.java)
        context.stopService(intent)
        monitoringStateManager.stopMonitoring()
    }
}
