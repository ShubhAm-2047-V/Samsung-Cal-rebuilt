package com.shubdeep.calculatorpro.domain.calculator

import com.shubdeep.calculatorpro.data.local.NumberFormatPreference
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.abs

object NumberFormatterUtil {

    fun formatDouble(
        value: Double,
        formatPref: NumberFormatPreference = NumberFormatPreference.INDIAN,
        precisionPref: String = "AUTO"
    ): String {
        if (value.isNaN()) return "NaN"
        if (value.isInfinite()) return if (value > 0) "Infinity" else "-Infinity"
        if (value == 0.0) return "0"

        // For extremely large or extremely small numbers, use scientific notation
        val absVal = abs(value)
        if (absVal >= 1e14 || (absVal < 1e-7 && absVal > 0.0)) {
            val df = DecimalFormat("0.######E0", DecimalFormatSymbols(Locale.US))
            return df.format(value).replace("E", "e")
        }

        val bd = BigDecimal(value.toString())
        val precision = when (precisionPref) {
            "2" -> 2
            "4" -> 4
            "6" -> 6
            "8" -> 8
            else -> 10 // AUTO up to 10 decimal digits
        }

        val scaled = if (precisionPref != "AUTO") {
            bd.setScale(precision, RoundingMode.HALF_UP)
        } else {
            // Strip trailing zeroes
            bd.stripTrailingZeros()
        }

        val plainString = scaled.toPlainString()

        val parts = plainString.split(".")
        val integerPart = parts[0]
        val decimalPart = if (parts.size > 1) parts[1] else ""

        val formattedInt = if (formatPref == NumberFormatPreference.INDIAN) {
            formatIndianGrouping(integerPart)
        } else {
            formatInternationalGrouping(integerPart)
        }

        return if (decimalPart.isNotEmpty() && (precisionPref != "AUTO" || decimalPart != "0")) {
            "$formattedInt.$decimalPart"
        } else {
            formattedInt
        }
    }

    /**
     * Formats integer part with Indian grouping:
     * Last 3 digits grouped, then every 2 digits (e.g. 12,34,567)
     */
    private fun formatIndianGrouping(numStr: String): String {
        val isNegative = numStr.startsWith("-")
        val clean = if (isNegative) numStr.substring(1) else numStr

        if (clean.length <= 3) return numStr

        val last3 = clean.substring(clean.length - 3)
        var remaining = clean.substring(0, clean.length - 3)
        val chunks = mutableListOf<String>()

        while (remaining.length > 2) {
            chunks.add(0, remaining.substring(remaining.length - 2))
            remaining = remaining.substring(0, remaining.length - 2)
        }
        if (remaining.isNotEmpty()) {
            chunks.add(0, remaining)
        }

        val joined = chunks.joinToString(",") + "," + last3
        return if (isNegative) "-$joined" else joined
    }

    /**
     * Standard international grouping: every 3 digits (e.g. 1,234,567)
     */
    private fun formatInternationalGrouping(numStr: String): String {
        val isNegative = numStr.startsWith("-")
        val clean = if (isNegative) numStr.substring(1) else numStr

        if (clean.length <= 3) return numStr

        val sb = StringBuilder()
        var count = 0
        for (i in clean.length - 1 downTo 0) {
            sb.append(clean[i])
            count++
            if (count % 3 == 0 && i != 0) {
                sb.append(',')
            }
        }
        val reversed = sb.reverse().toString()
        return if (isNegative) "-$reversed" else reversed
    }
}
