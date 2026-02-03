package com.vitaalert.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Paleta de colores VitaAlert - inspirada en Bogotá con toques modernos
object VitaAlertColors {
    // Primarios - Amarillo dorado de Bogotá
    val Primary = Color(0xFFF9C642)
    val PrimaryDark = Color(0xFFE5B438)
    val PrimaryLight = Color(0xFFFDD870)
    val OnPrimary = Color(0xFF1A1A1A)

    // Secundarios - Azul médico profesional
    val Secondary = Color(0xFF2196F3)
    val SecondaryDark = Color(0xFF1976D2)
    val SecondaryLight = Color(0xFF64B5F6)
    val OnSecondary = Color.White

    // Acentos - Rojo de alertas
    val Error = Color(0xFFE53935)
    val ErrorLight = Color(0xFFFF6F60)
    val OnError = Color.White

    // Estados de signos vitales
    val VitalNormal = Color(0xFF4CAF50)
    val VitalNormalLight = Color(0xFFE8F5E9)
    val VitalAlert = Color(0xFFFF9800)
    val VitalAlertLight = Color(0xFFFFF3E0)
    val VitalCritical = Color(0xFFE53935)
    val VitalCriticalLight = Color(0xFFFFEBEE)

    // Superficies - Light
    val SurfaceLight = Color(0xFFFFFFFF)
    val SurfaceVariantLight = Color(0xFFF5F5F7)
    val BackgroundLight = Color(0xFFFAFAFC)
    val OnSurfaceLight = Color(0xFF1A1A1A)
    val OnSurfaceVariantLight = Color(0xFF5C5C5C)

    // Superficies - Dark
    val SurfaceDark = Color(0xFF1E1E1E)
    val SurfaceVariantDark = Color(0xFF2D2D2D)
    val BackgroundDark = Color(0xFF121212)
    val OnSurfaceDark = Color(0xFFF5F5F5)
    val OnSurfaceVariantDark = Color(0xFFB0B0B0)

    // Bordes y divisores
    val OutlineLight = Color(0xFFE0E0E0)
    val OutlineDark = Color(0xFF3D3D3D)
}

private val VitaAlertLightColors = lightColorScheme(
    primary = VitaAlertColors.Primary,
    onPrimary = VitaAlertColors.OnPrimary,
    primaryContainer = VitaAlertColors.PrimaryLight,
    onPrimaryContainer = VitaAlertColors.OnPrimary,
    secondary = VitaAlertColors.Secondary,
    onSecondary = VitaAlertColors.OnSecondary,
    secondaryContainer = VitaAlertColors.SecondaryLight,
    onSecondaryContainer = Color.White,
    tertiary = VitaAlertColors.VitalNormal,
    onTertiary = Color.White,
    error = VitaAlertColors.Error,
    onError = VitaAlertColors.OnError,
    errorContainer = VitaAlertColors.VitalCriticalLight,
    onErrorContainer = VitaAlertColors.Error,
    background = VitaAlertColors.BackgroundLight,
    onBackground = VitaAlertColors.OnSurfaceLight,
    surface = VitaAlertColors.SurfaceLight,
    onSurface = VitaAlertColors.OnSurfaceLight,
    surfaceVariant = VitaAlertColors.SurfaceVariantLight,
    onSurfaceVariant = VitaAlertColors.OnSurfaceVariantLight,
    outline = VitaAlertColors.OutlineLight,
    outlineVariant = VitaAlertColors.OutlineLight.copy(alpha = 0.5f)
)

private val VitaAlertDarkColors = darkColorScheme(
    primary = VitaAlertColors.Primary,
    onPrimary = VitaAlertColors.OnPrimary,
    primaryContainer = VitaAlertColors.PrimaryDark,
    onPrimaryContainer = Color.White,
    secondary = VitaAlertColors.SecondaryLight,
    onSecondary = VitaAlertColors.OnPrimary,
    secondaryContainer = VitaAlertColors.SecondaryDark,
    onSecondaryContainer = Color.White,
    tertiary = VitaAlertColors.VitalNormal,
    onTertiary = Color.White,
    error = VitaAlertColors.ErrorLight,
    onError = VitaAlertColors.OnPrimary,
    errorContainer = VitaAlertColors.Error,
    onErrorContainer = Color.White,
    background = VitaAlertColors.BackgroundDark,
    onBackground = VitaAlertColors.OnSurfaceDark,
    surface = VitaAlertColors.SurfaceDark,
    onSurface = VitaAlertColors.OnSurfaceDark,
    surfaceVariant = VitaAlertColors.SurfaceVariantDark,
    onSurfaceVariant = VitaAlertColors.OnSurfaceVariantDark,
    outline = VitaAlertColors.OutlineDark,
    outlineVariant = VitaAlertColors.OutlineDark.copy(alpha = 0.5f)
)

private val VitaAlertTypography = Typography(
    displayLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 45.sp,
        lineHeight = 52.sp
    ),
    displaySmall = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 44.sp
    ),
    headlineLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 40.sp
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp
    ),
    headlineSmall = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
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
        typography = VitaAlertTypography,
        content = content
    )
}
