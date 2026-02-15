package com.vitaalert.mobile.dev.ui

import android.graphics.Color as AndroidColor
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.vitaalert.designsystem.SectionHeader
import com.vitaalert.designsystem.VitaAlertColors
import com.vitaalert.domain.model.VitalReading
import com.vitaalert.domain.model.VitalType
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import androidx.core.graphics.toColorInt

/**
 * History screen showing vital readings history with statistics.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onBack: () -> Unit,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Historial",
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
            // Vital type selector
            item {
                SectionHeader(
                    title = "Tipo de signo vital",
                    subtitle = "Selecciona qué quieres ver"
                )
                Spacer(modifier = Modifier.height(8.dp))
                VitalTypeSelector(
                    selectedType = uiState.selectedType,
                    onTypeSelected = { viewModel.selectType(it) }
                )
            }

            // Period selector
            item {
                Spacer(modifier = Modifier.height(8.dp))
                PeriodSelector(
                    selectedPeriod = uiState.selectedPeriod,
                    onPeriodSelected = { viewModel.selectPeriod(it) }
                )
            }

            // Stats cards
            item {
                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = VitaAlertColors.Primary)
                    }
                } else {
                    uiState.stats?.let { stats ->
                        StatsCards(stats = stats, type = uiState.selectedType)
                    }
                }
            }

            // Chart
            item {
                if (!uiState.isLoading && uiState.readings.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    SectionHeader(
                        title = "Tendencia",
                        subtitle = "Visualización de lecturas"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    HistoryChart(
                        readings = uiState.readings,
                        type = uiState.selectedType
                    )
                }
            }

            // Readings list
            item {
                Spacer(modifier = Modifier.height(8.dp))
                SectionHeader(
                    title = "Lecturas recientes",
                    subtitle = "${uiState.readings.size} registros"
                )
            }

            if (uiState.readings.isEmpty() && !uiState.isLoading) {
                item {
                    EmptyHistoryState()
                }
            } else {
                items(uiState.readings.take(20)) { reading ->
                    ReadingItem(reading = reading)
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun VitalTypeSelector(
    selectedType: VitalType,
    onTypeSelected: (VitalType) -> Unit
) {
    val types = listOf(
        Triple(VitalType.HR, "Corazón", Icons.Filled.Favorite),
        Triple(VitalType.SPO2, "SpO2", Icons.Outlined.Air),
        Triple(VitalType.TEMP, "Temp", Icons.Outlined.Thermostat)
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(types) { (type, label, icon) ->
            val isSelected = type == selectedType
            val backgroundColor by animateColorAsState(
                targetValue = if (isSelected) VitaAlertColors.Primary else MaterialTheme.colorScheme.surfaceVariant,
                label = "bg"
            )
            val contentColor by animateColorAsState(
                targetValue = if (isSelected) VitaAlertColors.OnPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                label = "content"
            )

            Card(
                modifier = Modifier
                    .clickable { onTypeSelected(type) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = backgroundColor)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium,
                        color = contentColor
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PeriodSelector(
    selectedPeriod: HistoryPeriod,
    onPeriodSelected: (HistoryPeriod) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        HistoryPeriod.entries.forEach { period ->
            FilterChip(
                selected = period == selectedPeriod,
                onClick = { onPeriodSelected(period) },
                label = { Text(period.label) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = VitaAlertColors.Primary.copy(alpha = 0.2f),
                    selectedLabelColor = VitaAlertColors.Primary
                )
            )
        }
    }
}

@Composable
private fun StatsCards(stats: VitalStats, type: VitalType) {
    val unit = when (type) {
        VitalType.HR -> "bpm"
        VitalType.SPO2 -> "%"
        VitalType.TEMP -> "°C"
        VitalType.BP_SYS, VitalType.BP_DIA -> "mmHg"
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            label = "Promedio",
            value = String.format("%.1f", stats.average),
            unit = unit,
            icon = Icons.Outlined.Analytics,
            color = VitaAlertColors.Primary
        )
        StatCard(
            modifier = Modifier.weight(1f),
            label = "Mínimo",
            value = String.format("%.0f", stats.min),
            unit = unit,
            icon = Icons.AutoMirrored.Filled.TrendingDown,
            color = VitaAlertColors.VitalNormal
        )
        StatCard(
            modifier = Modifier.weight(1f),
            label = "Máximo",
            value = String.format("%.0f", stats.max),
            unit = unit,
            icon = Icons.AutoMirrored.Filled.TrendingUp,
            color = VitaAlertColors.VitalCritical
        )
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    unit: String,
    icon: ImageVector,
    color: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = unit,
                style = MaterialTheme.typography.labelSmall,
                color = color.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun HistoryChart(readings: List<VitalReading>, type: VitalType) {
    val chartColor = when (type) {
        VitalType.HR -> "#F9C642"
        VitalType.SPO2 -> "#4CAF50"
        VitalType.TEMP -> "#FF5722"
        VitalType.BP_SYS, VitalType.BP_DIA -> "#2196F3"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp),
            factory = { ctx ->
                LineChart(ctx).apply {
                    description.isEnabled = false
                    legend.isEnabled = false
                    setTouchEnabled(true)
                    setDrawGridBackground(false)
                    setBackgroundColor(AndroidColor.TRANSPARENT)

                    xAxis.apply {
                        position = XAxis.XAxisPosition.BOTTOM
                        setDrawGridLines(false)
                        textColor = "#888888".toColorInt()
                    }

                    axisLeft.apply {
                        setDrawGridLines(true)
                        gridColor = "#E0E0E0".toColorInt()
                        textColor = "#888888".toColorInt()
                    }

                    axisRight.isEnabled = false
                }
            },
            update = { chart ->
                val entries = readings.mapIndexed { index, reading ->
                    Entry(index.toFloat(), reading.value.toFloat())
                }.reversed()

                val dataSet = LineDataSet(entries, "").apply {
                    color = chartColor.toColorInt()
                    setDrawCircles(true)
                    circleRadius = 3f
                    setCircleColor(chartColor.toColorInt())
                    lineWidth = 2.5f
                    mode = LineDataSet.Mode.CUBIC_BEZIER
                    setDrawFilled(true)
                    fillColor = chartColor.toColorInt()
                    fillAlpha = 50
                    setDrawValues(false)
                }

                chart.data = LineData(dataSet)
                chart.animateX(500)
                chart.invalidate()
            }
        )
    }
}

@Composable
private fun ReadingItem(reading: VitalReading) {
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val dateFormatter = DateTimeFormatter.ofPattern("dd MMM")
    val zoneId = ZoneId.systemDefault()

    val localDateTime = reading.timestamp.atZone(zoneId).toLocalDateTime()
    val time = localDateTime.format(timeFormatter)
    val date = localDateTime.format(dateFormatter)

    val (icon, unit, color) = when (reading.type) {
        VitalType.HR -> Triple(Icons.Filled.Favorite, "bpm", VitaAlertColors.Primary)
        VitalType.SPO2 -> Triple(Icons.Outlined.Air, "%", VitaAlertColors.VitalNormal)
        VitalType.TEMP -> Triple(Icons.Outlined.Thermostat, "°C", VitaAlertColors.VitalAlert)
        VitalType.BP_SYS, VitalType.BP_DIA -> Triple(Icons.Outlined.Analytics, "mmHg", VitaAlertColors.Secondary)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${reading.value.toInt()} $unit",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = reading.type.name.replace("_", " "),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = time,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = date,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun EmptyHistoryState() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
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
                imageVector = Icons.Outlined.Analytics,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Sin datos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "No hay lecturas para este período",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

