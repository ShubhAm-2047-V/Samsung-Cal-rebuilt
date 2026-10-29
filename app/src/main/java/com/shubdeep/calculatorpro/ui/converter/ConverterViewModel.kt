package com.shubdeep.calculatorpro.ui.converter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shubdeep.calculatorpro.data.local.CalculationType
import com.shubdeep.calculatorpro.data.repository.HistoryRepository
import com.shubdeep.calculatorpro.domain.calculator.NumberFormatterUtil
import com.shubdeep.calculatorpro.domain.converter.ConverterCategory
import com.shubdeep.calculatorpro.domain.converter.UnitConverterEngine
import com.shubdeep.calculatorpro.domain.converter.UnitItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

data class ConverterUiState(
    val selectedCategory: ConverterCategory = ConverterCategory.LENGTH,
    val inputValue: String = "1",
    val fromUnit: UnitItem? = null,
    val toUnit: UnitItem? = null,
    val convertedResult: String = "",
    val availableUnits: List<UnitItem> = emptyList(),

    // Specialized States
    val numeralInput: String = "42",
    val numeralFromBase: Int = 10,
    val numeralBin: String = "",
    val numeralOct: String = "",
    val numeralDec: String = "",
    val numeralHex: String = "",

    val bmiHeightCm: String = "175",
    val bmiWeightKg: String = "70",
    val bmiResult: UnitConverterEngine.BmiResult? = null,

    val ageBirthDate: LocalDate = LocalDate.of(2000, 1, 1),
    val ageResult: UnitConverterEngine.AgeResult? = null,

    val dateStart: LocalDate = LocalDate.now(),
    val dateEnd: LocalDate = LocalDate.now().plusDays(30),
    val dateDiffResult: UnitConverterEngine.DateDifferenceResult? = null,

    val discountPrice: String = "1000",
    val discountPercent: String = "20",
    val discountExtraPercent: String = "0",
    val discountTaxPercent: String = "0",
    val discountResult: UnitConverterEngine.DiscountResult? = null
)

