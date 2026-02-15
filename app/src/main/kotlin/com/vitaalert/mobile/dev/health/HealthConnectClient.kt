package com.vitaalert.mobile.dev.health

import android.content.Context
import android.util.Log
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.OxygenSaturationRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.vitaalert.domain.model.VitalQuality
import com.vitaalert.domain.model.VitalReading
import com.vitaalert.domain.model.VitalType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Cliente para leer datos de salud desde Health Connect.
 * Health Connect es el estándar de Google para compartir datos de salud entre apps.
 *
 * Funciona con cualquier app que sincronice con Health Connect:
 * - Huawei Health
 * - Samsung Health
 * - Google Fit
 * - Fitbit
 * - Garmin Connect
 * - etc.
 */
@Singleton
class HealthConnectDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "HealthConnectClient"
        const val HEALTH_CONNECT_PACKAGE = "com.google.android.apps.healthdata"
        const val PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=$HEALTH_CONNECT_PACKAGE"

        val PERMISSIONS = setOf(
            HealthPermission.getReadPermission(HeartRateRecord::class),
            HealthPermission.getReadPermission(OxygenSaturationRecord::class)
        )
    }

    private val healthConnectClient: HealthConnectClient? by lazy {
        try {
            if (HealthConnectClient.getSdkStatus(context) == HealthConnectClient.SDK_AVAILABLE) {
                HealthConnectClient.getOrCreate(context)
            } else {
                Log.w(TAG, "Health Connect not available")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error creating Health Connect client", e)
            null
        }
    }

    /**
     * Verifica si Health Connect está instalado en el dispositivo.
     */
    fun isInstalled(): Boolean {
        return try {
            val status = HealthConnectClient.getSdkStatus(context)
            status == HealthConnectClient.SDK_AVAILABLE ||
            status == HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Verifica si Health Connect necesita actualización.
     */
    fun needsUpdate(): Boolean {
        return try {
            HealthConnectClient.getSdkStatus(context) == HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Verifica si Health Connect está disponible y listo para usar.
     */
    fun isAvailable(): Boolean {
        return try {
            HealthConnectClient.getSdkStatus(context) == HealthConnectClient.SDK_AVAILABLE
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Verifica si los permisos necesarios están otorgados.
     */
    suspend fun hasPermissions(): Boolean {
        val client = healthConnectClient ?: return false
        return try {
            val granted = client.permissionController.getGrantedPermissions()
            PERMISSIONS.all { it in granted }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking permissions", e)
            false
        }
    }

    /**
     * Obtiene las lecturas de frecuencia cardíaca de los últimos minutos.
     */
    suspend fun getHeartRateReadings(lastMinutes: Int = 15): List<VitalReading> {
        val client = healthConnectClient ?: return emptyList()

        return try {
            val endTime = Instant.now()
            val startTime = endTime.minus(lastMinutes.toLong(), ChronoUnit.MINUTES)

            val request = ReadRecordsRequest(
                recordType = HeartRateRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )

            val response = client.readRecords(request)

            response.records.flatMap { record ->
                record.samples.map { sample ->
                    VitalReading(
                        id = UUID.randomUUID().toString(),
                        type = VitalType.HR,
                        value = sample.beatsPerMinute.toDouble(),
                        unit = "bpm",
                        timestamp = sample.time,
                        quality = VitalQuality.VALID,
                        sourceDeviceId = "health_connect",
                        sessionId = null
                    )
                }
            }.also {
                Log.d(TAG, "Got ${it.size} heart rate readings from Health Connect")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error reading heart rate", e)
            emptyList()
        }
    }

    /**
     * Obtiene las lecturas de SpO2 de los últimos minutos.
     */
    suspend fun getOxygenSaturationReadings(lastMinutes: Int = 15): List<VitalReading> {
        val client = healthConnectClient ?: return emptyList()

        return try {
            val endTime = Instant.now()
            val startTime = endTime.minus(lastMinutes.toLong(), ChronoUnit.MINUTES)

            val request = ReadRecordsRequest(
                recordType = OxygenSaturationRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )

            val response = client.readRecords(request)

            response.records.map { record ->
                VitalReading(
                    id = UUID.randomUUID().toString(),
                    type = VitalType.SPO2,
                    value = record.percentage.value,
                    unit = "%",
                    timestamp = record.time,
                    quality = VitalQuality.VALID,
                    sourceDeviceId = "health_connect",
                    sessionId = null
                )
            }.also {
                Log.d(TAG, "Got ${it.size} SpO2 readings from Health Connect")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error reading SpO2", e)
            emptyList()
        }
    }

    /**
     * Flujo continuo de lecturas de frecuencia cardíaca.
     * Consulta Health Connect cada 5 segundos.
     */
    fun heartRateFlow(): Flow<VitalReading> = flow {
        while (true) {
            val readings = getHeartRateReadings(1) // Último minuto
            readings.lastOrNull()?.let { reading ->
                emit(reading)
            }
            kotlinx.coroutines.delay(5000) // Cada 5 segundos
        }
    }
}

