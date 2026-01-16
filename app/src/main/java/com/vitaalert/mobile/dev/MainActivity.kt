package com.vitaalert.mobile.dev

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.content.ContextCompat
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.vitaalert.mobile.dev.ui.MainViewModel
import com.vitaalert.mobile.dev.monitor.MonitoringService
import com.vitaalert.designsystem.VitaAlertTheme

/**
 * VitaAlert entry activity.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ContextCompat.startForegroundService(
            this,
            Intent(this, MonitoringService::class.java)
        )
        setContent {
            VitaAlertTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
                    MainScreen(
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }
        }
    }
}

/**
 * Main screen showing a quick sensor summary.
 */
@Composable
private fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    Text(
        modifier = modifier,
        text = uiState.summary,
        style = MaterialTheme.typography.bodyLarge
    )
}
