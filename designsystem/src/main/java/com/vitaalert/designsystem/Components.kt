package com.vitaalert.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Displays the primary action button used across VitaAlert.
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        Text(text = text)
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
 * Shows a state badge with semantic coloring for vital status.
 */
@Composable
fun StateBadge(
    state: VitalState,
    modifier: Modifier = Modifier
) {
    val (label, containerColor, contentColor) = when (state) {
        VitalState.NORMAL -> Triple("NORMAL", Color(0xFF1B5E20), Color.White)
        VitalState.ALERT -> Triple("ALERT", Color(0xFFF57C00), Color.White)
        VitalState.CRITICAL -> Triple("CRITICAL", MaterialTheme.colorScheme.error, Color.White)
    }

    Surface(
        color = containerColor,
        contentColor = contentColor,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

/**
 * Card that highlights a vital measurement with a label and status badge.
 */
@Composable
fun VitalCard(
    value: String,
    label: String,
    state: VitalState,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.displaySmall.copy(fontSize = 28.sp)
                )
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            StateBadge(state = state)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PrimaryButtonPreview() {
    VitaAlertTheme {
        PrimaryButton(text = "Iniciar") {}
    }
}

@Preview(showBackground = true)
@Composable
private fun VitalCardPreview() {
    VitaAlertTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            VitalCard(value = "72", label = "Heart Rate", state = VitalState.NORMAL)
            Spacer(modifier = Modifier.height(12.dp))
            VitalCard(value = "89%", label = "SpO2", state = VitalState.ALERT)
            Spacer(modifier = Modifier.height(12.dp))
            VitalCard(value = "150/95", label = "Blood Pressure", state = VitalState.CRITICAL)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StateBadgePreview() {
    VitaAlertTheme {
        Row(modifier = Modifier.padding(16.dp)) {
            StateBadge(state = VitalState.NORMAL)
            Spacer(modifier = Modifier.width(8.dp))
            StateBadge(state = VitalState.ALERT)
            Spacer(modifier = Modifier.width(8.dp))
            StateBadge(state = VitalState.CRITICAL)
        }
    }
}
