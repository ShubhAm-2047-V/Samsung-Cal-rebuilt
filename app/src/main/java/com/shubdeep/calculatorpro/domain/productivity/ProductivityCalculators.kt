package com.shubdeep.calculatorpro.domain.productivity

import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.pow

object PercentageCalculator {

    fun percentOf(percent: Double, total: Double): Double {
        return (percent / 100.0) * total
    }

    fun isWhatPercentOf(part: Double, total: Double): Double {
        if (total == 0.0) return 0.0
        return (part / total) * 100.0
    }

    fun percentChange(oldValue: Double, newValue: Double): Double {
        if (oldValue == 0.0) return 0.0
        return ((newValue - oldValue) / oldValue) * 100.0
    }
}

object TipCalculator {

    data class TipResult(
        val tipAmount: Double,
        val totalBill: Double,
        val perPersonAmount: Double
    )

    fun calculate(
        billAmount: Double,
        tipPercent: Double,
        peopleCount: Int
    ): TipResult {
        if (billAmount <= 0.0 || peopleCount <= 0) {
            return TipResult(0.0, 0.0, 0.0)
        }
        val tip = billAmount * (tipPercent / 100.0)
        val total = billAmount + tip
        val perPerson = total / peopleCount
        return TipResult(
            tipAmount = BigDecimal(tip).setScale(2, RoundingMode.HALF_UP).toDouble(),
            totalBill = BigDecimal(total).setScale(2, RoundingMode.HALF_UP).toDouble(),
            perPersonAmount = BigDecimal(perPerson).setScale(2, RoundingMode.HALF_UP).toDouble()
        )
    }
}

object InterestCalculator {

    data class SimpleInterestResult(
        val interestEarned: Double,
        val totalAmount: Double
    )

    data class CompoundInterestResult(
        val interestEarned: Double,
        val totalAmount: Double
    )

    fun calculateSimple(principal: Double, annualRatePercent: Double, years: Double): SimpleInterestResult {
        if (principal <= 0.0 || years <= 0.0) return SimpleInterestResult(0.0, 0.0)
        val interest = (principal * annualRatePercent * years) / 100.0
        val total = principal + interest
        return SimpleInterestResult(
            interestEarned = BigDecimal(interest).setScale(2, RoundingMode.HALF_UP).toDouble(),
            totalAmount = BigDecimal(total).setScale(2, RoundingMode.HALF_UP).toDouble()
        )
    }

    fun calculateCompound(
        principal: Double,
        annualRatePercent: Double,
        years: Double,
        compoundsPerYear: Int = 12 // Monthly default
    ): CompoundInterestResult {
        if (principal <= 0.0 || years <= 0.0 || compoundsPerYear <= 0) return CompoundInterestResult(0.0, 0.0)
        val r = (annualRatePercent / 100.0) / compoundsPerYear
        val n = compoundsPerYear * years
        val total = principal * (1.0 + r).pow(n)
        val interest = total - principal
        return CompoundInterestResult(
            interestEarned = BigDecimal(interest).setScale(2, RoundingMode.HALF_UP).toDouble(),
            totalAmount = BigDecimal(total).setScale(2, RoundingMode.HALF_UP).toDouble()
        )
    }
}

data class ScientificConstant(
    val name: String,
    val symbol: String,
    val value: String,
    val unit: String,
    val numericValue: Double
)

object ScientificConstantsCatalog {
    val CONSTANTS = listOf(
        ScientificConstant("Speed of light in vacuum", "c", "299792458", "m/s", 299792458.0),
        ScientificConstant("Planck constant", "h", "6.62607015e-34", "J·s", 6.62607015e-34),
        ScientificConstant("Reduced Planck constant", "ℏ", "1.054571817e-34", "J·s", 1.054571817e-34),
        ScientificConstant("Gravitational constant", "G", "6.67430e-11", "m³/(kg·s²)", 6.67430e-11),
        ScientificConstant("Standard acceleration of gravity", "g", "9.80665", "m/s²", 9.80665),
        ScientificConstant("Elementary charge", "e", "1.602176634e-19", "C", 1.602176634e-19),
        ScientificConstant("Avogadro constant", "N_A", "6.02214076e23", "mol⁻¹", 6.02214076e23),
        ScientificConstant("Boltzmann constant", "k_B", "1.380649e-23", "J/K", 1.380649e-23),
        ScientificConstant("Ideal gas constant", "R", "8.314462618", "J/(mol·K)", 8.314462618),
        ScientificConstant("Electron mass", "m_e", "9.1093837015e-31", "kg", 9.1093837015e-31),
        ScientificConstant("Proton mass", "m_p", "1.67262192369e-27", "kg", 1.67262192369e-27),
        ScientificConstant("Neutron mass", "m_n", "1.67492749804e-27", "kg", 1.67492749804e-27),
        ScientificConstant("Permittivity of vacuum", "ε_0", "8.8541878128e-12", "F/m", 8.8541878128e-12),
        ScientificConstant("Permeability of vacuum", "μ_0", "1.25663706212e-6", "N/A²", 1.25663706212e-6),
        ScientificConstant("Stefan-Boltzmann constant", "σ", "5.670374419e-8", "W/(m²·K⁴)", 5.670374419e-8),
        ScientificConstant("Rydberg constant", "R_∞", "10973731.568160", "m⁻¹", 10973731.56816)
    )
}
