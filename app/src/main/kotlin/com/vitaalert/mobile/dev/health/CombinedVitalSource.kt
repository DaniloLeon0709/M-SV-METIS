package com.vitaalert.mobile.dev.health

import android.util.Log
import com.vitaalert.ble.service.BleVitalSource
import com.vitaalert.ble.service.VitalSource
import com.vitaalert.domain.model.VitalReading
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Fuente de datos combinada que intenta obtener signos vitales de múltiples fuentes:
 * 1. Health Connect (datos de cualquier app de salud)
 * 2. BLE directo (para dispositivos compatibles)
 *
 * Esto permite que la app funcione con cualquier reloj/pulsera que sincronice
 * datos con Health Connect (Huawei Health, Samsung Health, Google Fit, etc.)
 */
@Singleton
class CombinedVitalSource @Inject constructor(
    private val healthConnectDataSource: HealthConnectDataSource,
    private val bleVitalSource: BleVitalSource
) : VitalSource {

    companion object {
        private const val TAG = "CombinedVitalSource"
    }

    /**
     * Indica si Health Connect está instalado.
     */
    fun isHealthConnectInstalled(): Boolean = healthConnectDataSource.isInstalled()

    /**
     * Indica si Health Connect necesita actualización.
     */
    fun healthConnectNeedsUpdate(): Boolean = healthConnectDataSource.needsUpdate()

    /**
     * Indica si Health Connect está disponible y listo.
     */
    fun isHealthConnectAvailable(): Boolean = healthConnectDataSource.isAvailable()

    /**
     * Obtiene la URL de Play Store para Health Connect.
     */
    fun getHealthConnectPlayStoreUrl(): String = HealthConnectDataSource.PLAY_STORE_URL

    /**
     * Indica si BLE está disponible.
     */
    fun isBleAvailable(): Boolean = bleVitalSource.isBluetoothAvailable()

    /**
     * Verifica permisos de Health Connect.
     */
    suspend fun hasHealthConnectPermissions(): Boolean = healthConnectDataSource.hasPermissions()

    /**
     * Verifica permisos BLE.
     */
    fun hasBlePermissions(): Boolean = bleVitalSource.hasPermissions()

    /**
     * Flujo combinado de lecturas de ambas fuentes.
     */
    override fun readings(): Flow<VitalReading> = flow {
        Log.d(TAG, "Starting combined vital source...")

        // Intentar ambas fuentes en paralelo
        val flows = mutableListOf<Flow<VitalReading>>()

        // Health Connect (siempre disponible si está instalado)
        if (healthConnectDataSource.isAvailable()) {
            Log.d(TAG, "Health Connect available, adding to sources")
            flows.add(healthConnectDataSource.heartRateFlow())
        }

        // BLE directo
        if (bleVitalSource.isBluetoothAvailable() && bleVitalSource.hasPermissions()) {
            Log.d(TAG, "BLE available, adding to sources")
            flows.add(bleVitalSource.readings())
        }

        if (flows.isEmpty()) {
            Log.w(TAG, "No vital sources available!")
            return@flow
        }

        // Merge all flows
        emitAll(merge(*flows.toTypedArray()))
    }

    /**
     * Solo lecturas de Health Connect.
     */
    fun healthConnectReadings(): Flow<VitalReading> = healthConnectDataSource.heartRateFlow()

    /**
     * Solo lecturas BLE.
     */
    fun bleReadings(): Flow<VitalReading> = bleVitalSource.readings()
}

