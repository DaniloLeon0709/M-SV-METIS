package com.vitaalert.core.dispatchers

import kotlinx.coroutines.CoroutineDispatcher

/**
 * Provides coroutine dispatchers for VitaAlert.
 */
interface DispatcherProvider {
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
}
