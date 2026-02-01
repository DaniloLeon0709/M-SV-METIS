package com.vitaalert.mobile.dev.ui

import android.graphics.Color as AndroidColor
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.vitaalert.designsystem.PrimaryButton
import com.vitaalert.designsystem.VitalCard
import com.vitaalert.designsystem.VitalState
import com.vitaalert.domain.model.VitalReading
import java.time.Duration
import java.time.Instant

/**
 * Splash screen that determines the initial route based on session state.
 */
@Composable
fun SplashScreen(
    viewModel: SplashViewModel = hiltViewModel(),
    onNavigate: (String) -> Unit
) {
    val state by viewModel.sessionState.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.initialize()
    }
    LaunchedEffect(state) {
        val destination = when (state) {
            is com.vitaalert.auth.session.SessionState.Authenticated -> "home"
            else -> "login"
        }
        onNavigate(destination)
    }
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "VitaAlert", style = MaterialTheme.typography.headlineMedium)
    }
}

/**
 * Login screen for user authentication.
 */
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var email by remember { mutableStateOf("user@test.com") }
    var password by remember { mutableStateOf("Password123!") }

    if (state.isSuccess) {
        LaunchedEffect(Unit) { onLoginSuccess() }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Inicia sesión", style = MaterialTheme.typography.headlineSmall)
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Email") }
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Contraseña") }
        )
        PrimaryButton(text = if (state.isLoading) "Ingresando..." else "Ingresar") {
            viewModel.login(email, password)
        }
        state.errorMessage?.let { message ->
            Text(text = message, color = MaterialTheme.colorScheme.error)
        }
    }
}

/**
 * Dashboard screen showing vitals and a heart rate trend chart.
 */
@Composable
fun HomeDashboardScreen(
    onDevicePairing: () -> Unit,
    viewModel: HomeDashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val hrSeries by viewModel.heartRateSeries.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Dashboard", style = MaterialTheme.typography.headlineSmall)
        uiState.heartRate?.let { card ->
            VitalCard(value = card.value, label = card.label, state = card.state)
        } ?: VitalCard(value = "--", label = "HR (bpm)", state = VitalState.NORMAL)

        uiState.spo2?.let { card ->
            VitalCard(value = card.value, label = card.label, state = card.state)
        } ?: VitalCard(value = "--", label = "SpO2 (%)", state = VitalState.NORMAL)

        HeartRateChart(readings = hrSeries)

        Button(onClick = onDevicePairing) {
            Text("Emparejar dispositivo")
        }
    }
}

/**
 * Device pairing screen with BLE devices and demo controls.
 */
@Composable
fun DevicePairingScreen(
    onBack: () -> Unit,
    viewModel: DevicePairingViewModel = hiltViewModel()
) {
    val devices by viewModel.devices.collectAsState()
    val demoMode by viewModel.demoMode.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Dispositivos", style = MaterialTheme.typography.headlineSmall)
        devices.forEach { device ->
            Text(text = device.name ?: device.id)
        }
        PrimaryButton(text = if (demoMode) "Demo activado" else "Activar demo") {
            viewModel.setDemoMode(true)
        }
        PrimaryButton(text = "Iniciar monitoreo") {
            viewModel.startMonitoring()
        }
        Button(onClick = onBack) {
            Text("Volver")
        }
    }
}

@Composable
private fun HeartRateChart(readings: List<VitalReading>) {
    val context = LocalContext.current
    val now = Instant.now()
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        factory = { LineChart(context).apply { description.isEnabled = false } },
        update = { chart ->
            val entries = readings.map { reading ->
                val minutesAgo = Duration.between(reading.timestamp, now).toMinutes().toFloat()
                Entry(15f - minutesAgo, reading.value.toFloat())
            }
            val dataSet = LineDataSet(entries, "HR").apply {
                color = AndroidColor.parseColor("#F9C642")
                setDrawCircles(false)
                lineWidth = 2f
            }
            chart.data = LineData(dataSet)
            chart.invalidate()
        }
    )
}
