package com.shubdeep.calculatorpro.data.remote

import com.google.gson.annotations.SerializedName

data class ExchangeRateApiResponse(
    @SerializedName("result") val result: String?,
    @SerializedName("provider") val provider: String?,
    @SerializedName("base_code") val baseCode: String?,
    @SerializedName("time_last_update_utc") val timeLastUpdateUtc: String?,
    @SerializedName("time_next_update_utc") val timeNextUpdateUtc: String?,
    @SerializedName("rates") val rates: Map<String, Double>?
)

data class FrankfurterApiResponse(
    @SerializedName("amount") val amount: Double?,
    @SerializedName("base") val base: String?,
    @SerializedName("date") val date: String?,
    @SerializedName("rates") val rates: Map<String, Double>?
)

data class CurrencyInfo(
    val code: String,
    val name: String,
    val symbol: String,
    val flagEmoji: String
)
