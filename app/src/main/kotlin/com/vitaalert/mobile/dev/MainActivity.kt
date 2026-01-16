package com.vitaalert.mobile.dev

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vitaalert.designsystem.VitaAlertTheme
import com.vitaalert.mobile.dev.ui.DevicePairingScreen
import com.vitaalert.mobile.dev.ui.HomeDashboardScreen
import com.vitaalert.mobile.dev.ui.LoginScreen
import com.vitaalert.mobile.dev.ui.SplashScreen
import com.vitaalert.mobile.dev.ui.SplashViewModel

/**
 * Main activity hosting the Compose navigation graph.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VitaAlertTheme {
                Surface {
                    VitaAlertNavGraph()
                }
            }
        }
    }
}

@Composable
private fun VitaAlertNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
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
            LoginScreen(onLoginSuccess = { navController.navigate("home") {
                popUpTo("login") { inclusive = true }
            } })
        }
        composable("home") {
            HomeDashboardScreen(onDevicePairing = { navController.navigate("pairing") })
        }
        composable("pairing") {
            DevicePairingScreen(onBack = { navController.popBackStack() })
        }
    }
}