class ConverterViewModel(
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConverterUiState())
    val uiState: StateFlow<ConverterUiState> = _uiState.asStateFlow()

    init {
        selectCategory(ConverterCategory.LENGTH)
        recalculateNumeral()
        recalculateBmi()
        recalculateAge()
        recalculateDateDiff()
        recalculateDiscount()
    }

    fun selectCategory(category: ConverterCategory) {
        val units = UnitConverterEngine.getUnitsForCategory(category)
        val from = units.firstOrNull()
        val to = if (units.size > 1) units[1] else units.firstOrNull()

        _uiState.value = _uiState.value.copy(
            selectedCategory = category,
            availableUnits = units,
            fromUnit = from,
            toUnit = to
        )
        recalculateLinear()
    }

    fun onInputChanged(newValue: String) {
        val clean = newValue.filter { it.isDigit() || it == '.' || it == '-' }
        _uiState.value = _uiState.value.copy(inputValue = clean.ifEmpty { "0" })
        recalculateLinear()
    }

    fun onKeypadDigit(digit: String) {
        val current = _uiState.value.inputValue
        val updated = if (current == "0") digit else current + digit
        if (updated.length <= 15) {
            _uiState.value = _uiState.value.copy(inputValue = updated)
            recalculateLinear()
        }
    }

    fun onKeypadDecimal() {
        val current = _uiState.value.inputValue
        if (!current.contains(".")) {
            _uiState.value = _uiState.value.copy(inputValue = "$current.")
            recalculateLinear()
        }
    }

    fun onKeypadBackspace() {
        val current = _uiState.value.inputValue
        val updated = if (current.length <= 1 || (current.length == 2 && current.startsWith("-"))) {
            "0"
        } else {
            current.dropLast(1)
        }
        _uiState.value = _uiState.value.copy(inputValue = updated)
        recalculateLinear()
    }

    fun onKeypadClear() {
        _uiState.value = _uiState.value.copy(inputValue = "0")
        recalculateLinear()
    }

    fun onKeypadToggleSign() {
        val current = _uiState.value.inputValue
        val updated = if (current.startsWith("-")) {
            current.removePrefix("-")
        } else if (current != "0") {
            "-$current"
        } else {
            "0"
        }
        _uiState.value = _uiState.value.copy(inputValue = updated)
        recalculateLinear()
    }

    fun setFromUnit(unit: UnitItem) {
        _uiState.value = _uiState.value.copy(fromUnit = unit)
        recalculateLinear()
    }

    fun setToUnit(unit: UnitItem) {
        _uiState.value = _uiState.value.copy(toUnit = unit)
        recalculateLinear()
    }

    fun swapUnits() {
        val currentFrom = _uiState.value.fromUnit
        val currentTo = _uiState.value.toUnit
        _uiState.value = _uiState.value.copy(
            fromUnit = currentTo,
            toUnit = currentFrom
        )
        recalculateLinear()
    }

    private fun recalculateLinear() {
        val state = _uiState.value
        val valDouble = state.inputValue.toDoubleOrNull() ?: 0.0
        val from = state.fromUnit
        val to = state.toUnit

        if (from == null || to == null) return

        val resultDouble = when (state.selectedCategory) {
            ConverterCategory.TEMPERATURE -> UnitConverterEngine.convertTemperature(valDouble, from.id, to.id)
            ConverterCategory.FUEL_ECONOMY -> UnitConverterEngine.convertFuelEconomy(valDouble, from.id, to.id)
            else -> UnitConverterEngine.convertLinear(valDouble, from, to)
        }

        val formatted = NumberFormatterUtil.formatDouble(resultDouble)
        _uiState.value = _uiState.value.copy(convertedResult = formatted)
    }

    // Numeral
    fun onNumeralInputChanged(input: String, fromBase: Int) {
        _uiState.value = _uiState.value.copy(numeralInput = input, numeralFromBase = fromBase)
        recalculateNumeral()
    }

    private fun recalculateNumeral() {
        val state = _uiState.value
        val bin = UnitConverterEngine.convertNumeral(state.numeralInput, state.numeralFromBase, 2)
        val oct = UnitConverterEngine.convertNumeral(state.numeralInput, state.numeralFromBase, 8)
        val dec = UnitConverterEngine.convertNumeral(state.numeralInput, state.numeralFromBase, 10)
        val hex = UnitConverterEngine.convertNumeral(state.numeralInput, state.numeralFromBase, 16)
        _uiState.value = _uiState.value.copy(
            numeralBin = bin,
            numeralOct = oct,
            numeralDec = dec,
            numeralHex = hex
        )
    }

    // BMI
    fun onBmiInputsChanged(heightCm: String, weightKg: String) {
        _uiState.value = _uiState.value.copy(bmiHeightCm = heightCm, bmiWeightKg = weightKg)
        recalculateBmi()
    }

    private fun recalculateBmi() {
        val h = _uiState.value.bmiHeightCm.toDoubleOrNull() ?: 175.0
        val w = _uiState.value.bmiWeightKg.toDoubleOrNull() ?: 70.0
        _uiState.value = _uiState.value.copy(bmiResult = UnitConverterEngine.calculateBmi(h, w))
    }

    // Age
    fun onBirthDateChanged(date: LocalDate) {
        _uiState.value = _uiState.value.copy(ageBirthDate = date)
        recalculateAge()
    }

    private fun recalculateAge() {
        _uiState.value = _uiState.value.copy(ageResult = UnitConverterEngine.calculateAge(_uiState.value.ageBirthDate))
    }

    // Date
    fun onDateRangeChanged(start: LocalDate, end: LocalDate) {
        _uiState.value = _uiState.value.copy(dateStart = start, dateEnd = end)
        recalculateDateDiff()
    }

    private fun recalculateDateDiff() {
        _uiState.value = _uiState.value.copy(
            dateDiffResult = UnitConverterEngine.calculateDateDifference(
                _uiState.value.dateStart,
                _uiState.value.dateEnd
            )
        )
    }

    // Discount
    fun onDiscountInputsChanged(price: String, disc: String, extra: String, tax: String) {
        _uiState.value = _uiState.value.copy(
            discountPrice = price,
            discountPercent = disc,
            discountExtraPercent = extra,
            discountTaxPercent = tax
        )
        recalculateDiscount()
    }

    private fun recalculateDiscount() {
        val p = _uiState.value.discountPrice.toDoubleOrNull() ?: 0.0
        val d = _uiState.value.discountPercent.toDoubleOrNull() ?: 0.0
        val ed = _uiState.value.discountExtraPercent.toDoubleOrNull() ?: 0.0
        val t = _uiState.value.discountTaxPercent.toDoubleOrNull() ?: 0.0
        _uiState.value = _uiState.value.copy(discountResult = UnitConverterEngine.calculateDiscount(p, d, ed, t))
    }

    // Keypad handlers for BMI
    fun onBmiKeypadDigit(digit: String, isHeightActive: Boolean) {
        if (isHeightActive) {
            val cur = _uiState.value.bmiHeightCm
            val up = if (cur == "0" || cur.isEmpty()) digit else cur + digit
            if (up.length <= 4) onBmiInputsChanged(up, _uiState.value.bmiWeightKg)
        } else {
            val cur = _uiState.value.bmiWeightKg
            val up = if (cur == "0" || cur.isEmpty()) digit else cur + digit
            if (up.length <= 4) onBmiInputsChanged(_uiState.value.bmiHeightCm, up)
        }
    }

    fun onBmiKeypadDecimal(isHeightActive: Boolean) {
        if (isHeightActive) {
            val cur = _uiState.value.bmiHeightCm
            if (!cur.contains(".")) onBmiInputsChanged("$cur.", _uiState.value.bmiWeightKg)
        } else {
            val cur = _uiState.value.bmiWeightKg
            if (!cur.contains(".")) onBmiInputsChanged(_uiState.value.bmiHeightCm, "$cur.")
        }
    }

    fun onBmiKeypadBackspace(isHeightActive: Boolean) {
        if (isHeightActive) {
            val cur = _uiState.value.bmiHeightCm
            val up = if (cur.length <= 1) "0" else cur.dropLast(1)
            onBmiInputsChanged(up, _uiState.value.bmiWeightKg)
        } else {
            val cur = _uiState.value.bmiWeightKg
            val up = if (cur.length <= 1) "0" else cur.dropLast(1)
            onBmiInputsChanged(_uiState.value.bmiHeightCm, up)
        }
    }

    fun onBmiKeypadClear(isHeightActive: Boolean) {
        if (isHeightActive) {
            onBmiInputsChanged("0", _uiState.value.bmiWeightKg)
        } else {
            onBmiInputsChanged(_uiState.value.bmiHeightCm, "0")
        }
    }

    // Keypad handlers for Discount
    fun onDiscountKeypadDigit(digit: String, isPriceActive: Boolean) {
        if (isPriceActive) {
            val cur = _uiState.value.discountPrice
            val up = if (cur == "0" || cur.isEmpty()) digit else cur + digit
            if (up.length <= 8) onDiscountInputsChanged(up, _uiState.value.discountPercent, _uiState.value.discountExtraPercent, _uiState.value.discountTaxPercent)
        } else {
            val cur = _uiState.value.discountPercent
            val up = if (cur == "0" || cur.isEmpty()) digit else cur + digit
            val num = up.toDoubleOrNull() ?: 0.0
            if (up.length <= 3 && num <= 100) onDiscountInputsChanged(_uiState.value.discountPrice, up, _uiState.value.discountExtraPercent, _uiState.value.discountTaxPercent)
        }
    }

    fun onDiscountKeypadDecimal(isPriceActive: Boolean) {
        if (isPriceActive) {
            val cur = _uiState.value.discountPrice
            if (!cur.contains(".")) onDiscountInputsChanged("$cur.", _uiState.value.discountPercent, _uiState.value.discountExtraPercent, _uiState.value.discountTaxPercent)
        } else {
            val cur = _uiState.value.discountPercent
            if (!cur.contains(".")) onDiscountInputsChanged(_uiState.value.discountPrice, "$cur.", _uiState.value.discountExtraPercent, _uiState.value.discountTaxPercent)
        }
    }

    fun onDiscountKeypadBackspace(isPriceActive: Boolean) {
        if (isPriceActive) {
            val cur = _uiState.value.discountPrice
            val up = if (cur.length <= 1) "0" else cur.dropLast(1)
            onDiscountInputsChanged(up, _uiState.value.discountPercent, _uiState.value.discountExtraPercent, _uiState.value.discountTaxPercent)
        } else {
            val cur = _uiState.value.discountPercent
            val up = if (cur.length <= 1) "0" else cur.dropLast(1)
            onDiscountInputsChanged(_uiState.value.discountPrice, up, _uiState.value.discountExtraPercent, _uiState.value.discountTaxPercent)
        }
    }

    fun onDiscountKeypadClear(isPriceActive: Boolean) {
        if (isPriceActive) {
            onDiscountInputsChanged("0", _uiState.value.discountPercent, _uiState.value.discountExtraPercent, _uiState.value.discountTaxPercent)
        } else {
            onDiscountInputsChanged(_uiState.value.discountPrice, "0", _uiState.value.discountExtraPercent, _uiState.value.discountTaxPercent)
        }
    }

    fun saveConversionToHistory() {
        val state = _uiState.value
        viewModelScope.launch {
            val expr = "${state.inputValue} ${state.fromUnit?.symbol ?: ""}"
            val res = "${state.convertedResult} ${state.toUnit?.symbol ?: ""}"
            val details = "Category: ${state.selectedCategory.displayName}"
            historyRepository.addRecord(
                expression = expr,
                result = res,
                type = CalculationType.UNIT,
                details = details
            )
        }
    }
}
