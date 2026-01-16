package com.vitaalert.ble

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService

/**
 * Placeholder GATT client describing supported services for HR and SpO2.
 */
class BleGattClient {
    /**
     * Returns the standard Heart Rate service if available.
     */
    fun findHeartRateService(services: List<BluetoothGattService>): BluetoothGattService? {
        return services.firstOrNull { it.uuid == BleUuids.HEART_RATE_SERVICE }
    }

    /**
     * Returns the Heart Rate Measurement characteristic for notifications.
     */
    fun findHeartRateMeasurement(service: BluetoothGattService): BluetoothGattCharacteristic? {
        return service.getCharacteristic(BleUuids.HEART_RATE_MEASUREMENT)
    }

    /**
     * Returns the SpO2 service when supported by the device.
     */
    fun findSpo2Service(services: List<BluetoothGattService>): BluetoothGattService? {
        return services.firstOrNull { it.uuid == BleUuids.SPO2_SERVICE }
    }

    /**
     * Enables notifications on a given characteristic.
     */
    fun enableNotifications(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
        gatt.setCharacteristicNotification(characteristic, true)
    }
}
