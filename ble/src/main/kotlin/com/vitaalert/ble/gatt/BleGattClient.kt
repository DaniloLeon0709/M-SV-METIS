package com.vitaalert.ble.gatt

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID

/**
 * GATT helpers for standard Heart Rate and optional SpO2 services.
 */
class BleGattClient {
    /**
     * Returns the UUID for the Heart Rate service (0x180D).
     */
    val heartRateService: UUID = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb")

    /**
     * Returns the UUID for the Heart Rate Measurement characteristic (0x2A37).
     */
    val heartRateMeasurement: UUID = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb")

    /**
     * Returns the UUID for the SpO2 service (0x1822).
     */
    val spo2Service: UUID = UUID.fromString("00001822-0000-1000-8000-00805f9b34fb")

    /**
     * Parses the heart rate measurement characteristic payload.
     */
    fun parseHeartRate(bytes: ByteArray): Int? {
        if (bytes.isEmpty()) return null
        val flags = bytes[0].toInt()
        val is16Bit = flags and 0x01 == 0x01
        return if (is16Bit) {
            if (bytes.size < 3) return null
            ByteBuffer.wrap(bytes, 1, 2).order(ByteOrder.LITTLE_ENDIAN).short.toInt()
        } else {
            if (bytes.size < 2) return null
            bytes[1].toInt() and 0xFF
        }
    }

    /**
     * Returns true if a SpO2 service is advertised by the device.
     */
    fun supportsSpo2(services: List<UUID>): Boolean {
        return services.contains(spo2Service)
    }
}
