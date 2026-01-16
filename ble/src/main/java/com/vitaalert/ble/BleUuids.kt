package com.vitaalert.ble

import java.util.UUID

/**
 * BLE UUIDs for HR and SpO2 services.
 */
object BleUuids {
    val HEART_RATE_SERVICE: UUID = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb")
    val HEART_RATE_MEASUREMENT: UUID = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb")
    val SPO2_SERVICE: UUID = UUID.fromString("00001822-0000-1000-8000-00805f9b34fb")
}
