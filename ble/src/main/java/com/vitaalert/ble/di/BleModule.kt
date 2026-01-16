package com.vitaalert.ble.di

import com.vitaalert.ble.BleMonitor
import com.vitaalert.ble.demo.DemoBleMonitor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module to provide BLE monitoring.
 */
@Module
@InstallIn(SingletonComponent::class)
object BleModule {
    /**
     * Provides the demo BLE monitor implementation.
     */
    @Provides
    @Singleton
    fun provideBleMonitor(monitor: DemoBleMonitor): BleMonitor = monitor
}
