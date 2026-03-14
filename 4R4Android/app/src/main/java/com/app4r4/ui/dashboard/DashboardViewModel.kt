package com.app4r4.ui.dashboard

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app4r4.data.db.ActivityRecord
import com.app4r4.data.repository.CarbonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.util.Calendar
import javax.inject.Inject

data class DashboardUiState(
    val userName: String = "",
    val totalCarbonThisMonth: Double = 0.0,
    val recentActivities: List<ActivityRecord> = emptyList(),
    val monthlyAvgBrazil: Double = 1083.0, // kg CO2 per capita mensal Brasil
    val carbonLevel: CarbonLevel = CarbonLevel.GOOD
)

enum class CarbonLevel(val label: String, val description: String) {
    GREAT("Excelente", "Sua pegada está abaixo da média nacional"),
    GOOD("Bom", "Você está próximo da meta sustentável"),
    MODERATE("Moderado", "Há espaço para melhorar"),
    HIGH("Alto", "Considere reduzir atividades de alta emissão")
}

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: CarbonRepository,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        val nameKey = stringPreferencesKey("user_name")

        viewModelScope.let {
            combine(
                repository.getTotalCarbonThisMonth(),
                repository.getRecordsThisMonth(),
                dataStore.data.map { it[nameKey] ?: "Usuário" }
            ) { total, records, name ->
                val carbon = total ?: 0.0
                DashboardUiState(
                    userName = name,
                    totalCarbonThisMonth = carbon,
                    recentActivities = records.take(5),
                    carbonLevel = when {
                        carbon < 500 -> CarbonLevel.GREAT
                        carbon < 800 -> CarbonLevel.GOOD
                        carbon < 1200 -> CarbonLevel.MODERATE
                        else -> CarbonLevel.HIGH
                    }
                )
            }.stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                DashboardUiState()
            ).collect { state ->
                _uiState.value = state
            }
        }
    }
}
