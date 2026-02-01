package com.vitaalert.ble.scanner

import com.vitaalert.ble.model.BleDevice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * BLE scanner that exposes discovered devices as a flow.
 */
class BleScanner {
    private val devices = MutableStateFlow<List<BleDevice>>(emptyList())

    /**
     * Returns a flow of discovered BLE devices.
     */
    fun scan(): Flow<List<BleDevice>> = devices
}
