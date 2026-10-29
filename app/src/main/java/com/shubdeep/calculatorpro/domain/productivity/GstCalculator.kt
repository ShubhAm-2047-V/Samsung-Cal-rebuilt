package com.shubdeep.calculatorpro.domain.productivity

import java.math.BigDecimal
import java.math.RoundingMode

enum class GstCalculationMode {
    ADD_GST,      // Base price + GST = Total Amount
    EXTRACT_GST   // Total GST-inclusive price -> Base price + GST
}

enum class TransactionType {
    INTRA_STATE,  // CGST + SGST (50% each)
    INTER_STATE   // IGST (100%)
}

data class GstResult(
    val basePrice: BigDecimal,
    val gstAmount: BigDecimal,
    val totalPrice: BigDecimal,
    val cgstAmount: BigDecimal,
    val sgstAmount: BigDecimal,
    val igstAmount: BigDecimal,
    val gstRatePercent: Double
)

object GstCalculator {

    val COMMON_GST_SLABS = listOf(0.0, 5.0, 12.0, 18.0, 28.0)

    fun calculate(
        inputAmount: BigDecimal,
        gstRatePercent: Double,
        mode: GstCalculationMode,
        transactionType: TransactionType = TransactionType.INTRA_STATE
    ): GstResult {
        if (inputAmount <= BigDecimal.ZERO || gstRatePercent < 0.0) {
            return GstResult(
                basePrice = BigDecimal.ZERO,
                gstAmount = BigDecimal.ZERO,
                totalPrice = BigDecimal.ZERO,
                cgstAmount = BigDecimal.ZERO,
                sgstAmount = BigDecimal.ZERO,
                igstAmount = BigDecimal.ZERO,
                gstRatePercent = gstRatePercent
            )
        }

        val rateDecimal = BigDecimal(gstRatePercent.toString()).divide(BigDecimal("100"), 6, RoundingMode.HALF_UP)

        val (basePrice, gstAmount, totalPrice) = when (mode) {
            GstCalculationMode.ADD_GST -> {
                val gst = inputAmount.multiply(rateDecimal).setScale(2, RoundingMode.HALF_UP)
                val total = inputAmount.add(gst)
                Triple(inputAmount.setScale(2, RoundingMode.HALF_UP), gst, total)
            }
            GstCalculationMode.EXTRACT_GST -> {
                // Base = Total / (1 + Rate)
                val divisor = BigDecimal.ONE.add(rateDecimal)
                val base = inputAmount.divide(divisor, 4, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP)
                val gst = inputAmount.subtract(base).setScale(2, RoundingMode.HALF_UP)
                Triple(base, gst, inputAmount.setScale(2, RoundingMode.HALF_UP))
            }
        }

        val (cgst, sgst, igst) = if (transactionType == TransactionType.INTRA_STATE) {
            val halfGst = gstAmount.divide(BigDecimal("2"), 2, RoundingMode.HALF_UP)
            Triple(halfGst, halfGst, BigDecimal.ZERO)
        } else {
            Triple(BigDecimal.ZERO, BigDecimal.ZERO, gstAmount)
        }

        return GstResult(
            basePrice = basePrice,
            gstAmount = gstAmount,
            totalPrice = totalPrice,
            cgstAmount = cgst,
            sgstAmount = sgst,
            igstAmount = igst,
            gstRatePercent = gstRatePercent
        )
    }
}
