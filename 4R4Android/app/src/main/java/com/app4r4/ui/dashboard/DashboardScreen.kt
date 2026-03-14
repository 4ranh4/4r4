package com.app4r4.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app4r4.data.db.ActivityRecord
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onNavigateToCalculator: () -> Unit,
    onNavigateToTips: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Olá, ${state.userName.ifBlank { "Usuário" }} 👋",
                            style = MaterialTheme.typography.titleMedium)
                        Text("Seu impacto ambiental", style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToTips) {
                        Icon(Icons.Default.Lightbulb, contentDescription = "Dicas",
                            tint = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToCalculator,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Registrar atividade") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CarbonSummaryCard(
                totalCarbon = state.totalCarbonThisMonth,
                avgBrazil = state.monthlyAvgBrazil,
                level = state.carbonLevel
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickActionCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.DirectionsCar,
                    label = "Transporte",
                    onClick = onNavigateToCalculator
                )
                QuickActionCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Flight,
                    label = "Viagem",
                    onClick = onNavigateToCalculator
                )
                QuickActionCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Bolt,
                    label = "Energia",
                    onClick = onNavigateToCalculator
                )
            }

            if (state.recentActivities.isNotEmpty()) {
                RecentActivitiesSection(
                    activities = state.recentActivities,
                    onSeeAll = onNavigateToHistory
                )
            } else {
                EmptyStateCard(onNavigateToCalculator)
            }

            Spacer(Modifier.height(80.dp))
        }
    }
}

@Composable
private fun CarbonSummaryCard(
    totalCarbon: Double,
    avgBrazil: Double,
    level: CarbonLevel
) {
    val progress = min(1f, (totalCarbon / avgBrazil).toFloat())
    val levelColor = when (level) {
        CarbonLevel.GREAT -> Color(0xFF1B6B2F)
        CarbonLevel.GOOD -> Color(0xFF639922)
        CarbonLevel.MODERATE -> Color(0xFFEF9F27)
        CarbonLevel.HIGH -> Color(0xFFD85A30)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text("Este mês", style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                    Text(
                        text = "${String.format("%.1f", totalCarbon)} kg",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text("de CO\u2082 emitido", style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                }
                Surface(
                    color = levelColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = level.label,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = levelColor,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Text("vs. média brasileira (${String.format("%.0f", avgBrazil)} kg/mês)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f))

            Spacer(Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = levelColor,
                trackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.15f)
            )

            Spacer(Modifier.height(8.dp))
            Text(level.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
        }
    }
}

@Composable
private fun QuickActionCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(icon, contentDescription = label,
                tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
            Text(label, style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
private fun RecentActivitiesSection(
    activities: List<ActivityRecord>,
    onSeeAll: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Atividades recentes", style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold)
            TextButton(onClick = onSeeAll) { Text("Ver todas") }
        }

        activities.forEach { record ->
            ActivityItem(record)
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
        }
    }
}

@Composable
private fun ActivityItem(record: ActivityRecord) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val icon = when (record.type) {
                "CAR" -> Icons.Default.DirectionsCar
                "FLIGHT" -> Icons.Default.Flight
                "ELECTRICITY" -> Icons.Default.Bolt
                "FOOD_BEEF" -> Icons.Default.SetMeal
                else -> Icons.Default.Eco
            }
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary)
            }
            Column {
                Text(record.description, style = MaterialTheme.typography.bodyMedium)
                Text(formatDate(record.date), style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Text(
            text = "${String.format("%.2f", record.carbonKg)} kg",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun EmptyStateCard(onNavigate: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(24.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(Icons.Default.AddCircle, contentDescription = null,
                tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(40.dp))
            Text("Nenhuma atividade ainda", style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium)
            Text("Registre suas atividades para calcular\nsua pegada de carbono.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            OutlinedButton(onClick = onNavigate) { Text("Registrar primeira atividade") }
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val cal = java.util.Calendar.getInstance()
    cal.timeInMillis = timestamp
    return "${cal.get(java.util.Calendar.DAY_OF_MONTH)}/" +
            "${cal.get(java.util.Calendar.MONTH) + 1}/" +
            "${cal.get(java.util.Calendar.YEAR)}"
}
