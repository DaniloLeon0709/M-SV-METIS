package com.vitaalert.designsystem

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Displays the primary action button used across VitaAlert with gradient and elevation.
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(
                elevation = if (enabled) 8.dp else 0.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = VitaAlertColors.Primary.copy(alpha = 0.3f),
                spotColor = VitaAlertColors.Primary.copy(alpha = 0.3f)
            ),
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = VitaAlertColors.Primary,
            contentColor = VitaAlertColors.OnPrimary,
            disabledContainerColor = VitaAlertColors.Primary.copy(alpha = 0.5f),
            disabledContentColor = VitaAlertColors.OnPrimary.copy(alpha = 0.7f)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp,
            disabledElevation = 0.dp
        )
    ) {
        Text(
            text = if (isLoading) "Cargando..." else text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/**
 * Secondary outlined button for less prominent actions.
 */
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = enabled,
        shape = RoundedCornerShape(16.dp),
        border = ButtonDefaults.outlinedButtonBorder(enabled = enabled)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Represents the current state of a vital reading.
 */
enum class VitalState {
    NORMAL,
    ALERT,
    CRITICAL
}

/**
 * Returns colors associated with each vital state.
 */
@Composable
fun VitalState.colors(): Triple<Color, Color, Color> = when (this) {
    VitalState.NORMAL -> Triple(
        VitaAlertColors.VitalNormal,
        VitaAlertColors.VitalNormalLight,
        VitaAlertColors.VitalNormal
    )
    VitalState.ALERT -> Triple(
        VitaAlertColors.VitalAlert,
        VitaAlertColors.VitalAlertLight,
        VitaAlertColors.VitalAlert
    )
    VitalState.CRITICAL -> Triple(
        VitaAlertColors.VitalCritical,
        VitaAlertColors.VitalCriticalLight,
        VitaAlertColors.VitalCritical
    )
}

/**
 * Shows a state badge with semantic coloring for vital status.
 */
@Composable
fun StateBadge(
    state: VitalState,
    modifier: Modifier = Modifier
) {
    val (mainColor, lightColor, _) = state.colors()
    val label = when (state) {
        VitalState.NORMAL -> "Normal"
        VitalState.ALERT -> "Alerta"
        VitalState.CRITICAL -> "Crítico"
    }
    val icon = when (state) {
        VitalState.NORMAL -> Icons.Outlined.CheckCircle
        VitalState.ALERT -> Icons.Default.Warning
        VitalState.CRITICAL -> Icons.Default.Favorite
    }

    Surface(
        color = lightColor,
        contentColor = mainColor,
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = mainColor
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/**
 * Modern vital card with gradient accent, icon, and animated state.
 */
@Composable
fun VitalCard(
    value: String,
    label: String,
    state: VitalState,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.Favorite,
    unit: String = ""
) {
    val (mainColor, lightColor, _) = state.colors()
    val animatedColor by animateColorAsState(
        targetValue = mainColor,
        animationSpec = tween(durationMillis = 300),
        label = "vitalColor"
    )
    val animatedScale by animateFloatAsState(
        targetValue = if (state == VitalState.CRITICAL) 1.02f else 1f,
        animationSpec = tween(durationMillis = 300),
        label = "vitalScale"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = animatedColor.copy(alpha = 0.15f),
                spotColor = animatedColor.copy(alpha = 0.15f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Box {
            // Accent gradient bar on the left
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(100.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                animatedColor,
                                animatedColor.copy(alpha = 0.6f)
                            )
                        ),
                        shape = RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp)
                    )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 16.dp, top = 16.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Icon container with gradient background
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        lightColor,
                                        lightColor.copy(alpha = 0.5f)
                                    )
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            modifier = Modifier.size(28.dp),
                            tint = animatedColor
                        )
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = value,
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (unit.isNotEmpty()) {
                                Text(
                                    text = unit,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                StateBadge(state = state)
            }
        }
    }
}

/**
 * Section header with optional action.
 */
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    action: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            subtitle?.let {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        action?.invoke()
    }
}

/**
 * Device item card for BLE device list.
 */
@Composable
fun DeviceCard(
    name: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    isConnected: Boolean = false,
    onClick: () -> Unit = {}
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isConnected)
                VitaAlertColors.VitalNormalLight
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Status indicator
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(
                            if (isConnected) VitaAlertColors.VitalNormal
                            else MaterialTheme.colorScheme.outline
                        )
                )

                Column {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    subtitle?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (isConnected) {
                StateBadge(state = VitalState.NORMAL)
            }
        }
    }
}

/**
 * Empty state placeholder.
 */
@Composable
fun EmptyState(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: ImageVector? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        icon?.let {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        subtitle?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Chart container with title and styled background.
 */
@Composable
fun ChartContainer(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            SectionHeader(title = title, subtitle = subtitle)
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

// ============ PREVIEWS ============

@Preview(showBackground = true)
@Composable
private fun PrimaryButtonPreview() {
    VitaAlertTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            PrimaryButton(text = "Iniciar monitoreo", onClick = {})
            Spacer(modifier = Modifier.height(12.dp))
            SecondaryButton(text = "Cancelar", onClick = {})
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun VitalCardPreview() {
    VitaAlertTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            VitalCard(
                value = "72",
                label = "Frecuencia Cardíaca",
                unit = "bpm",
                state = VitalState.NORMAL
            )
            VitalCard(
                value = "94",
                label = "Saturación de Oxígeno",
                unit = "%",
                state = VitalState.ALERT
            )
            VitalCard(
                value = "150/95",
                label = "Presión Arterial",
                unit = "mmHg",
                state = VitalState.CRITICAL
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DeviceCardPreview() {
    VitaAlertTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DeviceCard(
                name = "VitaAlert Band Pro",
                subtitle = "Último uso: hace 2 horas",
                isConnected = true
            )
            DeviceCard(
                name = "Mi Band 7",
                subtitle = "No conectado"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StateBadgePreview() {
    VitaAlertTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StateBadge(state = VitalState.NORMAL)
            StateBadge(state = VitalState.ALERT)
            StateBadge(state = VitalState.CRITICAL)
        }
    }
}
