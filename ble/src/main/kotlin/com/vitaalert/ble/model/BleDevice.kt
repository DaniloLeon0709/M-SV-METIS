package com.vitaalert.ble.model

/**
 * Represents a BLE device discovered during scanning.
 */
data class BleDevice(
    val id: String,
    val name: String?,
    val rssi: Int
)
