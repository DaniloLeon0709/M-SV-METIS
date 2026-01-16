package com.vitaalert.mobile.dev

import android.app.Application
import com.vitaalert.mobile.dev.service.BleServiceConnector
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Application entry point for VitaAlert.
 */
@HiltAndroidApp
class VitaAlertApp : Application() {
    @Inject
    lateinit var bleServiceConnector: BleServiceConnector

    override fun onCreate() {
        super.onCreate()
        bleServiceConnector.initialize()
    }
}
