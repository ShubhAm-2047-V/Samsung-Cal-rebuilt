package com.shubdeep.calculatorpro.domain.productivity

import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.pow

data class EmiResult(
    val monthlyEmi: BigDecimal,
    val totalInterest: BigDecimal,
    val totalPayment: BigDecimal,
    val principal: BigDecimal,
    val annualRatePercent: Double,
    val tenureMonths: Int
)

object EmiCalculator {

    fun calculate(
        principal: Double,
        annualRatePercent: Double,
        tenureMonths: Int
    ): EmiResult {
        if (principal <= 0.0 || tenureMonths <= 0) {
            return EmiResult(
                monthlyEmi = BigDecimal.ZERO,
                totalInterest = BigDecimal.ZERO,
                totalPayment = BigDecimal.ZERO,
                principal = BigDecimal.ZERO,
                annualRatePercent = annualRatePercent,
                tenureMonths = tenureMonths
            )
        }

        // Handle 0% interest loan
        if (annualRatePercent <= 0.0) {
            val emi = BigDecimal(principal).divide(BigDecimal(tenureMonths), 2, RoundingMode.HALF_UP)
            return EmiResult(
                monthlyEmi = emi,
                totalInterest = BigDecimal.ZERO,
                totalPayment = BigDecimal(principal).setScale(2, RoundingMode.HALF_UP),
                principal = BigDecimal(principal).setScale(2, RoundingMode.HALF_UP),
                annualRatePercent = 0.0,
                tenureMonths = tenureMonths
            )
        }

        val monthlyRate = (annualRatePercent / 12.0) / 100.0
        val numerator = principal * monthlyRate * (1.0 + monthlyRate).pow(tenureMonths.toDouble())
        val denominator = (1.0 + monthlyRate).pow(tenureMonths.toDouble()) - 1.0

        val emiDouble = numerator / denominator
        val monthlyEmi = BigDecimal(emiDouble).setScale(2, RoundingMode.HALF_UP)
        val totalPayment = monthlyEmi.multiply(BigDecimal(tenureMonths)).setScale(2, RoundingMode.HALF_UP)
        val totalInterest = totalPayment.subtract(BigDecimal(principal).setScale(2, RoundingMode.HALF_UP))

        return EmiResult(
            monthlyEmi = monthlyEmi,
            totalInterest = totalInterest,
            totalPayment = totalPayment,
            principal = BigDecimal(principal).setScale(2, RoundingMode.HALF_UP),
            annualRatePercent = annualRatePercent,
            tenureMonths = tenureMonths
        )
    }
}
