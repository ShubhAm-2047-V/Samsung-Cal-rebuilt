package com.shubdeep.calculatorpro.domain.calculator

import com.shubdeep.calculatorpro.data.local.AngleMode
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import kotlin.math.*

sealed class EvalResult {
    data class Success(val value: Double, val formatted: String) : EvalResult()
    data class Error(val message: String) : EvalResult()
}

class MathParser(private val angleMode: AngleMode = AngleMode.DEG) {

    fun evaluate(expression: String): EvalResult {
        if (expression.isBlank()) return EvalResult.Error("Empty expression")

        try {
            val sanitized = sanitize(expression)
            val tokens = tokenize(sanitized)
            if (tokens.isEmpty()) return EvalResult.Error("Empty expression")

            val parser = TokenParser(tokens, angleMode)
            val result = parser.parseExpression()
            if (parser.hasNext()) {
                return EvalResult.Error("Unexpected character: ${parser.peek()}")
            }

            if (result.isNaN()) {
                return EvalResult.Error("Result is undefined")
            }
            if (result.isInfinite()) {
                return EvalResult.Error("Cannot divide by zero")
            }

            val formatted = NumberFormatterUtil.formatDouble(result)
            return EvalResult.Success(result, formatted)
        } catch (e: ArithmeticException) {
            return EvalResult.Error(e.message ?: "Math error")
        } catch (e: IllegalArgumentException) {
            return EvalResult.Error(e.message ?: "Invalid syntax")
        } catch (e: Exception) {
            return EvalResult.Error(e.message ?: "Syntax error")
        }
    }

    private fun sanitize(expr: String): String {
        return expr
            .replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")
            .replace("π", "PI")
            .replace("√", "sqrt")
            .replace("∛", "cbrt")
            .replace(" ", "")
    }

    private fun tokenize(expr: String): List<String> {
        val tokens = mutableListOf<String>()
        var i = 0
        while (i < expr.length) {
            val c = expr[i]
            when {
                c.isDigit() || c == '.' -> {
                    val sb = StringBuilder()
                    while (i < expr.length && (expr[i].isDigit() || expr[i] == '.' || expr[i] == 'E' || expr[i] == 'e')) {
                        // Handle scientific notation e.g. 1e-5 or 2e+3
                        if ((expr[i] == 'E' || expr[i] == 'e') && i + 1 < expr.length && (expr[i + 1] == '+' || expr[i + 1] == '-')) {
                            sb.append(expr[i])
                            i++
                            sb.append(expr[i])
                            i++
                        } else {
                            sb.append(expr[i])
                            i++
                        }
                    }
                    tokens.add(sb.toString())
                }
                c.isLetter() -> {
                    val sb = StringBuilder()
                    while (i < expr.length && expr[i].isLetter()) {
                        sb.append(expr[i])
                        i++
                    }
                    tokens.add(sb.toString())
                }
                c in "+-*/^()%!," -> {
                    tokens.add(c.toString())
                    i++
                }
                else -> {
                    i++
                }
            }
        }
        return tokens
    }

