package com.vitaalert.mobile.dev.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.vitaalert.designsystem.PrimaryButton
import com.vitaalert.designsystem.SecondaryButton
import com.vitaalert.designsystem.VitaAlertColors
import kotlinx.coroutines.launch

/**
 * Profile screen with photo upload and password change functionality.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showChangePasswordSheet by remember { mutableStateOf(false) }
    var showEditNameSheet by remember { mutableStateOf(false) }
    var showEditPhoneSheet by remember { mutableStateOf(false) }

    // Photo picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.updatePhoto(it) }
    }

    // Handle success/error messages
    LaunchedEffect(uiState.successMessage, uiState.errorMessage) {
        uiState.successMessage?.let { message ->
            scope.launch {
                snackbarHostState.showSnackbar(message)
                viewModel.clearMessages()
            }
        }
        uiState.errorMessage?.let { message ->
            scope.launch {
                snackbarHostState.showSnackbar(message)
                viewModel.clearMessages()
            }
        }
    }

    // Handle password change success
    LaunchedEffect(uiState.passwordChangeSuccess) {
        if (uiState.passwordChangeSuccess) {
            showChangePasswordSheet = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mi Perfil",
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
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = VitaAlertColors.Primary)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                // Profile header with photo
                ProfileHeader(
                    photoUri = uiState.profile?.photoUri,
                    displayName = uiState.profile?.displayName ?: "Usuario",
                    email = uiState.profile?.email ?: "",
                    onEditPhoto = { photoPickerLauncher.launch("image/*") },
                    isSaving = uiState.isSaving
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Profile options
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Edit name option
                    ProfileOptionCard(
                        icon = Icons.Outlined.Person,
                        title = "Nombre de usuario",
                        subtitle = uiState.profile?.displayName ?: "No configurado",
                        onClick = { showEditNameSheet = true }
                    )

                    // Email (read-only)
                    ProfileOptionCard(
                        icon = Icons.Outlined.Email,
                        title = "Correo electrónico",
                        subtitle = uiState.profile?.email ?: "",
                        onClick = { },
                        showArrow = false
                    )

                    // Phone number
                    ProfileOptionCard(
                        icon = Icons.Outlined.Phone,
                        title = "Número de teléfono",
                        subtitle = uiState.profile?.phoneNumber ?: "No configurado",
                        onClick = { showEditPhoneSheet = true }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Change password
                    ProfileOptionCard(
                        icon = Icons.Outlined.Key,
                        title = "Cambiar contraseña",
                        subtitle = "Actualiza tu contraseña de acceso",
                        onClick = { showChangePasswordSheet = true }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Logout button
                    SecondaryButton(
                        text = "Cerrar sesión",
                        onClick = {
                            viewModel.logout()
                            onLogout()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }

    // Change password bottom sheet
    if (showChangePasswordSheet) {
        ChangePasswordBottomSheet(
            isLoading = uiState.isChangingPassword,
            errorMessage = uiState.errorMessage,
            onDismiss = {
                showChangePasswordSheet = false
                viewModel.clearMessages()
            },
            onChangePassword = { current, new, confirm ->
                viewModel.changePassword(current, new, confirm)
            }
        )
    }

    // Edit name bottom sheet
    if (showEditNameSheet) {
        EditNameBottomSheet(
            currentName = uiState.profile?.displayName ?: "",
            isSaving = uiState.isSaving,
            onDismiss = { showEditNameSheet = false },
            onSave = { newName ->
                viewModel.updateDisplayName(newName)
                showEditNameSheet = false
            }
        )
    }

    // Edit phone bottom sheet
    if (showEditPhoneSheet) {
        EditPhoneBottomSheet(
            currentPhone = uiState.profile?.phoneNumber ?: "",
            isSaving = uiState.isSaving,
            onDismiss = { showEditPhoneSheet = false },
            onSave = { newPhone ->
                viewModel.updatePhoneNumber(newPhone)
                showEditPhoneSheet = false
            }
        )
    }
}

@Composable
private fun ProfileHeader(
    photoUri: String?,
    displayName: String,
    email: String,
    onEditPhoto: () -> Unit,
    isSaving: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        VitaAlertColors.Primary.copy(alpha = 0.1f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .padding(vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile photo with edit button
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(VitaAlertColors.Primary.copy(alpha = 0.2f))
                        .border(
                            width = 3.dp,
                            color = VitaAlertColors.Primary,
                            shape = CircleShape
                        )
                        .clickable(enabled = !isSaving) { onEditPhoto() },
                    contentAlignment = Alignment.Center
                ) {
                    if (photoUri != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(photoUri)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Foto de perfil",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(60.dp),
                            tint = VitaAlertColors.Primary
                        )
                    }

                    if (isSaving) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(32.dp),
                                color = VitaAlertColors.Primary
                            )
                        }
                    }
                }

                // Camera button
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(VitaAlertColors.Primary, CircleShape)
                        .clickable(enabled = !isSaving) { onEditPhoto() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Cambiar foto",
                        modifier = Modifier.size(20.dp),
                        tint = VitaAlertColors.OnPrimary
                    )
                }
            }

            // Name and email
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = displayName,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ProfileOptionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    showArrow: Boolean = true
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
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

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (showArrow) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChangePasswordBottomSheet(
    isLoading: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onChangePassword: (current: String, new: String, confirm: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showCurrentPassword by remember { mutableStateOf(false) }
    var showNewPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Cambiar contraseña",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            // Current password
            PasswordTextField(
                value = currentPassword,
                onValueChange = { currentPassword = it },
                label = "Contraseña actual",
                showPassword = showCurrentPassword,
                onToggleVisibility = { showCurrentPassword = !showCurrentPassword }
            )

            // New password
            PasswordTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = "Nueva contraseña",
                showPassword = showNewPassword,
                onToggleVisibility = { showNewPassword = !showNewPassword }
            )

            // Confirm password
            PasswordTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "Confirmar nueva contraseña",
                showPassword = showConfirmPassword,
                onToggleVisibility = { showConfirmPassword = !showConfirmPassword }
            )

            // Error message
            AnimatedVisibility(
                visible = errorMessage != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                errorMessage?.let {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = VitaAlertColors.VitalCriticalLight
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = it,
                            modifier = Modifier.padding(16.dp),
                            color = VitaAlertColors.VitalCritical,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }


            Spacer(modifier = Modifier.height(8.dp))

            PrimaryButton(
                text = "Cambiar contraseña",
                onClick = { onChangePassword(currentPassword, newPassword, confirmPassword) },
                isLoading = isLoading
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditNameBottomSheet(
    currentName: String,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var name by remember { mutableStateOf(currentName) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Editar nombre",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Nombre de usuario") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VitaAlertColors.Primary,
                    focusedLabelColor = VitaAlertColors.Primary
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            PrimaryButton(
                text = "Guardar",
                onClick = { onSave(name) },
                isLoading = isSaving
            )
        }
    }
}

@Composable
private fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    showPassword: Boolean,
    onToggleVisibility: () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingIcon = {
            IconButton(onClick = onToggleVisibility) {
                Icon(
                    imageVector = if (showPassword)
                        Icons.Default.Visibility
                    else
                        Icons.Default.VisibilityOff,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        visualTransformation = if (showPassword)
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
}

/**
 * Data class representing a country with its phone code.
 */
