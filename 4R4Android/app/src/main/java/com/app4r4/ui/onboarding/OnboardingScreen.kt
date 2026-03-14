package com.app4r4.ui.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OnboardingScreen(onFinish: (transport: String, diet: String) -> Unit) {
    var currentPage by remember { mutableIntStateOf(0) }
    var selectedTransport by remember { mutableStateOf("") }
    var selectedDiet by remember { mutableStateOf("") }
    var userName by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (currentPage) {
            0 -> WelcomePage(onNext = { currentPage = 1 })
            1 -> NamePage(
                name = userName,
                onNameChange = { userName = it },
                onNext = { currentPage = 2 }
            )
            2 -> TransportPage(
                selected = selectedTransport,
                onSelect = { selectedTransport = it },
                onNext = { currentPage = 3 }
            )
            3 -> DietPage(
                selected = selectedDiet,
                onSelect = { selectedDiet = it },
                onFinish = { onFinish(selectedTransport, selectedDiet) }
            )
        }
    }
}

@Composable
private fun WelcomePage(onNext: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Eco,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(96.dp)
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = "App4R4",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "Calcule, entenda e reduza sua pegada de carbono no dia a dia.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            FeaturePill(icon = Icons.Default.DirectionsCar, text = "Transporte")
            FeaturePill(icon = Icons.Default.Flight, text = "Viagens")
            FeaturePill(icon = Icons.Default.Bolt, text = "Energia")
        }
        Spacer(Modifier.height(48.dp))
        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Começar", modifier = Modifier.padding(vertical = 8.dp))
        }
    }
}

@Composable
private fun FeaturePill(icon: ImageVector, text: String) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(14.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer)
            Text(text, style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer)
        }
    }
}

@Composable
private fun NamePage(name: String, onNameChange: (String) -> Unit, onNext: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center
    ) {
        StepIndicator(current = 1, total = 3)
        Spacer(Modifier.height(32.dp))
        Text("Como você se chama?", style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text("Vamos personalizar sua experiência.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(32.dp))
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Seu nome") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )
        Spacer(Modifier.height(32.dp))
        Button(
            onClick = onNext,
            enabled = name.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Continuar", modifier = Modifier.padding(vertical = 8.dp))
        }
    }
}

@Composable
private fun TransportPage(selected: String, onSelect: (String) -> Unit, onNext: () -> Unit) {
    val options = listOf(
        Triple("Carro particular", Icons.Default.DirectionsCar, "car"),
        Triple("Transporte público", Icons.Default.DirectionsBus, "bus"),
        Triple("Bicicleta / a pé", Icons.Default.DirectionsBike, "bike"),
        Triple("Moto", Icons.Default.TwoWheeler, "moto")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(32.dp)
    ) {
        Spacer(Modifier.height(48.dp))
        StepIndicator(current = 2, total = 3)
        Spacer(Modifier.height(32.dp))
        Text("Qual seu principal\nmodo de transporte?",
            style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(24.dp))
        options.forEach { (label, icon, key) ->
            SelectionCard(
                label = label,
                icon = icon,
                selected = selected == key,
                onClick = { onSelect(key) }
            )
            Spacer(Modifier.height(12.dp))
        }
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onNext,
            enabled = selected.isNotEmpty(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Continuar", modifier = Modifier.padding(vertical = 8.dp))
        }
    }
}

@Composable
private fun DietPage(selected: String, onSelect: (String) -> Unit, onFinish: () -> Unit) {
    val options = listOf(
        Triple("Onívoro (come carne)", Icons.Default.SetMeal, "omnivore"),
        Triple("Flexitariano (pouca carne)", Icons.Default.Restaurant, "flexitarian"),
        Triple("Vegetariano", Icons.Default.SpaOutlined, "vegetarian"),
        Triple("Vegano", Icons.Default.EnergySavingsLeaf, "vegan")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(32.dp)
    ) {
        Spacer(Modifier.height(48.dp))
        StepIndicator(current = 3, total = 3)
        Spacer(Modifier.height(32.dp))
        Text("Qual é o seu padrão alimentar?",
            style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text("A alimentação pode representar até 30% da pegada de carbono individual.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(24.dp))
        options.forEach { (label, icon, key) ->
            SelectionCard(
                label = label,
                icon = icon,
                selected = selected == key,
                onClick = { onSelect(key) }
            )
            Spacer(Modifier.height(12.dp))
        }
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onFinish,
            enabled = selected.isNotEmpty(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Começar a monitorar", modifier = Modifier.padding(vertical = 8.dp))
        }
    }
}

@Composable
private fun SelectionCard(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (selected) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    val bgColor = if (selected) MaterialTheme.colorScheme.primaryContainer
    else MaterialTheme.colorScheme.surface

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(icon, contentDescription = null,
            tint = if (selected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp))
        Text(label, style = MaterialTheme.typography.bodyLarge,
            color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
            else MaterialTheme.colorScheme.onSurface)
        Spacer(Modifier.weight(1f))
        if (selected) {
            Icon(Icons.Default.CheckCircle, contentDescription = null,
                tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun StepIndicator(current: Int, total: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(total) { index ->
            Box(
                modifier = Modifier
                    .height(4.dp)
                    .width(if (index + 1 == current) 32.dp else 16.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        if (index + 1 <= current) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
            )
        }
    }
}
