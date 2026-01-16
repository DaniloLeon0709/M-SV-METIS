package com.vitaalert.ble

/**
 * Abstraction for BLE monitoring control.
 */
interface BleMonitor {
    /**
     * Starts monitoring sensors.
     */
    fun startMonitoring()

    /**
     * Stops monitoring sensors.
     */
    fun stopMonitoring()
}
