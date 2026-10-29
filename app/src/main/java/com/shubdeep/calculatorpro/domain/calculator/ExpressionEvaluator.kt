package com.shubdeep.calculatorpro.domain.calculator

import com.shubdeep.calculatorpro.data.local.AngleMode
import com.shubdeep.calculatorpro.data.local.NumberFormatPreference

data class CalculatorState(
    val expression: String = "",
    val liveResult: String = "",
    val error: String? = null,
    val isEvaluated: Boolean = false,
    val angleMode: AngleMode = AngleMode.DEG,
    val isScientificExpanded: Boolean = false
)

class ExpressionEvaluator(
    private var angleMode: AngleMode = AngleMode.DEG,
    private var numberFormat: NumberFormatPreference = NumberFormatPreference.INDIAN,
    private var precision: String = "AUTO"
) {

    private var mathParser = MathParser(angleMode)

    fun updatePreferences(
        angleMode: AngleMode,
        numberFormat: NumberFormatPreference,
        precision: String
    ) {
        this.angleMode = angleMode
        this.numberFormat = numberFormat
        this.precision = precision
        this.mathParser = MathParser(angleMode)
    }

    fun onDigit(state: CalculatorState, digit: String): CalculatorState {
        val newExpr = if (state.isEvaluated) digit else state.expression + digit
        val live = computeLiveResult(newExpr)
        return state.copy(
            expression = newExpr,
            liveResult = live,
            error = null,
            isEvaluated = false
        )
    }

    fun onDecimal(state: CalculatorState): CalculatorState {
        if (state.isEvaluated) {
            return state.copy(expression = "0.", liveResult = "", isEvaluated = false, error = null)
        }
        val expr = state.expression
        if (expr.isEmpty()) {
            return state.copy(expression = "0.", liveResult = "", isEvaluated = false, error = null)
        }

        // Check if the current trailing number already has a decimal point
        val lastNumber = expr.takeLastWhile { it.isDigit() || it == '.' }
        if (lastNumber.contains(".")) {
            return state // Ignore repeated decimal
        }

        val lastChar = expr.last()
        val newExpr = if (lastChar.isDigit()) {
            expr + "."
        } else {
            expr + "0."
        }

        return state.copy(
            expression = newExpr,
            liveResult = computeLiveResult(newExpr),
            error = null,
            isEvaluated = false
        )
    }

    fun onOperator(state: CalculatorState, operator: String): CalculatorState {
        var expr = state.expression

        if (state.isEvaluated && state.liveResult.isNotEmpty() && state.error == null) {
            // Chain calculation starting from previous result
            expr = state.liveResult.replace(",", "")
        }

        if (expr.isEmpty()) {
            if (operator == "−" || operator == "-") {
                return state.copy(expression = "−", liveResult = "", isEvaluated = false, error = null)
            }
            return state
        }

        val lastChar = expr.last().toString()
        val operators = listOf("+", "−", "-", "×", "*", "÷", "/", "^")

        val newExpr = if (operators.contains(lastChar)) {
            // Replace trailing operator
            expr.dropLast(1) + operator
        } else {
            expr + operator
        }

        return state.copy(
            expression = newExpr,
            liveResult = computeLiveResult(newExpr),
            error = null,
            isEvaluated = false
        )
    }

    fun onParenthesis(state: CalculatorState): CalculatorState {
        val expr = state.expression
        if (expr.isEmpty() || state.isEvaluated) {
            return state.copy(expression = "(", liveResult = "", isEvaluated = false, error = null)
        }

        val openCount = expr.count { it == '(' }
        val closeCount = expr.count { it == ')' }
        val lastChar = expr.last()

        val nextChar = if (openCount > closeCount && (lastChar.isDigit() || lastChar == ')')) {
            ")"
        } else if (lastChar.isDigit() || lastChar == ')') {
            "×("
        } else {
            "("
        }

        val newExpr = expr + nextChar
        return state.copy(
            expression = newExpr,
            liveResult = computeLiveResult(newExpr),
            error = null,
            isEvaluated = false
        )
    }

    fun onFunction(state: CalculatorState, functionName: String): CalculatorState {
        val prefix = if (state.isEvaluated) "" else state.expression
        val lastChar = prefix.lastOrNull()
        val separator = if (lastChar != null && (lastChar.isDigit() || lastChar == ')')) "×" else ""
        val newExpr = "$prefix$separator$functionName("
        return state.copy(
            expression = newExpr,
            liveResult = "",
            error = null,
            isEvaluated = false
        )
    }

    fun onConstant(state: CalculatorState, constant: String): CalculatorState {
        val prefix = if (state.isEvaluated) "" else state.expression
        val lastChar = prefix.lastOrNull()
        val separator = if (lastChar != null && (lastChar.isDigit() || lastChar == ')')) "×" else ""
        val newExpr = "$prefix$separator$constant"
        return state.copy(
            expression = newExpr,
            liveResult = computeLiveResult(newExpr),
            error = null,
            isEvaluated = false
        )
    }

    fun onToggleSign(state: CalculatorState): CalculatorState {
        val expr = state.expression
        if (expr.isEmpty()) return state

        // If simple single number, toggle negative sign
        return if (!expr.contains(Regex("[+×÷^]")) && !expr.contains("−") && !expr.contains("-")) {
            state.copy(expression = "−$expr", liveResult = computeLiveResult("−$expr"))
        } else if (expr.startsWith("−") && !expr.drop(1).contains(Regex("[+×÷^−-]"))) {
            state.copy(expression = expr.drop(1), liveResult = computeLiveResult(expr.drop(1)))
        } else {
            // Surround with -( ... )
            val newExpr = "−($expr)"
            state.copy(expression = newExpr, liveResult = computeLiveResult(newExpr))
        }
    }

    fun onBackspace(state: CalculatorState): CalculatorState {
        if (state.isEvaluated) {
            return state.copy(expression = "", liveResult = "", isEvaluated = false, error = null)
        }
        if (state.expression.isEmpty()) return state

        // Check for function names like "sin(", "cos(", "sqrt("
        var newExpr = state.expression
        val functions = listOf("sinh(", "cosh(", "tanh(", "asin(", "acos(", "atan(", "sqrt(", "cbrt(", "sin(", "cos(", "tan(", "log(", "ln(", "abs(")
        var matchedFunc: String? = null
        for (func in functions) {
            if (newExpr.endsWith(func)) {
                matchedFunc = func
                break
            }
        }

        newExpr = if (matchedFunc != null) {
            newExpr.dropLast(matchedFunc.length)
        } else {
            newExpr.dropLast(1)
        }

        return state.copy(
            expression = newExpr,
            liveResult = computeLiveResult(newExpr),
            error = null,
            isEvaluated = false
        )
    }

    fun onClear(state: CalculatorState): CalculatorState {
        return state.copy(
            expression = "",
            liveResult = "",
            error = null,
            isEvaluated = false
        )
    }

    fun onEquals(state: CalculatorState): Pair<CalculatorState, Boolean> {
        val expr = state.expression
        if (expr.isEmpty()) return Pair(state, false)

        // Balance open parentheses
        val openCount = expr.count { it == '(' }
        val closeCount = expr.count { it == ')' }
        val balanced = if (openCount > closeCount) {
            expr + ")".repeat(openCount - closeCount)
        } else {
            expr
        }

        return when (val res = mathParser.evaluate(balanced)) {
            is EvalResult.Success -> {
                val formatted = NumberFormatterUtil.formatDouble(res.value, numberFormat, precision)
                Pair(
                    state.copy(
                        expression = balanced,
                        liveResult = formatted,
                        error = null,
                        isEvaluated = true
                    ),
                    true // record to history
                )
            }
            is EvalResult.Error -> {
                Pair(
                    state.copy(
                        expression = balanced,
                        error = res.message,
                        isEvaluated = true
                    ),
                    false
                )
            }
        }
    }

    private fun computeLiveResult(expr: String): String {
        if (expr.isEmpty()) return ""
        // Auto-close any unclosed parentheses for live preview
        val openCount = expr.count { it == '(' }
        val closeCount = expr.count { it == ')' }
        val previewExpr = if (openCount > closeCount) {
            expr + ")".repeat(openCount - closeCount)
        } else {
            expr
        }

        return when (val res = mathParser.evaluate(previewExpr)) {
            is EvalResult.Success -> NumberFormatterUtil.formatDouble(res.value, numberFormat, precision)
            is EvalResult.Error -> ""
        }
    }
}
