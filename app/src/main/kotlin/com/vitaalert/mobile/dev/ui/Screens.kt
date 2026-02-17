package com.vitaalert.mobile.dev.ui

import android.graphics.Color as AndroidColor
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.BluetoothDisabled
import androidx.compose.material.icons.filled.BluetoothSearching
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.MonitorHeart
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
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
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.core.content.ContextCompat

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
    onRegisterClick: () -> Unit = {},
    onGoogleSignIn: () -> Unit = {},
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
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

                    // Divider with "o continúa con"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                        Text(
                            text = "  o continúa con  ",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }

                    // Google Sign In Button
                    OutlinedButton(
                        onClick = onGoogleSignIn,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Google Icon (using a simple G text as placeholder)
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(
                                        color = Color.Transparent,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "G",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4285F4)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Continuar con Google",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Register link
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "¿No tienes una cuenta? ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Regístrate",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = VitaAlertColors.Primary,
                    modifier = Modifier.clickable { onRegisterClick() }
                )
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
    onProfileClick: () -> Unit = {},
    onHistoryClick: () -> Unit = {},
    onEmergencyClick: () -> Unit = {},
    viewModel: HomeDashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val hrSeries by viewModel.heartRateSeries.collectAsState(initial = emptyList())
    val context = LocalContext.current

    // Estado para mostrar diálogo de emergencia
    var showEmergencyDialog by remember { mutableStateOf(false) }

    // Diálogo de confirmación de emergencia
    if (showEmergencyDialog) {
        AlertDialog(
            onDismissRequest = { showEmergencyDialog = false },
            containerColor = MaterialTheme.colorScheme.surface,
            icon = {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            VitaAlertColors.VitalCritical.copy(alpha = 0.1f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = VitaAlertColors.VitalCritical,
                        modifier = Modifier.size(32.dp)
                    )
                }
            },
            title = {
                Text(
                    text = "¿Llamar a Emergencias?",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Esto llamará al número de emergencias 123",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Se compartirá tu ubicación actual con los servicios de emergencia",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                androidx.compose.material3.Button(
                    onClick = {
                        showEmergencyDialog = false
                        // Llamar a emergencias (123 en Colombia)
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:123")
                        }
                        context.startActivity(intent)
                    },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = VitaAlertColors.VitalCritical
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Llamar 123")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEmergencyDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

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
                actions = {
                    IconButton(onClick = onProfileClick) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    VitaAlertColors.Primary.copy(alpha = 0.1f),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Perfil",
                                tint = VitaAlertColors.Primary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            // Emergency button
            androidx.compose.material3.FloatingActionButton(
                onClick = { showEmergencyDialog = true },
                containerColor = VitaAlertColors.VitalCritical,
                contentColor = VitaAlertColors.OnPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Emergencia"
                )
            }
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
            // Quick actions row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.History,
                        label = "Historial",
                        onClick = onHistoryClick
                    )
                    QuickActionCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Bluetooth,
                        label = "Dispositivos",
                        onClick = onDevicePairing
                    )
                }
            }

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

            // View history button
            item {
                Spacer(modifier = Modifier.height(8.dp))
                SecondaryButton(
                    text = "Ver historial completo",
                    onClick = onHistoryClick,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

/**
 * Quick action card for dashboard.
 */
@Composable
private fun QuickActionCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        VitaAlertColors.Primary.copy(alpha = 0.1f),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = VitaAlertColors.Primary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * Device pairing screen - simplified version.
 * Shows bonded devices and allows to start monitoring.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevicePairingScreen(
    onBack: () -> Unit,
    viewModel: DevicePairingViewModel = hiltViewModel()
) {
    val devices by viewModel.devices.collectAsState()
    val isMonitoring by viewModel.isMonitoring.collectAsState()
    val connectionStatus by viewModel.connectionStatus.collectAsState()
    val connectedDevice by viewModel.connectedDevice.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()
    val context = LocalContext.current

    // Definir los permisos BLE requeridos según la versión de Android
    val blePermissions = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }

    // Función para verificar si los permisos están concedidos
    fun hasBluetoothPermissions(): Boolean {
        return blePermissions.all { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    // Launcher para solicitar permisos
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            viewModel.startScan()
        } else {
            Toast.makeText(context, "Se requieren permisos Bluetooth", Toast.LENGTH_LONG).show()
        }
    }

    // Función para abrir configuración Bluetooth del sistema
    fun openBluetoothSettings() {
        try {
            val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "No se pudo abrir la configuración Bluetooth", Toast.LENGTH_SHORT).show()
        }
    }

    // Función para iniciar/detener monitoreo
    fun toggleMonitoring() {
        if (isMonitoring) {
            viewModel.stopMonitoring()
            Toast.makeText(context, "Monitoreo detenido", Toast.LENGTH_SHORT).show()
        } else {
            if (devices.isEmpty()) {
                Toast.makeText(context, "No hay dispositivos compatibles. Vincula un reloj desde configuración Bluetooth.", Toast.LENGTH_LONG).show()
                return
            }
            if (hasBluetoothPermissions()) {
                viewModel.startMonitoring()
            } else {
                permissionLauncher.launch(blePermissions)
            }
        }
    }

    // Cargar dispositivos al abrir la pantalla (solo si no estamos monitoreando)
    LaunchedEffect(Unit) {
        if (!isMonitoring) {
            if (hasBluetoothPermissions()) {
                viewModel.startScan()
            } else {
                permissionLauncher.launch(blePermissions)
            }
        }
    }

    // Detener escaneo al salir (pero no el monitoreo)
    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopScan()
        }
    }

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Estado de conexión
            ConnectionStatusCard(
                status = connectionStatus,
                isMonitoring = isMonitoring,
                statusMessage = statusMessage
            )

            // Si está monitoreando, mostrar solo el dispositivo conectado
            if (isMonitoring && connectedDevice != null) {
                Text(
                    text = "Dispositivo en uso",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = VitaAlertColors.VitalNormalLight
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(
                                    VitaAlertColors.VitalNormal.copy(alpha = 0.2f),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = VitaAlertColors.VitalNormal
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = connectedDevice!!.name,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Medium,
                                color = VitaAlertColors.VitalNormal
                            )
                            Text(
                                text = "Monitoreando signos vitales",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = VitaAlertColors.VitalNormal
                        )
                    }
                }
            } else {
                // Si no está monitoreando, mostrar opciones para conectar

                // Estado para mostrar diálogos de info
                var showHealthConnectInfo by remember { mutableStateOf(false) }
                var showBleInfo by remember { mutableStateOf(false) }

                // Diálogo de info de Health Connect
                if (showHealthConnectInfo) {
                    AlertDialog(
                        onDismissRequest = { showHealthConnectInfo = false },
                        title = { Text("¿Cómo funciona Health Connect?") },
                        text = {
                            Column {
                                Text("Health Connect es el estándar de Google para compartir datos de salud entre aplicaciones.")
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Funciona así:")
                                Text("1. Tu reloj sincroniza datos con su app (Huawei Health, Samsung Health, etc.)")
                                Text("2. Esa app comparte los datos con Health Connect")
                                Text("3. VitaAlert lee los datos desde Health Connect")
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("⚠️ Los datos no son en tiempo real exacto, dependen de la sincronización de tu app de salud.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error)
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showHealthConnectInfo = false }) {
                                Text("Entendido")
                            }
                        }
                    )
                }

                // Diálogo de info de BLE
                if (showBleInfo) {
                    AlertDialog(
                        onDismissRequest = { showBleInfo = false },
                        title = { Text("¿Cómo funciona la conexión BLE?") },
                        text = {
                            Column {
                                Text("La conexión BLE directa lee datos en tiempo real desde dispositivos que exponen el servicio Heart Rate estándar (UUID 0x180D).")
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Dispositivos compatibles:")
                                Text("• Polar (H10, OH1, Verity Sense)")
                                Text("• Garmin (HRM-Pro, HRM-Dual)")
                                Text("• Wahoo (TICKR)")
                                Text("• Algunos Fitbit y Samsung")
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("⚠️ Huawei, Xiaomi y Apple Watch NO son compatibles porque usan protocolos propietarios.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error)
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showBleInfo = false }) {
                                Text("Entendido")
                            }
                        }
                    )
                }

                // Tarjeta de Health Connect
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            when {
                                !viewModel.isHealthConnectInstalled -> {
                                    // No está instalado, abrir Play Store
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(viewModel.getHealthConnectPlayStoreUrl()))
                                    context.startActivity(intent)
                                    Toast.makeText(context, "Instala Health Connect para continuar", Toast.LENGTH_LONG).show()
                                }
                                viewModel.healthConnectNeedsUpdate -> {
                                    // Necesita actualización
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(viewModel.getHealthConnectPlayStoreUrl()))
                                    context.startActivity(intent)
                                    Toast.makeText(context, "Actualiza Health Connect para continuar", Toast.LENGTH_LONG).show()
                                }
                                viewModel.isHealthConnectAvailable -> {
                                    // Disponible, iniciar monitoreo
                                    viewModel.startHealthConnectMonitoring()
                                    Toast.makeText(context, "Iniciando con Health Connect...", Toast.LENGTH_SHORT).show()
                                }
                                else -> {
                                    Toast.makeText(context, "Health Connect no disponible", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (viewModel.isHealthConnectAvailable)
                            VitaAlertColors.VitalNormalLight
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    if (viewModel.isHealthConnectAvailable)
                                        VitaAlertColors.VitalNormal.copy(alpha = 0.2f)
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f),
                                    RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = if (viewModel.isHealthConnectAvailable)
                                    VitaAlertColors.VitalNormal
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Health Connect",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium,
                                color = if (viewModel.isHealthConnectAvailable)
                                    VitaAlertColors.VitalNormal
                                else
                                    MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = when {
                                    !viewModel.isHealthConnectInstalled -> "Toca para instalar desde Play Store"
                                    viewModel.healthConnectNeedsUpdate -> "Toca para actualizar"
                                    viewModel.isHealthConnectAvailable -> "Lee datos de Huawei Health, Samsung Health, etc."
                                    else -> "No disponible"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        // Ícono de info
                        IconButton(
                            onClick = { showHealthConnectInfo = true },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Información",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        // Ícono de estado
                        Icon(
                            imageVector = when {
                                !viewModel.isHealthConnectInstalled -> Icons.Default.Add
                                viewModel.healthConnectNeedsUpdate -> Icons.Default.Refresh
                                viewModel.isHealthConnectAvailable -> Icons.Default.CheckCircle
                                else -> Icons.Default.Close
                            },
                            contentDescription = null,
                            tint = if (viewModel.isHealthConnectAvailable)
                                VitaAlertColors.VitalNormal
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Text(
                    text = "— o conecta directamente —",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                // Tarjeta de conexión BLE directa
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.startMonitoring()
                            Toast.makeText(context, "Buscando dispositivos BLE...", Toast.LENGTH_SHORT).show()
                        },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    VitaAlertColors.Primary.copy(alpha = 0.1f),
                                    RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bluetooth,
                                contentDescription = null,
                                tint = VitaAlertColors.Primary
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Conexión BLE directa",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Tiempo real con Polar, Garmin, Wahoo",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        // Ícono de info
                        IconButton(
                            onClick = { showBleInfo = true },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Información",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.Bluetooth,
                            contentDescription = null,
                            tint = VitaAlertColors.Primary
                        )
                    }
                }

                // Dispositivos compatibles encontrados
                if (devices.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Dispositivos compatibles",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${devices.size}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Mostrar solo el primer dispositivo compatible
                    val device = devices.first()
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(
                                        VitaAlertColors.Primary.copy(alpha = 0.1f),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bluetooth,
                                    contentDescription = null,
                                    tint = VitaAlertColors.Primary
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = device.name ?: "Dispositivo",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Listo para conectar",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = VitaAlertColors.Primary
                                )
                            }
                        }
                    }
                } else {
                    // No hay dispositivos compatibles
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bluetooth,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No hay dispositivos compatibles",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Vincula un reloj o pulsera con sensor de frecuencia cardíaca",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Botón de iniciar/detener monitoreo
            if (isMonitoring) {
                // Botón de detener (rojo)
                androidx.compose.material3.Button(
                    onClick = { toggleMonitoring() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = VitaAlertColors.VitalCritical
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Detener monitoreo",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            } else {
                PrimaryButton(
                    text = if (devices.isNotEmpty()) "Iniciar monitoreo" else "Vincular dispositivo primero",
                    onClick = {
                        if (devices.isEmpty()) {
                            openBluetoothSettings()
                        } else {
                            toggleMonitoring()
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/**
 * Card showing connection status.
 */
@Composable
private fun ConnectionStatusCard(
    status: ConnectionStatus,
    isMonitoring: Boolean,
    statusMessage: String = ""
) {
    val (backgroundColor, iconColor, icon, title) = when {
        isMonitoring && status == ConnectionStatus.CONNECTED -> {
            Tuple4(
                VitaAlertColors.VitalNormalLight,
                VitaAlertColors.VitalNormal,
                Icons.Default.CheckCircle,
                "Conectado y monitoreando"
            )
        }
        isMonitoring && status == ConnectionStatus.CONNECTING -> {
            Tuple4(
                VitaAlertColors.Primary.copy(alpha = 0.1f),
                VitaAlertColors.Primary,
                Icons.Default.Bluetooth,
                "Conectando..."
            )
        }
        isMonitoring && status == ConnectionStatus.DISCONNECTED -> {
            Tuple4(
                VitaAlertColors.VitalAlertLight,
                VitaAlertColors.VitalAlert,
                Icons.Default.Warning,
                "Conexión perdida"
            )
        }
        else -> {
            Tuple4(
                MaterialTheme.colorScheme.surfaceVariant,
                MaterialTheme.colorScheme.onSurfaceVariant,
                Icons.Default.BluetoothDisabled,
                "Sin monitoreo activo"
            )
        }
    }

    // Usar el mensaje de estado si está disponible, sino uno por defecto
    val subtitle = if (statusMessage.isNotEmpty()) {
        statusMessage
    } else {
        when {
            isMonitoring && status == ConnectionStatus.CONNECTED -> "Recibiendo datos del dispositivo"
            isMonitoring && status == ConnectionStatus.CONNECTING -> "Buscando dispositivo..."
            isMonitoring && status == ConnectionStatus.DISCONNECTED -> "Intentando reconectar..."
            else -> "Inicia el monitoreo para conectar"
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isMonitoring && status == ConnectionStatus.CONNECTING) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    color = iconColor,
                    strokeWidth = 3.dp
                )
            } else {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (backgroundColor == MaterialTheme.colorScheme.surfaceVariant)
                        MaterialTheme.colorScheme.onSurface
                    else iconColor
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Simple data class for status card info.
 */
private data class Tuple4<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)

private data class Tuple5<A, B, C, D, E>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E
)

/**
 * Connection status enum.
 */
enum class ConnectionStatus {
    DISCONNECTED,
    CONNECTING,
    CONNECTED
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