    private class TokenParser(
        private val tokens: List<String>,
        private val angleMode: AngleMode
    ) {
        private var pos = 0

        fun hasNext(): Boolean = pos < tokens.size
        fun peek(): String = if (pos < tokens.size) tokens[pos] else ""
        private fun consume(): String = tokens[pos++]

        fun parseExpression(): Double {
            return parseAddSubtract()
        }

        // Add & Subtract (+, -)
        private fun parseAddSubtract(): Double {
            var value = parseMultiplyDivide()
            while (hasNext()) {
                val op = peek()
                if (op == "+" || op == "-") {
                    consume()
                    val right = parseMultiplyDivide()
                    value = if (op == "+") value + right else value - right
                } else {
                    break
                }
            }
            return value
        }

        // Multiply, Divide, Modulo/Percentage (*, /, %)
        private fun parseMultiplyDivide(): Double {
            var value = parsePower()
            while (hasNext()) {
                val op = peek()
                if (op == "*" || op == "/" || op == "%") {
                    consume()
                    val right = parsePower()
                    value = when (op) {
                        "*" -> value * right
                        "/" -> {
                            if (right == 0.0) throw ArithmeticException("Cannot divide by zero")
                            value / right
                        }
                        "%" -> value * (right / 100.0)
                        else -> value
                    }
                } else {
                    break
                }
            }
            return value
        }

        // Exponent (^)
        private fun parsePower(): Double {
            var value = parseUnary()
            while (hasNext() && peek() == "^") {
                consume()
                val exponent = parseUnary()
                value = value.pow(exponent)
            }
            return value
        }

        // Unary (+, -) & Postfix Factorial (!)
        private fun parseUnary(): Double {
            if (hasNext()) {
                val op = peek()
                if (op == "-") {
                    consume()
                    return -parseUnary()
                } else if (op == "+") {
                    consume()
                    return parseUnary()
                }
            }
            var value = parsePrimary()

            // Check for postfix factorial or percentage
            while (hasNext()) {
                val op = peek()
                if (op == "!") {
                    consume()
                    value = factorial(value)
                } else if (op == "%") {
                    consume()
                    value /= 100.0
                } else {
                    break
                }
            }
            return value
        }

        // Primary: numbers, constants, functions, parentheses
        private fun parsePrimary(): Double {
            if (!hasNext()) throw IllegalArgumentException("Unexpected end of expression")
            val token = consume()

            // Parentheses
            if (token == "(") {
                val expr = parseExpression()
                if (hasNext() && peek() == ")") {
                    consume()
                }
                return expr
            }

            // Constants
            if (token.equals("PI", ignoreCase = true)) return Math.PI
            if (token.equals("e", ignoreCase = true) || token.equals("E", ignoreCase = true)) return Math.E

            // Functions
            when (token.lowercase()) {
                "sin" -> return applyTrig(parsePrimary(), false) { sin(it) }
                "cos" -> return applyTrig(parsePrimary(), false) { cos(it) }
                "tan" -> {
                    val arg = parsePrimary()
                    val rad = if (angleMode == AngleMode.DEG) Math.toRadians(arg) else arg
                    if (abs(cos(rad)) < 1e-15) throw ArithmeticException("tan(${arg}°) is undefined")
                    return tan(rad)
                }
                "asin" -> {
                    val arg = parsePrimary()
                    if (arg < -1.0 || arg > 1.0) throw ArithmeticException("Domain error: asin input must be in [-1, 1]")
                    val rad = asin(arg)
                    return if (angleMode == AngleMode.DEG) Math.toDegrees(rad) else rad
                }
                "acos" -> {
                    val arg = parsePrimary()
                    if (arg < -1.0 || arg > 1.0) throw ArithmeticException("Domain error: acos input must be in [-1, 1]")
                    val rad = acos(arg)
                    return if (angleMode == AngleMode.DEG) Math.toDegrees(rad) else rad
                }
                "atan" -> {
                    val rad = atan(parsePrimary())
                    return if (angleMode == AngleMode.DEG) Math.toDegrees(rad) else rad
                }
                "sinh" -> return sinh(parsePrimary())
                "cosh" -> return cosh(parsePrimary())
                "tanh" -> return tanh(parsePrimary())
                "ln" -> {
                    val arg = parsePrimary()
                    if (arg <= 0) throw ArithmeticException("Domain error: ln requires positive value")
                    return ln(arg)
                }
                "log" -> {
                    val arg = parsePrimary()
                    if (arg <= 0) throw ArithmeticException("Domain error: log requires positive value")
                    return log10(arg)
                }
                "sqrt" -> {
                    val arg = parsePrimary()
                    if (arg < 0) throw ArithmeticException("Domain error: sqrt of negative number")
                    return sqrt(arg)
                }
                "cbrt" -> return cbrt(parsePrimary())
                "abs" -> return abs(parsePrimary())
                "fact" -> return factorial(parsePrimary())
                "recip" -> {
                    val arg = parsePrimary()
                    if (arg == 0.0) throw ArithmeticException("Cannot divide by zero")
                    return 1.0 / arg
                }
            }

            // Numeric Literal
            try {
                return token.toDouble()
            } catch (e: NumberFormatException) {
                throw IllegalArgumentException("Invalid number: $token")
            }
        }

        private fun applyTrig(arg: Double, isInverse: Boolean, func: (Double) -> Double): Double {
            val rad = if (angleMode == AngleMode.DEG) Math.toRadians(arg) else arg
            val res = func(rad)
            // Round nearly-zero results (e.g. sin(180°) is 0, not 1.22e-16)
            return if (abs(res) < 1e-14) 0.0 else res
        }

        private fun factorial(n: Double): Double {
            if (n < 0 || n != floor(n)) throw ArithmeticException("Factorial is only defined for non-negative integers")
            if (n > 170) throw ArithmeticException("Overflow: factorial > 170!")
            var result = 1.0
            val intN = n.toLong()
            for (i in 2..intN) {
                result *= i
            }
            return result
        }
    }
}
