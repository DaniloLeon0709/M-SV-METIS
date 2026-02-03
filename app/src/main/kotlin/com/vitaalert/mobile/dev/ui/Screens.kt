package com.vitaalert.mobile.dev.ui

import android.graphics.Color as AndroidColor
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BluetoothSearching
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.MonitorHeart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.vitaalert.designsystem.ChartContainer
import com.vitaalert.designsystem.DeviceCard
import com.vitaalert.designsystem.EmptyState
import com.vitaalert.designsystem.PrimaryButton
import com.vitaalert.designsystem.SecondaryButton
import com.vitaalert.designsystem.SectionHeader
import com.vitaalert.designsystem.VitalCard
import com.vitaalert.designsystem.VitalState
import com.vitaalert.designsystem.VitaAlertColors
import com.vitaalert.domain.model.VitalReading
import java.time.Duration
import java.time.Instant

/**
 * Splash screen with animated logo and loading indicator.
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        VitaAlertColors.Primary,
                        VitaAlertColors.PrimaryDark
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Logo container
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        color = VitaAlertColors.OnPrimary.copy(alpha = 0.1f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.MonitorHeart,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = VitaAlertColors.OnPrimary
                )
            }

            Text(
                text = "VitaAlert",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = VitaAlertColors.OnPrimary
            )

            Text(
                text = "Monitoreo de signos vitales",
                style = MaterialTheme.typography.bodyLarge,
                color = VitaAlertColors.OnPrimary.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            CircularProgressIndicator(
                modifier = Modifier.size(32.dp),
                color = VitaAlertColors.OnPrimary,
                strokeWidth = 3.dp
            )
        }
    }
}

/**
 * Modern login screen with styled inputs and branding.
 */
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var email by remember { mutableStateOf("user@test.com") }
    var password by remember { mutableStateOf("Password123!") }
    var passwordVisible by remember { mutableStateOf(false) }

    if (state.isSuccess) {
        LaunchedEffect(Unit) { onLoginSuccess() }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            VitaAlertColors.Primary,
                            VitaAlertColors.PrimaryLight.copy(alpha = 0.3f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
        ) {
            // Header content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .padding(top = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            color = VitaAlertColors.OnPrimary.copy(alpha = 0.15f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.MonitorHeart,
                        contentDescription = null,
                        modifier = Modifier.size(44.dp),
                        tint = VitaAlertColors.OnPrimary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Bienvenido",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = VitaAlertColors.OnPrimary
                )

                Text(
                    text = "Inicia sesión para continuar",
                    style = MaterialTheme.typography.bodyMedium,
                    color = VitaAlertColors.OnPrimary.copy(alpha = 0.8f)
                )
            }

            // Login form card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 24.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Correo electrónico") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VitaAlertColors.Primary,
                            focusedLabelColor = VitaAlertColors.Primary
                        ),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Contraseña") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible)
                                        Icons.Default.Visibility
                                    else
                                        Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VitaAlertColors.Primary,
                            focusedLabelColor = VitaAlertColors.Primary
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    PrimaryButton(
                        text = "Iniciar sesión",
                        onClick = { viewModel.login(email, password) },
                        isLoading = state.isLoading
                    )

                    AnimatedVisibility(
                        visible = state.errorMessage != null,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        state.errorMessage?.let { message ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = VitaAlertColors.VitalCriticalLight
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = message,
                                    modifier = Modifier.padding(16.dp),
                                    color = VitaAlertColors.VitalCritical,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }

            // Demo credentials hint
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🔑 Credenciales de prueba",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "user@test.com / Password123!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

/**
 * Home dashboard with vitals cards and trend chart.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeDashboardScreen(
    onDevicePairing: () -> Unit,
    viewModel: HomeDashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val hrSeries by viewModel.heartRateSeries.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text(
                            text = "¡Hola! 👋",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Tu salud hoy",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Vitals section header
            item {
                SectionHeader(
                    title = "Signos vitales",
                    subtitle = "Actualizados en tiempo real"
                )
            }

            // Heart Rate Card
            item {
                uiState.heartRate?.let { card ->
                    VitalCard(
                        value = card.value,
                        label = "Frecuencia Cardíaca",
                        unit = "bpm",
                        state = card.state,
                        icon = Icons.Default.Favorite
                    )
                } ?: VitalCard(
                    value = "--",
                    label = "Frecuencia Cardíaca",
                    unit = "bpm",
                    state = VitalState.NORMAL,
                    icon = Icons.Default.Favorite
                )
            }

            // SpO2 Card
            item {
                uiState.spo2?.let { card ->
                    VitalCard(
                        value = card.value,
                        label = "Saturación de Oxígeno",
                        unit = "%",
                        state = card.state,
                        icon = Icons.Outlined.Air
                    )
                } ?: VitalCard(
                    value = "--",
                    label = "Saturación de Oxígeno",
                    unit = "%",
                    state = VitalState.NORMAL,
                    icon = Icons.Outlined.Air
                )
            }

            // Chart section
            item {
                Spacer(modifier = Modifier.height(8.dp))
                ChartContainer(
                    title = "Tendencia cardíaca",
                    subtitle = "Últimos 15 minutos"
                ) {
                    HeartRateChart(readings = hrSeries)
                }
            }

            // Device pairing button
            item {
                Spacer(modifier = Modifier.height(8.dp))
                PrimaryButton(
                    text = "Emparejar dispositivo",
                    onClick = onDevicePairing
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

/**
 * Device pairing screen with BLE devices list and demo mode.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevicePairingScreen(
    onBack: () -> Unit,
    viewModel: DevicePairingViewModel = hiltViewModel()
) {
    val devices by viewModel.devices.collectAsState()
    val demoMode by viewModel.demoMode.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Dispositivos",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Demo mode card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (demoMode)
                            VitaAlertColors.VitalNormalLight
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(
                                        color = if (demoMode)
                                            VitaAlertColors.VitalNormal.copy(alpha = 0.2f)
                                        else
                                            MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Science,
                                    contentDescription = null,
                                    tint = if (demoMode)
                                        VitaAlertColors.VitalNormal
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Column {
                                Text(
                                    text = "Modo Demo",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = if (demoMode) "Activado" else "Datos simulados",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        SecondaryButton(
                            text = if (demoMode) "Activado ✓" else "Activar",
                            onClick = { viewModel.setDemoMode(true) },
                            modifier = Modifier.width(130.dp)
                        )
                    }
                }
            }

            // Divider
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // BLE Devices section
            item {
                SectionHeader(
                    title = "Dispositivos BLE",
                    subtitle = "Cercanos disponibles"
                )
            }

            if (devices.isEmpty()) {
                item {
                    EmptyState(
                        title = "Sin dispositivos",
                        subtitle = "Activa Bluetooth y acerca un dispositivo compatible",
                        icon = Icons.Default.BluetoothSearching
                    )
                }
            } else {
                items(devices) { device ->
                    DeviceCard(
                        name = device.name ?: "Dispositivo desconocido",
                        subtitle = device.id,
                        isConnected = false
                    )
                }
            }

            // Start monitoring button
            item {
                Spacer(modifier = Modifier.height(16.dp))
                PrimaryButton(
                    text = "Iniciar monitoreo",
                    onClick = { viewModel.startMonitoring() }
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun HeartRateChart(readings: List<VitalReading>) {
    val context = LocalContext.current
    val now = Instant.now()
    val isDarkTheme = MaterialTheme.colorScheme.background.luminance() < 0.5f

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(12.dp)),
        factory = { ctx ->
            LineChart(ctx).apply {
                description.isEnabled = false
                legend.isEnabled = false
                setTouchEnabled(true)
                setDrawGridBackground(false)

                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    textColor = if (isDarkTheme)
                        AndroidColor.parseColor("#B0B0B0")
                    else
                        AndroidColor.parseColor("#5C5C5C")
                    axisLineColor = if (isDarkTheme)
                        AndroidColor.parseColor("#3D3D3D")
                    else
                        AndroidColor.parseColor("#E0E0E0")
                }

                axisLeft.apply {
                    setDrawGridLines(true)
                    gridColor = if (isDarkTheme)
                        AndroidColor.parseColor("#2D2D2D")
                    else
                        AndroidColor.parseColor("#F0F0F0")
                    textColor = if (isDarkTheme)
                        AndroidColor.parseColor("#B0B0B0")
                    else
                        AndroidColor.parseColor("#5C5C5C")
                    axisLineColor = if (isDarkTheme)
                        AndroidColor.parseColor("#3D3D3D")
                    else
                        AndroidColor.parseColor("#E0E0E0")
                }

                axisRight.isEnabled = false
                setBackgroundColor(AndroidColor.TRANSPARENT)
            }
        },
        update = { chart ->
            val entries = readings.map { reading ->
                val minutesAgo = Duration.between(reading.timestamp, now).toMinutes().toFloat()
                Entry(15f - minutesAgo, reading.value.toFloat())
            }

            val dataSet = LineDataSet(entries, "HR").apply {
                color = AndroidColor.parseColor("#F9C642")
                setDrawCircles(false)
                lineWidth = 3f
                mode = LineDataSet.Mode.CUBIC_BEZIER
                setDrawFilled(true)
                fillColor = AndroidColor.parseColor("#F9C642")
                fillAlpha = 30
                setDrawValues(false)
                highLightColor = AndroidColor.parseColor("#E5B438")
            }

            chart.data = LineData(dataSet)
            chart.animateX(300)
            chart.invalidate()
        }
    )
}

// Extension function to check luminance
private fun androidx.compose.ui.graphics.Color.luminance(): Float {
    return (0.299f * red + 0.587f * green + 0.114f * blue)
}
