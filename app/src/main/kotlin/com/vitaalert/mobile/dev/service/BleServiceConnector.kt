package com.vitaalert.mobile.dev.service

import com.vitaalert.ble.service.BleServiceLocator
import com.vitaalert.ble.service.BleVitalSource
import com.vitaalert.ble.service.DemoVitalSourceAdapter
import com.vitaalert.ble.service.Dependencies
import com.vitaalert.domain.repository.VitalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Connects BLE monitoring dependencies to the service locator.
 */
class BleServiceConnector(
    private val repository: VitalRepository,
    private val demoSource: DemoVitalSourceAdapter,
    private val bleSource: BleVitalSource
) {
    private val _demoMode = MutableStateFlow(false)

    /**
     * Exposes the current demo mode setting.
     */
    val demoMode: StateFlow<Boolean> = _demoMode

    /**
     * Initializes the service locator with the default source.
     */
    fun initialize() {
        updateDependencies()
    }

    /**
     * Enables or disables demo mode.
     */
    fun setDemoMode(enabled: Boolean) {
        _demoMode.value = enabled
        updateDependencies()
    }

    private fun updateDependencies() {
        val source = if (_demoMode.value) demoSource else bleSource
        BleServiceLocator.dependencies = Dependencies(repository = repository, source = source)
    }
}
