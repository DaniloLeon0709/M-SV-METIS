package com.vitaalert.ble.permissions

import android.Manifest
import android.os.Build

/**
 * Helper for BLE permissions based on Android version.
 */
object BlePermissions {
    /**
     * Returns the required permissions to scan for BLE devices.
     */
    fun scanPermissions(): List<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            listOf(Manifest.permission.BLUETOOTH_SCAN)
        } else {
            listOf(Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN)
        }
    }

    /**
     * Returns the required permissions to connect to BLE devices.
     */
    fun connectPermissions(): List<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            listOf(Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            listOf(Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN)
        }
    }
}
