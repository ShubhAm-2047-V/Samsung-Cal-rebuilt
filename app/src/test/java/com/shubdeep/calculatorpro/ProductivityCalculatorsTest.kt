package com.shubdeep.calculatorpro

import com.shubdeep.calculatorpro.domain.productivity.*
import org.junit.Assert.*
import org.junit.Test
import java.math.BigDecimal

class ProductivityCalculatorsTest {

    @Test
    fun testIndianGstAddMode() {
        // Base Price ₹ 10,000 at 18% GST (Intra-state)
        val res = GstCalculator.calculate(
            inputAmount = BigDecimal("10000"),
            gstRatePercent = 18.0,
            mode = GstCalculationMode.ADD_GST,
            transactionType = TransactionType.INTRA_STATE
        )

        assertEquals(BigDecimal("10000.00"), res.basePrice)
        assertEquals(BigDecimal("1800.00"), res.gstAmount)
        assertEquals(BigDecimal("11800.00"), res.totalPrice)
        assertEquals(BigDecimal("900.00"), res.cgstAmount)
        assertEquals(BigDecimal("900.00"), res.sgstAmount)
        assertEquals(BigDecimal.ZERO, res.igstAmount)
    }

    @Test
    fun testIndianGstExtractMode() {
        // GST-inclusive price ₹ 11,800 at 18% GST (Inter-state)
        val res = GstCalculator.calculate(
            inputAmount = BigDecimal("11800"),
            gstRatePercent = 18.0,
            mode = GstCalculationMode.EXTRACT_GST,
            transactionType = TransactionType.INTER_STATE
        )

        assertEquals(BigDecimal("10000.00"), res.basePrice)
        assertEquals(BigDecimal("1800.00"), res.gstAmount)
        assertEquals(BigDecimal("11800.00"), res.totalPrice)
        assertEquals(BigDecimal.ZERO, res.cgstAmount)
        assertEquals(BigDecimal.ZERO, res.sgstAmount)
        assertEquals(BigDecimal("1800.00"), res.igstAmount)
    }

    @Test
    fun testLoanEmiCalculation() {
        // Principal ₹ 1,000,000 (10 Lakhs) at 8.5% for 20 years (240 months)
        val res = EmiCalculator.calculate(
            principal = 1000000.0,
            annualRatePercent = 8.5,
            tenureMonths = 240
        )

        // Expected monthly EMI ~ ₹ 8678.23
        assertEquals(BigDecimal("8678.23"), res.monthlyEmi)
        // Total repayment = 8678.23 * 240 = 2082775.20
        assertEquals(BigDecimal("2082775.20"), res.totalPayment)
        // Total interest = 2082775.20 - 1000000 = 1082775.20
        assertEquals(BigDecimal("1082775.20"), res.totalInterest)
    }

    @Test
    fun testZeroInterestLoanEmi() {
        // ₹ 12,000 loan at 0% for 12 months -> EMI ₹ 1,000, Total Interest ₹ 0
        val res = EmiCalculator.calculate(
            principal = 12000.0,
            annualRatePercent = 0.0,
            tenureMonths = 12
        )

        assertEquals(BigDecimal("1000.00"), res.monthlyEmi)
        assertEquals(BigDecimal.ZERO, res.totalInterest)
        assertEquals(BigDecimal("12000.00"), res.totalPayment)
    }

    @Test
    fun testPercentageAndTipCalculations() {
        // 25% of 800 = 200
        val p1 = PercentageCalculator.percentOf(25.0, 800.0)
        assertEquals(200.0, p1, 1e-9)

        // 50 is 25% of 200
        val p2 = PercentageCalculator.isWhatPercentOf(50.0, 200.0)
        assertEquals(25.0, p2, 1e-9)

        // Bill ₹ 1,200 with 10% tip for 4 people -> Tip ₹ 120, Total ₹ 1,320, Per person ₹ 330
        val tipRes = TipCalculator.calculate(1200.0, 10.0, 4)
        assertEquals(120.0, tipRes.tipAmount, 1e-9)
        assertEquals(1320.0, tipRes.totalBill, 1e-9)
        assertEquals(330.0, tipRes.perPersonAmount, 1e-9)
    }
}
