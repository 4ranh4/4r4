package com.app4r4.ui.tips

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class EsgTip(
    val category: String,
    val title: String,
    val description: String,
    val impact: String,
    val impactLevel: ImpactLevel,
    val icon: ImageVector
)

enum class ImpactLevel(val label: String, val color: Color) {
    HIGH("Alto impacto", Color(0xFF1B6B2F)),
    MEDIUM("Médio impacto", Color(0xFFEF9F27)),
    LOW("Baixo impacto", Color(0xFF185FA5))
}

val esgTips = listOf(
    EsgTip("Transporte", "Use transporte público", "Substituir o carro particular pelo metrô ou ônibus pode reduzir até 2,4 t de CO₂ por ano.", "Redução de até 70% nas emissões de transporte", ImpactLevel.HIGH, Icons.Default.DirectionsBus),
    EsgTip("Transporte", "Opte por carona solidária", "Dividir o carro com colegas de trabalho reduz emissões e custos pela metade.", "Redução de 50% nas emissões por km", ImpactLevel.HIGH, Icons.Default.PeopleAlt),
    EsgTip("Alimentação", "Reduza o consumo de carne vermelha", "Produzir 1 kg de carne bovina emite até 27 kg de CO₂. Substituir por leguminosas pode fazer grande diferença.", "Economia de 1–3 t CO₂/ano", ImpactLevel.HIGH, Icons.Default.SetMeal),
    EsgTip("Alimentação", "Prefira alimentos locais e sazonais", "Alimentos transportados longas distâncias geram mais emissões. Feiras locais são aliadas do clima.", "Redução de 10–15% na pegada alimentar", ImpactLevel.MEDIUM, Icons.Default.StoreMallDirectory),
    EsgTip("Energia", "Instale energia solar", "Painéis solares podem zerar suas emissões residenciais de energia e gerar créditos.", "Redução de até 100% nas emissões domésticas", ImpactLevel.HIGH, Icons.Default.WbSunny),
    EsgTip("Energia", "Substitua lâmpadas por LED", "LEDs consomem até 80% menos energia que lâmpadas incandescentes e duram mais.", "Redução de 4–6% na conta de luz", ImpactLevel.LOW, Icons.Default.Lightbulb),
    EsgTip("Energia", "Desligue aparelhos em standby", "Aparelhos em modo standby consomem até 10% da energia residencial sem necessidade.", "Economia de 50–150 kWh/mês", ImpactLevel.MEDIUM, Icons.Default.PowerSettingsNew),
    EsgTip("Consumo", "Compre menos, compre melhor", "A indústria da moda é responsável por 10% das emissões globais. Prefira durabilidade à quantidade.", "Impacto direto no ciclo de vida dos produtos", ImpactLevel.MEDIUM, Icons.Default.ShoppingBag),
    EsgTip("Consumo", "Evite o desperdício de alimentos", "30% dos alimentos produzidos são desperdiçados. Planejar compras reduz emissões e despesas.", "Redução de 6–8% na pegada alimentar", ImpactLevel.MEDIUM, Icons.Default.DeleteOutline),
    EsgTip("Natureza", "Plante árvores ou adote uma", "Uma árvore adulta absorve em média 22 kg de CO₂ por ano. Apoie programas de reflorestamento.", "Compensação passiva de emissões", ImpactLevel.LOW, Icons.Default.Park),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TipsScreen(onBack: () -> Unit) {
    var selectedCategory by remember { mutableStateOf("Todas") }
    val categories = listOf("Todas") + esgTips.map { it.category }.distinct()

    val filteredTips = if (selectedCategory == "Todas") esgTips
    else esgTips.filter { it.category == selectedCategory }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dicas ESG") },
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    "Ações para reduzir sua pegada de carbono",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    categories.forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat) }
                        )
                    }
                }
            }

            items(filteredTips) { tip ->
                TipCard(tip)
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun TipCard(tip: EsgTip) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        onClick = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        tip.icon, contentDescription = null,
                        modifier = Modifier.padding(8.dp).size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(tip.title, style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold)
                    Text(tip.category, style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Icon(
                    if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (expanded) {
                Spacer(Modifier.height(12.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                Spacer(Modifier.height(12.dp))
                Text(tip.description, style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(8.dp))
                Surface(
                    color = tip.impactLevel.color.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.TrendingDown, contentDescription = null,
                            modifier = Modifier.size(14.dp), tint = tip.impactLevel.color)
                        Text(tip.impact, style = MaterialTheme.typography.bodySmall,
                            color = tip.impactLevel.color, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}
