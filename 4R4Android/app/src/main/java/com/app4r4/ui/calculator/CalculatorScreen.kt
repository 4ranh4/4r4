package com.app4r4.ui.calculator

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app4r4.data.model.ActivityType

enum class CalculatorTab(val label: String) {
    CAR("Carro"),
    FLIGHT("Avião"),
    ELECTRICITY("Energia"),
    FOOD("Alimentação")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onNavigateToDashboard: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(CalculatorTab.CAR) }

    LaunchedEffect(state.saved) {
        if (state.saved) {
            onNavigateToDashboard()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calcular emissões") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            ScrollableTabRow(
                selectedTabIndex = selectedTab.ordinal,
                edgePadding = 16.dp,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                CalculatorTab.values().forEach { tab ->
                    Tab(
                        selected = selectedTab == tab,
                        onClick = {
                            selectedTab = tab
                            viewModel.reset()
                        },
                        text = { Text(tab.label) }
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when (selectedTab) {
                    CalculatorTab.CAR -> CarForm(
                        onCalculate = { viewModel.calculateVehicle(it) }
                    )
                    CalculatorTab.FLIGHT -> FlightForm(
                        onCalculate = { origin, dest, pax -> viewModel.calculateFlight(origin, dest, pax) }
                    )
                    CalculatorTab.ELECTRICITY -> ElectricityForm(
                        onCalculate = { viewModel.calculateElectricity(it) }
                    )
                    CalculatorTab.FOOD -> FoodForm(
                        onCalculate = { type, qty -> viewModel.calculateFood(type, qty) }
                    )
                }

                if (state.isLoading) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                state.result?.let { carbonKg ->
                    ResultCard(
                        carbonKg = carbonKg,
                        onSave = {
                            val actType = when (selectedTab) {
                                CalculatorTab.CAR -> ActivityType.CAR
                                CalculatorTab.FLIGHT -> ActivityType.FLIGHT
                                CalculatorTab.ELECTRICITY -> ActivityType.ELECTRICITY
                                CalculatorTab.FOOD -> ActivityType.FOOD_BEEF
                            }
                            val desc = when (selectedTab) {
                                CalculatorTab.CAR -> "Viagem de carro"
                                CalculatorTab.FLIGHT -> "Voo"
                                CalculatorTab.ELECTRICITY -> "Consumo elétrico"
                                CalculatorTab.FOOD -> "Alimentação"
                            }
                            viewModel.saveActivity(actType, desc)
                        }
                    )
                }

                state.error?.let {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            "Erro: $it",
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CarForm(onCalculate: (Double) -> Unit) {
    var distance by remember { mutableStateOf("") }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        FormHeader(
            icon = Icons.Default.DirectionsCar,
            title = "Viagem de carro",
            subtitle = "Calculado com base no consumo médio de um sedan brasileiro"
        )
        OutlinedTextField(
            value = distance,
            onValueChange = { distance = it },
            label = { Text("Distância percorrida (km)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )
        Button(
            onClick = { distance.toDoubleOrNull()?.let(onCalculate) },
            enabled = distance.toDoubleOrNull() != null && distance.toDouble() > 0,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) { Text("Calcular") }
    }
}

@Composable
private fun FlightForm(onCalculate: (String, String, Int) -> Unit) {
    var origin by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var passengers by remember { mutableStateOf("1") }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        FormHeader(
            icon = Icons.Default.Flight,
            title = "Voo",
            subtitle = "Use códigos IATA: GRU (Guarulhos), GIG (Galeão), BSB (Brasília)"
        )
        OutlinedTextField(
            value = origin, onValueChange = { origin = it.uppercase() },
            label = { Text("Aeroporto de origem (ex: GRU)") },
            modifier = Modifier.fillMaxWidth(), singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
        OutlinedTextField(
            value = destination, onValueChange = { destination = it.uppercase() },
            label = { Text("Aeroporto de destino (ex: GIG)") },
            modifier = Modifier.fillMaxWidth(), singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
        OutlinedTextField(
            value = passengers, onValueChange = { passengers = it },
            label = { Text("Número de passageiros") },
            modifier = Modifier.fillMaxWidth(), singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(12.dp)
        )
        Button(
            onClick = {
                val pax = passengers.toIntOrNull() ?: 1
                if (origin.length >= 3 && destination.length >= 3) {
                    onCalculate(origin, destination, pax)
                }
            },
            enabled = origin.length >= 3 && destination.length >= 3,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) { Text("Calcular") }
    }
}

@Composable
private fun ElectricityForm(onCalculate: (Double) -> Unit) {
    var kwh by remember { mutableStateOf("") }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        FormHeader(
            icon = Icons.Default.Bolt,
            title = "Consumo de energia elétrica",
            subtitle = "Informe o valor da sua conta de luz em kWh"
        )
        OutlinedTextField(
            value = kwh, onValueChange = { kwh = it },
            label = { Text("Consumo em kWh") },
            modifier = Modifier.fillMaxWidth(), singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            shape = RoundedCornerShape(12.dp)
        )
        Button(
            onClick = { kwh.toDoubleOrNull()?.let(onCalculate) },
            enabled = kwh.toDoubleOrNull() != null && kwh.toDouble() > 0,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) { Text("Calcular") }
    }
}

@Composable
private fun FoodForm(onCalculate: (ActivityType, Double) -> Unit) {
    var quantity by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(ActivityType.FOOD_BEEF) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        FormHeader(
            icon = Icons.Default.Restaurant,
            title = "Alimentação",
            subtitle = "Estime o impacto do seu consumo alimentar"
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(ActivityType.FOOD_BEEF, ActivityType.FOOD_CHICKEN).forEach { type ->
                FilterChip(
                    selected = selectedType == type,
                    onClick = { selectedType = type },
                    label = { Text(type.label) }
                )
            }
        }
        OutlinedTextField(
            value = quantity, onValueChange = { quantity = it },
            label = { Text("Quantidade consumida (kg)") },
            modifier = Modifier.fillMaxWidth(), singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            shape = RoundedCornerShape(12.dp)
        )
        Button(
            onClick = { quantity.toDoubleOrNull()?.let { onCalculate(selectedType, it) } },
            enabled = quantity.toDoubleOrNull() != null && quantity.toDouble() > 0,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) { Text("Calcular") }
    }
}

@Composable
private fun FormHeader(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(icon, contentDescription = null,
            tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
        Column {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun ResultCard(carbonKg: Double, onSave: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.CheckCircle, contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary)
                Text("Resultado calculado", fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleMedium)
            }
            Text(
                text = "${String.format("%.3f", carbonKg)} kg de CO\u2082",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Equivale a ${String.format("%.1f", carbonKg / 22)} árvores necessárias para absorver",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
            Button(
                onClick = onSave,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) { Text("Salvar no meu histórico") }
        }
    }
}
