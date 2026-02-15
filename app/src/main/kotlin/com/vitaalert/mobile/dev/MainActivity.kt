package com.vitaalert.mobile.dev

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vitaalert.auth.google.GoogleSignInHelper
import com.vitaalert.auth.session.SessionManager
import com.vitaalert.data.repository.FirebaseProfileRepository
import com.vitaalert.designsystem.VitaAlertTheme
import com.vitaalert.domain.model.UserProfile
import com.vitaalert.mobile.dev.ui.DevicePairingScreen
import com.vitaalert.mobile.dev.ui.HistoryScreen
import com.vitaalert.mobile.dev.ui.HomeDashboardScreen
import com.vitaalert.mobile.dev.ui.LoginScreen
import com.vitaalert.mobile.dev.ui.ProfileScreen
import com.vitaalert.mobile.dev.ui.RegisterScreen
import com.vitaalert.mobile.dev.ui.SplashScreen
import com.vitaalert.mobile.dev.ui.SplashViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Main activity hosting the Compose navigation graph.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var firebaseProfileRepository: FirebaseProfileRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VitaAlertTheme {
                Surface {
                    VitaAlertNavGraph(
                        sessionManager = sessionManager,
                        firebaseProfileRepository = firebaseProfileRepository
                    )
                }
            }
        }
    }
}

@Composable
private fun VitaAlertNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    sessionManager: SessionManager,
    firebaseProfileRepository: FirebaseProfileRepository
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val googleSignInHelper = remember { GoogleSignInHelper(context) }
    var isGoogleSignInLoading by remember { mutableStateOf(false) }

    // Google Sign-In launcher
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        android.util.Log.d("MainActivity", "Google Sign-In result code: ${result.resultCode}")
        val account = googleSignInHelper.handleSignInResult(result.data)
        if (account != null) {
            isGoogleSignInLoading = true
            scope.launch {
                val signInResult = googleSignInHelper.firebaseAuthWithGoogle(account)
                val userId = signInResult.userId

                if (signInResult.success && userId != null) {
                    // Crear o actualizar el perfil del usuario con la info de Google
                    try {
                        val existingProfile = try {
                            firebaseProfileRepository.getProfile()
                        } catch (e: Exception) {
                            null
                        }

                        if (existingProfile == null) {
                            // Crear nuevo perfil con datos de Google
                            val newProfile = UserProfile(
                                id = userId,
                                email = signInResult.email ?: "",
                                displayName = signInResult.displayName ?: "Usuario",
                                photoUri = signInResult.photoUrl,
                                phoneNumber = null,
                                createdAt = System.currentTimeMillis(),
                                updatedAt = System.currentTimeMillis()
                            )
                            firebaseProfileRepository.saveProfile(newProfile)
                            android.util.Log.d("MainActivity", "Created new profile for Google user: ${newProfile.email}")
                        } else {
                            android.util.Log.d("MainActivity", "Profile already exists for: ${existingProfile.email}")
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("MainActivity", "Error saving profile: ${e.message}")
                    }

                    isGoogleSignInLoading = false
                    navController.navigate("home") {
                        popUpTo(0) { inclusive = true }
                    }
                } else {
                    isGoogleSignInLoading = false
                    Toast.makeText(context, "Error al autenticar con Firebase", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            // No mostrar mensaje si el usuario canceló intencionalmente
            android.util.Log.w("MainActivity", "Google Sign-In returned null account")
        }
    }

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            val viewModel: SplashViewModel = hiltViewModel()
            SplashScreen(
                viewModel = viewModel,
                onNavigate = { destination -> navController.navigate(destination) {
                    popUpTo("splash") { inclusive = true }
                } }
            )
        }
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onRegisterClick = { navController.navigate("register") },
                onGoogleSignIn = {
                    googleSignInLauncher.launch(googleSignInHelper.getSignInIntent())
                }
            )
        }
        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    // Redirigir al Login después de registrarse exitosamente
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onBackToLogin = { navController.popBackStack() },
                onGoogleSignIn = {
                    googleSignInLauncher.launch(googleSignInHelper.getSignInIntent())
                }
            )
        }
        composable("home") {
            HomeDashboardScreen(
                onDevicePairing = { navController.navigate("pairing") },
                onProfileClick = { navController.navigate("profile") },
                onHistoryClick = { navController.navigate("history") },
                onEmergencyClick = { /* TODO: Implement emergency call */ }
            )
        }
        composable("history") {
            HistoryScreen(onBack = { navController.popBackStack() })
        }
        composable("pairing") {
            DevicePairingScreen(onBack = { navController.popBackStack() })
        }
        composable("profile") {
            ProfileScreen(
                onBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
