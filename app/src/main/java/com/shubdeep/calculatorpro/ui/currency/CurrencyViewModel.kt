package com.shubdeep.calculatorpro.ui.currency

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shubdeep.calculatorpro.data.local.CalculationType
import com.shubdeep.calculatorpro.data.local.PreferencesManager
import com.shubdeep.calculatorpro.data.remote.CurrencyInfo
import com.shubdeep.calculatorpro.data.repository.ConversionResult
import com.shubdeep.calculatorpro.data.repository.CurrencyRatesResult
import com.shubdeep.calculatorpro.data.repository.CurrencyRepository
import com.shubdeep.calculatorpro.data.repository.HistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal

data class CurrencyUiState(
    val amountInput: String = "100",
    val fromCurrency: String = "USD",
    val toCurrency: String = "INR",
    val convertedAmount: BigDecimal = BigDecimal.ZERO,
    val exchangeRate: BigDecimal = BigDecimal.ONE,
    val inverseRate: BigDecimal = BigDecimal.ONE,
    val lastUpdatedUtc: String = "Loading...",
    val provider: String = "ExchangeRate-API",
    val isOffline: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val rates: Map<String, Double> = emptyMap(),
    val favoritePairs: List<Pair<String, String>> = listOf(
        Pair("USD", "INR"),
        Pair("EUR", "INR"),
        Pair("GBP", "INR"),
        Pair("AED", "INR"),
        Pair("SAR", "INR"),
        Pair("CAD", "INR"),
        Pair("JPY", "INR"),
        Pair("AUD", "INR"),
        Pair("INR", "USD"),
        Pair("INR", "EUR")
    )
)

class CurrencyViewModel(
    private val currencyRepository: CurrencyRepository,
    private val historyRepository: HistoryRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(CurrencyUiState())
    val uiState: StateFlow<CurrencyUiState> = _uiState.asStateFlow()

    val supportedCurrencies: List<CurrencyInfo> = currencyRepository.supportedCurrencies

    init {
        viewModelScope.launch {
            preferencesManager.userSettingsFlow.collect { settings ->
                // Set default base and target currency from settings if uninitialized
                if (_uiState.value.rates.isEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        fromCurrency = settings.defaultTargetCurrency, // USD
                        toCurrency = settings.defaultBaseCurrency      // INR
                    )
                }
            }
        }
        loadRates(forceRefresh = false)
    }

    fun loadRates(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = currencyRepository.getExchangeRates(forceRefresh)

            _uiState.value = _uiState.value.copy(
                rates = result.ratesAgainstUsd,
                lastUpdatedUtc = result.lastUpdatedUtc,
                provider = result.provider,
                isOffline = result.isOffline,
                isLoading = false,
                errorMessage = result.errorMessage
            )
            recalculate()
        }
    }

    fun onAmountChanged(newAmount: String) {
        val clean = newAmount.filter { it.isDigit() || it == '.' }
        if (clean.count { it == '.' } > 1) return
        _uiState.value = _uiState.value.copy(amountInput = clean.ifEmpty { "0" })
        recalculate()
    }

    fun onKeypadDigit(digit: String) {
        val current = _uiState.value.amountInput
        val updated = if (current == "0") digit else current + digit
        if (updated.length <= 12) {
            _uiState.value = _uiState.value.copy(amountInput = updated)
            recalculate()
        }
    }

    fun onKeypadDecimal() {
        val current = _uiState.value.amountInput
        if (!current.contains(".")) {
            _uiState.value = _uiState.value.copy(amountInput = "$current.")
            recalculate()
        }
    }

    fun onKeypadBackspace() {
        val current = _uiState.value.amountInput
        val updated = if (current.length <= 1) "0" else current.dropLast(1)
        _uiState.value = _uiState.value.copy(amountInput = updated)
        recalculate()
    }

    fun onKeypadClear() {
        _uiState.value = _uiState.value.copy(amountInput = "0")
        recalculate()
    }

    fun onFromCurrencySelected(code: String) {
        _uiState.value = _uiState.value.copy(fromCurrency = code)
        recalculate()
    }

    fun onToCurrencySelected(code: String) {
        _uiState.value = _uiState.value.copy(toCurrency = code)
        recalculate()
    }

    fun swapCurrencies() {
        val oldFrom = _uiState.value.fromCurrency
        val oldTo = _uiState.value.toCurrency
        _uiState.value = _uiState.value.copy(
            fromCurrency = oldTo,
            toCurrency = oldFrom
        )
        recalculate()
    }

    fun selectPair(from: String, to: String) {
        _uiState.value = _uiState.value.copy(
            fromCurrency = from,
            toCurrency = to
        )
        recalculate()
    }

    fun recordConversionToHistory() {
        val state = _uiState.value
        if (state.convertedAmount <= BigDecimal.ZERO) return

        viewModelScope.launch {
            val fromSymbol = currencyRepository.getCurrencyInfo(state.fromCurrency).symbol
            val toSymbol = currencyRepository.getCurrencyInfo(state.toCurrency).symbol
            val expr = "${state.amountInput} ${state.fromCurrency} ($fromSymbol)"
            val res = "${state.convertedAmount.toPlainString()} ${state.toCurrency} ($toSymbol)"
            val details = "Rate: 1 ${state.fromCurrency} = ${state.exchangeRate.toPlainString()} ${state.toCurrency} | ${state.lastUpdatedUtc}"

            historyRepository.addRecord(
                expression = expr,
                result = res,
                type = CalculationType.CURRENCY,
                details = details
            )
        }
    }

    private fun recalculate() {
        val state = _uiState.value
        val amount = try {
            if (state.amountInput.isBlank() || state.amountInput == ".") BigDecimal.ZERO else BigDecimal(state.amountInput)
        } catch (e: Exception) {
            BigDecimal.ZERO
        }

        if (state.rates.isEmpty()) return

        val result = currencyRepository.convert(
            amount = amount,
            fromCurrency = state.fromCurrency,
            toCurrency = state.toCurrency,
            ratesAgainstUsd = state.rates
        )

        if (result.isSuccess) {
            _uiState.value = _uiState.value.copy(
                convertedAmount = result.convertedAmount,
                exchangeRate = result.rate,
                inverseRate = result.inverseRate,
                errorMessage = null
            )
        } else {
            _uiState.value = _uiState.value.copy(
                errorMessage = result.errorMessage
            )
        }
    }

    fun getCurrencyInfo(code: String): CurrencyInfo = currencyRepository.getCurrencyInfo(code)
}
