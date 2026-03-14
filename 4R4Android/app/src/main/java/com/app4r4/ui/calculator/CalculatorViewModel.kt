package com.app4r4.ui.calculator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app4r4.data.model.ActivityType
import com.app4r4.data.repository.CarbonRepository
import com.app4r4.data.repository.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CalculatorUiState(
    val isLoading: Boolean = false,
    val result: Double? = null,
    val error: String? = null,
    val saved: Boolean = false
)

@HiltViewModel
class CalculatorViewModel @Inject constructor(
    private val repository: CarbonRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    fun calculateVehicle(distanceKm: Double) {
        viewModelScope.launch {
            _uiState.value = CalculatorUiState(isLoading = true)
            when (val result = repository.calculateVehicleEmission(distanceKm)) {
                is Result.Success -> _uiState.value = CalculatorUiState(result = result.data)
                is Result.Error -> _uiState.value = CalculatorUiState(error = result.message)
                else -> Unit
            }
        }
    }

    fun calculateFlight(origin: String, destination: String, passengers: Int) {
        viewModelScope.launch {
            _uiState.value = CalculatorUiState(isLoading = true)
            when (val result = repository.calculateFlightEmission(origin, destination, passengers)) {
                is Result.Success -> _uiState.value = CalculatorUiState(result = result.data)
                is Result.Error -> _uiState.value = CalculatorUiState(error = result.message)
                else -> Unit
            }
        }
    }

    fun calculateElectricity(kwh: Double) {
        viewModelScope.launch {
            _uiState.value = CalculatorUiState(isLoading = true)
            when (val result = repository.calculateElectricityEmission(kwh)) {
                is Result.Success -> _uiState.value = CalculatorUiState(result = result.data)
                is Result.Error -> _uiState.value = CalculatorUiState(error = result.message)
                else -> Unit
            }
        }
    }

    fun calculateFood(type: ActivityType, quantityKg: Double) {
        // Estimativas locais kg CO2 por kg de alimento
        val factor = when (type) {
            ActivityType.FOOD_BEEF -> 27.0
            ActivityType.FOOD_CHICKEN -> 6.9
            else -> 0.0
        }
        _uiState.value = CalculatorUiState(result = quantityKg * factor)
    }

    fun saveActivity(type: ActivityType, description: String) {
        val carbon = _uiState.value.result ?: return
        viewModelScope.launch {
            repository.saveActivity(type, description, carbon)
            _uiState.value = _uiState.value.copy(saved = true)
        }
    }

    fun reset() {
        _uiState.value = CalculatorUiState()
    }
}
