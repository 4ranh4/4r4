package com.app4r4.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app4r4.data.db.ActivityRecord

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Histórico & Metas") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        value = "${String.format("%.1f", state.totalAllTime)} kg",
                        label = "CO\u2082 total registrado",
                        icon = Icons.Default.Cloud
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        value = "${state.savedTrees}",
                        label = "Árvores p/ compensar",
                        icon = Icons.Default.Park
                    )
                }
            }

            item {
                if (state.monthlyTotals.isNotEmpty()) {
                    BarChartCard(monthlyTotals = state.monthlyTotals)
                }
            }

            item {
                GoalCard(totalCarbon = state.totalAllTime)
            }

            item {
                Text(
                    "Todas as atividades",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (state.records.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Nenhuma atividade registrada ainda.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                items(state.records, key = { it.id }) { record ->
                    RecordItem(
                        record = record,
                        onDelete = { viewModel.deleteRecord(record) }
                    )
                }
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Icon(icon, contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(20.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer)
            Text(label, style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f))
        }
    }
}

@Composable
private fun BarChartCard(monthlyTotals: List<MonthlyTotal>) {
    val maxVal = monthlyTotals.maxOfOrNull { it.carbonKg } ?: 1.0

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Emissões mensais (kg CO\u2082)",
                style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth().height(120.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                monthlyTotals.forEach { month ->
                    val fraction = (month.carbonKg / maxVal).toFloat().coerceIn(0.05f, 1f)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            "${String.format("%.0f", month.carbonKg)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.6f)
                                .fillMaxHeight(fraction)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(MaterialTheme.colorScheme.primary)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(month.label, style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun GoalCard(totalCarbon: Double) {
    val monthlyGoal = 700.0 // kg CO2 meta sustentável
    val currentMonth = totalCarbon.coerceAtMost(monthlyGoal)
    val progress = (currentMonth / monthlyGoal).toFloat().coerceIn(0f, 1f)
    val achieved = totalCarbon <= monthlyGoal

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (achieved) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(
                    if (achieved) Icons.Default.EmojiEvents else Icons.Default.Flag,
                    contentDescription = null,
                    tint = if (achieved) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.error
                )
                Text(
                    if (achieved) "Meta sustentável atingida!" else "Meta sustentável: 700 kg CO\u2082/mês",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            if (!achieved) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = MaterialTheme.colorScheme.error,
                    trackColor = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.15f)
                )
                Text("${String.format("%.1f", totalCarbon - monthlyGoal)} kg acima da meta mensal",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer)
            } else {
                Text("Você está dentro da meta de emissões sustentáveis. Continue assim!",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecordItem(record: ActivityRecord, onDelete: () -> Unit) {
    val icon = when (record.type) {
        "CAR" -> Icons.Default.DirectionsCar
        "FLIGHT" -> Icons.Default.Flight
        "ELECTRICITY" -> Icons.Default.Bolt
        "FOOD_BEEF", "FOOD_CHICKEN" -> Icons.Default.Restaurant
        else -> Icons.Default.Eco
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(record.description, style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium)
                Text(formatDate(record.date), style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(
                "${String.format("%.2f", record.carbonKg)} kg",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.DeleteOutline, contentDescription = "Apagar",
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.error)
            }
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