private data class CountryCode(
    val name: String,
    val code: String,
    val flag: String
)

/**
 * List of common Latin American and North American country codes.
 */
private val countryCodes = listOf(
    CountryCode("Colombia", "+57", "🇨🇴"),
    CountryCode("México", "+52", "🇲🇽"),
    CountryCode("Argentina", "+54", "🇦🇷"),
    CountryCode("Chile", "+56", "🇨🇱"),
    CountryCode("Perú", "+51", "🇵🇪"),
    CountryCode("Ecuador", "+593", "🇪🇨"),
    CountryCode("Venezuela", "+58", "🇻🇪"),
    CountryCode("Brasil", "+55", "🇧🇷"),
    CountryCode("Estados Unidos", "+1", "🇺🇸"),
    CountryCode("España", "+34", "🇪🇸"),
    CountryCode("Panamá", "+507", "🇵🇦"),
    CountryCode("Costa Rica", "+506", "🇨🇷"),
    CountryCode("Guatemala", "+502", "🇬🇹"),
    CountryCode("Honduras", "+504", "🇭🇳"),
    CountryCode("El Salvador", "+503", "🇸🇻"),
    CountryCode("Nicaragua", "+505", "🇳🇮"),
    CountryCode("Bolivia", "+591", "🇧🇴"),
    CountryCode("Paraguay", "+595", "🇵🇾"),
    CountryCode("Uruguay", "+598", "🇺🇾"),
    CountryCode("República Dominicana", "+1", "🇩🇴"),
    CountryCode("Puerto Rico", "+1", "🇵🇷"),
    CountryCode("Cuba", "+53", "🇨🇺")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditPhoneBottomSheet(
    currentPhone: String,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Parse current phone if exists
    val initialCountryCode = countryCodes.find { currentPhone.startsWith(it.code) }
        ?: countryCodes.first()
    val initialNumber = if (currentPhone.startsWith(initialCountryCode.code)) {
        currentPhone.removePrefix(initialCountryCode.code).trim()
    } else {
        currentPhone.replace(Regex("^\\+\\d+\\s*"), "")
    }

    var selectedCountry by remember { mutableStateOf(initialCountryCode) }
    var phoneNumber by remember { mutableStateOf(initialNumber) }
    var showCountryPicker by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Número de teléfono",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Text(
                text = "Ingresa tu número con indicativo de país",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            // Country code selector + Phone number
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Country code button
                Card(
                    modifier = Modifier
                        .clickable { showCountryPicker = !showCountryPicker },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = selectedCountry.flag,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = selectedCountry.code,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Phone number input
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it.filter { char -> char.isDigit() } },
                    modifier = Modifier.weight(1f),
                    label = { Text("Número") },
                    placeholder = { Text("300 123 4567") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VitaAlertColors.Primary,
                        focusedLabelColor = VitaAlertColors.Primary
                    ),
                    singleLine = true
                )
            }

            // Country picker dropdown
            AnimatedVisibility(visible = showCountryPicker) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(8.dp)
                    ) {
                        countryCodes.forEach { country ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedCountry = country
                                        showCountryPicker = false
                                    }
                                    .padding(horizontal = 12.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = country.flag,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = country.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = country.code,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (country == selectedCountry) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = VitaAlertColors.Primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            PrimaryButton(
                text = "Guardar",
                onClick = {
                    val fullNumber = "${selectedCountry.code} $phoneNumber".trim()
                    onSave(fullNumber)
                },
                isLoading = isSaving
            )
        }
    }
}

