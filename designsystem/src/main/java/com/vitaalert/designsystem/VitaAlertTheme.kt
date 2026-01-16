package com.vitaalert.designsystem

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Compose theme for VitaAlert using the Bogotá palette.
 */
@Composable
fun VitaAlertTheme(
    darkTheme: Boolean = androidx.compose.foundation.isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        darkColorScheme(
            primary = Color(0xFFF9C642),
            error = Color(0xFFE53935),
            background = Color(0xFF0B0B0C),
            surface = Color(0xFF0B0B0C),
            onPrimary = Color(0xFF0B0B0C),
            onBackground = Color(0xFFF7F7F8),
            onSurface = Color(0xFFF7F7F8)
        )
    } else {
        lightColorScheme(
            primary = Color(0xFFF9C642),
            error = Color(0xFFE53935),
            background = Color(0xFFF7F7F8),
            surface = Color(0xFFF7F7F8),
            onPrimary = Color(0xFF0B0B0C),
            onBackground = Color(0xFF0B0B0C),
            onSurface = Color(0xFF0B0B0C)
        )
    }

    MaterialTheme(
        colorScheme = colors,
        typography = MaterialTheme.typography,
        content = content
    )
}
