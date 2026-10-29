package com.shubdeep.calculatorpro.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currency_rates_cache")
data class CurrencyRateEntity(
    @PrimaryKey
    val currencyCode: String,
    val rateAgainstUsd: Double,
    val lastUpdatedUtc: String,
    val fetchedAtLocalEpochMs: Long = System.currentTimeMillis(),
    val provider: String = "ExchangeRate-API"
)
