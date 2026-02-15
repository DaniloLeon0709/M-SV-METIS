package com.vitaalert.mobile.dev.di

import android.content.Context
import com.vitaalert.ble.demo.DemoVitalSource
import com.vitaalert.ble.gatt.BleGattClient
import com.vitaalert.ble.scanner.BleScanner
import com.vitaalert.ble.service.BleVitalSource
import com.vitaalert.ble.service.DemoVitalSourceAdapter
import com.vitaalert.ble.service.VitalSource
import com.vitaalert.domain.repository.VitalRepository
import com.vitaalert.mobile.dev.service.BleServiceConnector
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers

/**
 * Provides BLE-related dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppBleModule {
    /**
     * Provides the BLE scanner.
     */
    @Provides
    @Singleton
    fun provideBleScanner(@ApplicationContext context: Context): BleScanner = BleScanner(context)

    /**
     * Provides the BLE GATT client.
     */
    @Provides
    @Singleton
    fun provideBleGattClient(): BleGattClient = BleGattClient()

    /**
     * Provides the demo vital source.
     */
    @Provides
    @Singleton
    fun provideDemoVitalSource(): DemoVitalSource = DemoVitalSource(Dispatchers.IO)

    /**
     * Provides the demo vital source adapter.
     */
    @Provides
    @Singleton
    fun provideDemoVitalSourceAdapter(demoVitalSource: DemoVitalSource): DemoVitalSourceAdapter {
        return DemoVitalSourceAdapter(demoVitalSource)
    }

    /**
     * Provides the BLE vital source.
     */
    @Provides
    @Singleton
    fun provideBleVitalSource(
        @ApplicationContext context: Context,
        gattClient: BleGattClient
    ): BleVitalSource = BleVitalSource(context, gattClient)

    /**
     * Provides the service connector.
     */
    @Provides
    @Singleton
    fun provideBleServiceConnector(
        repository: VitalRepository,
        demoVitalSourceAdapter: DemoVitalSourceAdapter,
        bleVitalSource: BleVitalSource
    ): BleServiceConnector {
        return BleServiceConnector(repository, demoVitalSourceAdapter, bleVitalSource)
    }

    /**
     * Provides the default vital source (demo by default).
     */
    @Provides
    fun provideVitalSource(demoVitalSourceAdapter: DemoVitalSourceAdapter): VitalSource =
        demoVitalSourceAdapter
}
