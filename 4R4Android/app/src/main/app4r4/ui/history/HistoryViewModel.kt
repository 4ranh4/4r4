package com.app4r4.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app4r4.data.db.ActivityRecord
import com.app4r4.data.repository.CarbonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class MonthlyTotal(val label: String, val carbonKg: Double)

data class HistoryUiState(
    val records: List<ActivityRecord> = emptyList(),
    val monthlyTotals: List<MonthlyTotal> = emptyList(),
    val totalAllTime: Double = 0.0,
    val savedTrees: Int = 0
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: CarbonRepository
) : ViewModel() {

    val uiState: StateFlow<HistoryUiState> = repository.getAllRecords()
        .map { records ->
            val total = records.sumOf { it.carbonKg }
            val monthly = buildMonthlyTotals(records)
            HistoryUiState(
                records = records,
                monthlyTotals = monthly,
                totalAllTime = total,
                savedTrees = (total / 22).toInt()
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HistoryUiState())

    fun deleteRecord(record: ActivityRecord) {
        viewModelScope.launch { repository.deleteRecord(record) }
    }

    private fun buildMonthlyTotals(records: List<ActivityRecord>): List<MonthlyTotal> {
        val monthNames = listOf("Jan","Fev","Mar","Abr","Mai","Jun","Jul","Ago","Set","Out","Nov","Dez")
        return records
            .groupBy {
                val cal = Calendar.getInstance().apply { timeInMillis = it.date }
                cal.get(Calendar.MONTH)
            }
            .map { (month, recs) ->
                MonthlyTotal(monthNames[month], recs.sumOf { it.carbonKg })
            }
            .sortedBy { monthNames.indexOf(it.label) }
            .takeLast(6)
    }
}
