package com.shubdeep.calculatorpro.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shubdeep.calculatorpro.data.local.CalculationEntity
import com.shubdeep.calculatorpro.data.local.CalculationType
import com.shubdeep.calculatorpro.data.repository.HistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class HistoryFilter {
    ALL,
    CALCULATOR,
    CURRENCY,
    UNIT,
    PRODUCTIVITY
}

class HistoryViewModel(
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val _selectedFilter = MutableStateFlow(HistoryFilter.ALL)
    val selectedFilter: StateFlow<HistoryFilter> = _selectedFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val historyList: StateFlow<List<CalculationEntity>> = combine(
        historyRepository.getAllHistory(),
        _selectedFilter,
        _searchQuery
    ) { all, filter, query ->
        all.filter { record ->
            val matchesFilter = when (filter) {
                HistoryFilter.ALL -> true
                HistoryFilter.CALCULATOR -> record.type == CalculationType.STANDARD || record.type == CalculationType.SCIENTIFIC
                HistoryFilter.CURRENCY -> record.type == CalculationType.CURRENCY
                HistoryFilter.UNIT -> record.type == CalculationType.UNIT
                HistoryFilter.PRODUCTIVITY -> record.type in listOf(
                    CalculationType.GST,
                    CalculationType.EMI,
                    CalculationType.PERCENTAGE,
                    CalculationType.INTEREST,
                    CalculationType.TIP
                )
            }
            val matchesQuery = if (query.isBlank()) true else {
                record.expression.contains(query, ignoreCase = true) ||
                record.result.contains(query, ignoreCase = true) ||
                (record.details?.contains(query, ignoreCase = true) == true)
            }
            matchesFilter && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun setFilter(filter: HistoryFilter) {
        _selectedFilter.value = filter
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun deleteItem(record: CalculationEntity) {
        viewModelScope.launch {
            historyRepository.deleteRecord(record)
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            historyRepository.clearAll()
        }
    }
}
