package com.vitaalert.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val VitaAlertLightColors = lightColorScheme(
    primary = Color(0xFFF9C642),
    error = Color(0xFFE53935),
    background = Color(0xFFF7F7F8),
    surface = Color(0xFFF7F7F8),
    onPrimary = Color(0xFF0B0B0C),
    onBackground = Color(0xFF0B0B0C),
    onSurface = Color(0xFF0B0B0C)
)

private val VitaAlertDarkColors = darkColorScheme(
    primary = Color(0xFFF9C642),
    error = Color(0xFFE53935),
    background = Color(0xFF0B0B0C),
    surface = Color(0xFF0B0B0C),
    onPrimary = Color(0xFF0B0B0C),
    onBackground = Color(0xFFF7F7F8),
    onSurface = Color(0xFFF7F7F8)
)

/**
 * VitaAlert Material 3 theme using the Bogotá palette with dark mode support.
 */
@Composable
fun VitaAlertTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) VitaAlertDarkColors else VitaAlertLightColors

    MaterialTheme(
        colorScheme = colors,
        typography = Typography(),
        content = content
    )
}
