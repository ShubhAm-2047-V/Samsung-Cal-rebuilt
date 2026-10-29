package com.shubdeep.calculatorpro.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class CalculationType {
    STANDARD,
    SCIENTIFIC,
    CURRENCY,
    UNIT,
    GST,
    EMI,
    PERCENTAGE,
    INTEREST,
    TIP
}

@Entity(tableName = "calculation_history")
data class CalculationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val expression: String,
    val result: String,
    val type: CalculationType,
    val timestamp: Long = System.currentTimeMillis(),
    val details: String? = null // e.g. for currency: "USD to INR @ 83.45 (Cached)"
)
