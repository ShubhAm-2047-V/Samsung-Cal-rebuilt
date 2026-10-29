package com.shubdeep.calculatorpro.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shubdeep.calculatorpro.data.local.*
import com.shubdeep.calculatorpro.data.repository.CurrencyRepository
import com.shubdeep.calculatorpro.data.repository.HistoryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val preferencesManager: PreferencesManager,
    private val historyRepository: HistoryRepository,
    private val currencyRepository: CurrencyRepository
) : ViewModel() {

    val userSettings: StateFlow<UserSettings> = preferencesManager.userSettingsFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, UserSettings())

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { preferencesManager.setThemeMode(mode) }
    }

    fun setHapticFeedback(enabled: Boolean) {
        viewModelScope.launch { preferencesManager.setHapticFeedback(enabled) }
    }

    fun setSoundFeedback(enabled: Boolean) {
        viewModelScope.launch { preferencesManager.setSoundFeedback(enabled) }
    }

    fun setNumberFormat(format: NumberFormatPreference) {
        viewModelScope.launch { preferencesManager.setNumberFormat(format) }
    }

    fun setDecimalPrecision(precision: String) {
        viewModelScope.launch { preferencesManager.setDecimalPrecision(precision) }
    }

    fun setDefaultBaseCurrency(currencyCode: String) {
        viewModelScope.launch { preferencesManager.setDefaultBaseCurrency(currencyCode) }
    }

    fun clearHistory() {
        viewModelScope.launch { historyRepository.clearAll() }
    }

    fun clearCurrencyCache() {
        viewModelScope.launch { currencyRepository.clearCache() }
    }
}
