package com.shubdeep.calculatorpro.ui.calculator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shubdeep.calculatorpro.data.local.AngleMode
import com.shubdeep.calculatorpro.data.local.CalculationType
import com.shubdeep.calculatorpro.data.local.PreferencesManager
import com.shubdeep.calculatorpro.data.local.UserSettings
import com.shubdeep.calculatorpro.data.repository.HistoryRepository
import com.shubdeep.calculatorpro.domain.calculator.CalculatorState
import com.shubdeep.calculatorpro.domain.calculator.ExpressionEvaluator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CalculatorViewModel(
    private val historyRepository: HistoryRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    val userSettings: StateFlow<UserSettings> = preferencesManager.userSettingsFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, UserSettings())

    private val evaluator = ExpressionEvaluator()

    private val _uiState = MutableStateFlow(CalculatorState())
    val uiState: StateFlow<CalculatorState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            userSettings.collect { settings ->
                evaluator.updatePreferences(
                    angleMode = settings.angleMode,
                    numberFormat = settings.numberFormat,
                    precision = settings.decimalPrecision
                )
                _uiState.value = _uiState.value.copy(angleMode = settings.angleMode)
            }
        }
    }

    fun onDigit(digit: String) {
        _uiState.value = evaluator.onDigit(_uiState.value, digit)
    }

    fun onDecimal() {
        _uiState.value = evaluator.onDecimal(_uiState.value)
    }

    fun onOperator(op: String) {
        _uiState.value = evaluator.onOperator(_uiState.value, op)
    }

    fun onParenthesis() {
        _uiState.value = evaluator.onParenthesis(_uiState.value)
    }

    fun onFunction(func: String) {
        _uiState.value = evaluator.onFunction(_uiState.value, func)
    }

    fun onConstant(constant: String) {
        _uiState.value = evaluator.onConstant(_uiState.value, constant)
    }

    fun onToggleSign() {
        _uiState.value = evaluator.onToggleSign(_uiState.value)
    }

    fun onBackspace() {
        _uiState.value = evaluator.onBackspace(_uiState.value)
    }

    fun onClear() {
        _uiState.value = evaluator.onClear(_uiState.value)
    }

    fun onEquals() {
        val (newState, shouldRecord) = evaluator.onEquals(_uiState.value)
        _uiState.value = newState

        if (shouldRecord) {
            viewModelScope.launch {
                val type = if (_uiState.value.isScientificExpanded) CalculationType.SCIENTIFIC else CalculationType.STANDARD
                historyRepository.addRecord(
                    expression = newState.expression,
                    result = newState.liveResult,
                    type = type
                )
            }
        }
    }

    fun toggleScientific() {
        _uiState.value = _uiState.value.copy(
            isScientificExpanded = !_uiState.value.isScientificExpanded
        )
    }

    fun toggleAngleMode() {
        val newMode = if (userSettings.value.angleMode == AngleMode.DEG) AngleMode.RAD else AngleMode.DEG
        viewModelScope.launch {
            preferencesManager.setAngleMode(newMode)
        }
    }

    fun setExpression(expr: String) {
        _uiState.value = _uiState.value.copy(
            expression = expr,
            liveResult = "",
            error = null,
            isEvaluated = false
        )
    }
}
