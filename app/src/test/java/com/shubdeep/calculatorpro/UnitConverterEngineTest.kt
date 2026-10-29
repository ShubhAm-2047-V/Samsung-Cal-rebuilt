package com.shubdeep.calculatorpro

import com.shubdeep.calculatorpro.domain.converter.ConverterCategory
import com.shubdeep.calculatorpro.domain.converter.UnitConverterEngine
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class UnitConverterEngineTest {

    @Test
    fun testLengthConversions() {
        val lengthUnits = UnitConverterEngine.getUnitsForCategory(ConverterCategory.LENGTH)
        val km = lengthUnits.find { it.id == "km" }!!
        val m = lengthUnits.find { it.id == "m" }!!
        val cm = lengthUnits.find { it.id == "cm" }!!

        // 1 kilometer = 1,000 meters
        val metersFrom1Km = UnitConverterEngine.convertLinear(1.0, km, m)
        assertEquals(1000.0, metersFrom1Km, 1e-9)

        // 1 meter = 100 centimeters
        val cmFrom1M = UnitConverterEngine.convertLinear(1.0, m, cm)
        assertEquals(100.0, cmFrom1M, 1e-9)
    }

    @Test
    fun testMassConversions() {
        val massUnits = UnitConverterEngine.getUnitsForCategory(ConverterCategory.MASS)
        val kg = massUnits.find { it.id == "kg" }!!
        val g = massUnits.find { it.id == "g" }!!

        // 1 kilogram = 1,000 grams
        val gramsFrom1Kg = UnitConverterEngine.convertLinear(1.0, kg, g)
        assertEquals(1000.0, gramsFrom1Kg, 1e-9)
    }

    @Test
    fun testTemperatureAffineConversions() {
        // 0°C = 32°F
        val fFrom0C = UnitConverterEngine.convertTemperature(0.0, "c", "f")
        assertEquals(32.0, fFrom0C, 1e-9)

        // 100°C = 212°F
        val fFrom100C = UnitConverterEngine.convertTemperature(100.0, "c", "f")
        assertEquals(212.0, fFrom100C, 1e-9)

        // 0°C = 273.15 K
        val kFrom0C = UnitConverterEngine.convertTemperature(0.0, "c", "k")
        assertEquals(273.15, kFrom0C, 1e-9)

        // 32°F = 0°C
        val cFrom32F = UnitConverterEngine.convertTemperature(32.0, "f", "c")
        assertEquals(0.0, cFrom32F, 1e-9)
    }

    @Test
    fun testTimeConversions() {
        val timeUnits = UnitConverterEngine.getUnitsForCategory(ConverterCategory.TIME)
        val hour = timeUnits.find { it.id == "h" }!!
        val second = timeUnits.find { it.id == "s" }!!

        // 1 hour = 3,600 seconds
        val sFrom1H = UnitConverterEngine.convertLinear(1.0, hour, second)
        assertEquals(3600.0, sFrom1H, 1e-9)
    }

    @Test
    fun testDataStorageSiVsIec() {
        val storageUnits = UnitConverterEngine.getUnitsForCategory(ConverterCategory.DATA_STORAGE)
        val byte = storageUnits.find { it.id == "byte" }!!
        val kb = storageUnits.find { it.id == "kb" }!!
        val kib = storageUnits.find { it.id == "kib" }!!

        // 1 kB (Decimal SI) = 1,000 bytes
        val bytesFrom1Kb = UnitConverterEngine.convertLinear(1.0, kb, byte)
        assertEquals(1000.0, bytesFrom1Kb, 1e-9)

        // 1 KiB (Binary IEC) = 1,024 bytes
        val bytesFrom1Kib = UnitConverterEngine.convertLinear(1.0, kib, byte)
        assertEquals(1024.0, bytesFrom1Kib, 1e-9)
    }

    @Test
    fun testNumeralConversions() {
        // Decimal 42 to Binary = "101010"
        val bin = UnitConverterEngine.convertNumeral("42", 10, 2)
        assertEquals("101010", bin)

        // Decimal 42 to Hex = "2A"
        val hex = UnitConverterEngine.convertNumeral("42", 10, 16)
        assertEquals("2A", hex)

        // Hex "2A" to Decimal = "42"
        val dec = UnitConverterEngine.convertNumeral("2A", 16, 10)
        assertEquals("42", dec)
    }

    @Test
    fun testBmiCalculation() {
        // Height: 175 cm, Weight: 70 kg -> BMI = 70 / (1.75^2) = 22.857... -> 22.9 (Normal weight)
        val bmi = UnitConverterEngine.calculateBmi(175.0, 70.0)
        assertNotNull(bmi)
        assertEquals(22.9, bmi!!.bmi, 0.1)
        assertEquals("Normal weight", bmi.category)
    }

    @Test
    fun testAgeAndDateCalculations() {
        val dob = LocalDate.of(2000, 1, 1)
        val refDate = LocalDate.of(2025, 1, 1)
        val age = UnitConverterEngine.calculateAge(dob, refDate)
        assertNotNull(age)
        assertEquals(25, age!!.years)
        assertEquals(0, age.months)
        assertEquals(0, age.days)

        // Date Difference between Jan 1, 2024 and Jan 11, 2024 is 10 days
        val d1 = LocalDate.of(2024, 1, 1)
        val d2 = LocalDate.of(2024, 1, 11)
        val diff = UnitConverterEngine.calculateDateDifference(d1, d2)
        assertEquals(10L, diff.totalDays)
    }

    @Test
    fun testDiscountCalculation() {
        // Price ₹1,000 with 20% discount = Final ₹800, Saved ₹200
        val res = UnitConverterEngine.calculateDiscount(1000.0, 20.0)
        assertEquals(800.0, res.finalPrice, 1e-9)
        assertEquals(200.0, res.totalSaved, 1e-9)
    }
}
