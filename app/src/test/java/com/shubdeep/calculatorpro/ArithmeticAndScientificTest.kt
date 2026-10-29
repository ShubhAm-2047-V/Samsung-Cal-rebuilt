package com.shubdeep.calculatorpro

import com.shubdeep.calculatorpro.data.local.AngleMode
import com.shubdeep.calculatorpro.domain.calculator.EvalResult
import com.shubdeep.calculatorpro.domain.calculator.MathParser
import org.junit.Assert.*
import org.junit.Test
import kotlin.math.abs

class ArithmeticAndScientificTest {

    private val degParser = MathParser(AngleMode.DEG)
    private val radParser = MathParser(AngleMode.RAD)

    @Test
    fun testBasicArithmetic() {
        // 2 + 3 = 5
        val r1 = degParser.evaluate("2 + 3")
        assertTrue(r1 is EvalResult.Success)
        assertEquals(5.0, (r1 as EvalResult.Success).value, 1e-9)

        // 10 - 4 = 6
        val r2 = degParser.evaluate("10 - 4")
        assertTrue(r2 is EvalResult.Success)
        assertEquals(6.0, (r2 as EvalResult.Success).value, 1e-9)

        // 6 × 7 = 42
        val r3 = degParser.evaluate("6 * 7")
        assertTrue(r3 is EvalResult.Success)
        assertEquals(42.0, (r3 as EvalResult.Success).value, 1e-9)

        // 15 ÷ 3 = 5
        val r4 = degParser.evaluate("15 / 3")
        assertTrue(r4 is EvalResult.Success)
        assertEquals(5.0, (r4 as EvalResult.Success).value, 1e-9)
    }

    @Test
    fun testOperatorPrecedenceAndParentheses() {
        // 2 + 3 * 4 = 14 (not 20)
        val r1 = degParser.evaluate("2 + 3 * 4")
        assertTrue(r1 is EvalResult.Success)
        assertEquals(14.0, (r1 as EvalResult.Success).value, 1e-9)

        // (2 + 3) * 4 = 20
        val r2 = degParser.evaluate("(2 + 3) * 4")
        assertTrue(r2 is EvalResult.Success)
        assertEquals(20.0, (r2 as EvalResult.Success).value, 1e-9)

        // Nested parentheses: ((2 + 3) * (5 - 1)) / 2 = 10
        val r3 = degParser.evaluate("((2 + 3) * (5 - 1)) / 2")
        assertTrue(r3 is EvalResult.Success)
        assertEquals(10.0, (r3 as EvalResult.Success).value, 1e-9)
    }

    @Test
    fun testDivisionByZeroProducesSafeError() {
        val r = degParser.evaluate("10 / 0")
        assertTrue(r is EvalResult.Error)
        assertEquals("Cannot divide by zero", (r as EvalResult.Error).message)
    }

    @Test
    fun testTrigonometricDegreeMode() {
        // sin(90°) = 1.0
        val r1 = degParser.evaluate("sin(90)")
        assertTrue(r1 is EvalResult.Success)
        assertEquals(1.0, (r1 as EvalResult.Success).value, 1e-9)

        // cos(0°) = 1.0
        val r2 = degParser.evaluate("cos(0)")
        assertTrue(r2 is EvalResult.Success)
        assertEquals(1.0, (r2 as EvalResult.Success).value, 1e-9)

        // tan(45°) = 1.0
        val r3 = degParser.evaluate("tan(45)")
        assertTrue(r3 is EvalResult.Success)
        assertEquals(1.0, (r3 as EvalResult.Success).value, 1e-9)

        // sin(180°) = 0.0
        val r4 = degParser.evaluate("sin(180)")
        assertTrue(r4 is EvalResult.Success)
        assertEquals(0.0, (r4 as EvalResult.Success).value, 1e-9)
    }

    @Test
    fun testTrigonometricRadianMode() {
        // sin(π / 2) = 1.0 in radians
        val r1 = radParser.evaluate("sin(PI / 2)")
        assertTrue(r1 is EvalResult.Success)
        assertEquals(1.0, (r1 as EvalResult.Success).value, 1e-9)

        // cos(PI) = -1.0
        val r2 = radParser.evaluate("cos(PI)")
        assertTrue(r2 is EvalResult.Success)
        assertEquals(-1.0, (r2 as EvalResult.Success).value, 1e-9)
    }

    @Test
    fun testScientificFunctionsAndDomainErrors() {
        // sqrt(16) = 4
        val r1 = degParser.evaluate("sqrt(16)")
        assertTrue(r1 is EvalResult.Success)
        assertEquals(4.0, (r1 as EvalResult.Success).value, 1e-9)

        // sqrt(-4) -> Domain error
        val r2 = degParser.evaluate("sqrt(-4)")
        assertTrue(r2 is EvalResult.Error)

        // log10(100) = 2
        val r3 = degParser.evaluate("log(100)")
        assertTrue(r3 is EvalResult.Success)
        assertEquals(2.0, (r3 as EvalResult.Success).value, 1e-9)

        // log10(0) -> Domain error
        val r4 = degParser.evaluate("log(0)")
        assertTrue(r4 is EvalResult.Error)

        // 5! = 120
        val r5 = degParser.evaluate("5!")
        assertTrue(r5 is EvalResult.Success)
        assertEquals(120.0, (r5 as EvalResult.Success).value, 1e-9)

        // 2 ^ 10 = 1024
        val r6 = degParser.evaluate("2 ^ 10")
        assertTrue(r6 is EvalResult.Success)
        assertEquals(1024.0, (r6 as EvalResult.Success).value, 1e-9)
    }
}
