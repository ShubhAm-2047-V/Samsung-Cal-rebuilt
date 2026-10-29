package com.shubdeep.calculatorpro.data.repository

import com.shubdeep.calculatorpro.data.local.CurrencyCacheDao
import com.shubdeep.calculatorpro.data.local.CurrencyRateEntity
import com.shubdeep.calculatorpro.data.remote.CurrencyApiService
import com.shubdeep.calculatorpro.data.remote.CurrencyInfo
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CurrencyRatesResult(
    val ratesAgainstUsd: Map<String, Double>,
    val baseCurrency: String = "USD",
    val lastUpdatedUtc: String,
    val fetchedAtEpochMs: Long,
    val isOffline: Boolean,
    val provider: String,
    val errorMessage: String? = null
)

class CurrencyRepository(
    private val apiService: CurrencyApiService,
    private val currencyDao: CurrencyCacheDao
) {

    // Currencies catalog with priority for INR and major trade currencies
    val supportedCurrencies: List<CurrencyInfo> = listOf(
        CurrencyInfo("INR", "Indian Rupee", "₹", "🇮🇳"),
        CurrencyInfo("USD", "US Dollar", "$", "🇺🇸"),
        CurrencyInfo("EUR", "Euro", "€", "🇪🇺"),
        CurrencyInfo("GBP", "British Pound", "£", "🇬🇧"),
        CurrencyInfo("AED", "UAE Dirham", "د.إ", "🇦🇪"),
        CurrencyInfo("SAR", "Saudi Riyal", "﷼", "🇸🇦"),
        CurrencyInfo("JPY", "Japanese Yen", "¥", "🇯🇵"),
        CurrencyInfo("CNY", "Chinese Yuan", "¥", "🇨🇳"),
        CurrencyInfo("CAD", "Canadian Dollar", "$", "🇨🇦"),
        CurrencyInfo("AUD", "Australian Dollar", "$", "🇦🇺"),
        CurrencyInfo("SGD", "Singapore Dollar", "$", "🇸🇬"),
        CurrencyInfo("CHF", "Swiss Franc", "CHF", "🇨🇭"),
        CurrencyInfo("NZD", "New Zealand Dollar", "$", "🇳🇿"),
        CurrencyInfo("HKD", "Hong Kong Dollar", "$", "🇭🇰"),
        CurrencyInfo("MYR", "Malaysian Ringgit", "RM", "🇲🇾"),
        CurrencyInfo("THB", "Thai Baht", "฿", "🇹🇭"),
        CurrencyInfo("IDR", "Indonesian Rupiah", "Rp", "🇮🇩"),
        CurrencyInfo("PKR", "Pakistani Rupee", "₨", "🇵🇰"),
        CurrencyInfo("BDT", "Bangladeshi Taka", "৳", "🇧🇩"),
        CurrencyInfo("NPR", "Nepalese Rupee", "रू", "🇳🇵"),
        CurrencyInfo("LKR", "Sri Lankan Rupee", "Rs", "🇱🇰"),
        CurrencyInfo("KRW", "South Korean Won", "₩", "🇰🇷"),
        CurrencyInfo("RUB", "Russian Ruble", "₽", "🇷🇺"),
        CurrencyInfo("ZAR", "South African Rand", "R", "🇿🇦"),
        CurrencyInfo("BRL", "Brazilian Real", "R$", "🇧🇷"),
        CurrencyInfo("MXN", "Mexican Peso", "$", "🇲🇽"),
        CurrencyInfo("TRY", "Turkish Lira", "₺", "🇹🇷"),
        CurrencyInfo("KWD", "Kuwaiti Dinar", "KD", "🇰🇼"),
        CurrencyInfo("BHD", "Bahraini Dinar", "BD", "🇧🇭"),
        CurrencyInfo("OMR", "Omani Rial", "RO", "🇴🇲"),
        CurrencyInfo("QAR", "Qatari Riyal", "QR", "🇶🇦"),
        CurrencyInfo("SEK", "Swedish Krona", "kr", "🇸🇪"),
        CurrencyInfo("NOK", "Norwegian Krone", "kr", "🇳🇴"),
        CurrencyInfo("DKK", "Danish Krone", "kr", "🇩🇰"),
        CurrencyInfo("PLN", "Polish Zloty", "zł", "🇵🇱"),
        CurrencyInfo("ILS", "Israeli Shekel", "₪", "🇮🇱"),
        CurrencyInfo("PHP", "Philippine Peso", "₱", "🇵🇭"),
        CurrencyInfo("VND", "Vietnamese Dong", "₫", "🇻🇳"),
        CurrencyInfo("EGP", "Egyptian Pound", "E£", "🇪🇬")
    )

    fun getCurrencyInfo(code: String): CurrencyInfo {
        return supportedCurrencies.find { it.code.equals(code, ignoreCase = true) }
            ?: CurrencyInfo(code.uppercase(), code.uppercase(), code.uppercase(), "🌐")
    }

    suspend fun getExchangeRates(forceRefresh: Boolean = false): CurrencyRatesResult {
        // If not forcing refresh, check local cache first if recent (e.g. within 2 hours)
        if (!forceRefresh) {
            val cached = currencyDao.getAllCachedRates()
            val lastFetch = currencyDao.getLastFetchedTime() ?: 0L
            val twoHoursMs = 2 * 60 * 60 * 1000L
            if (cached.isNotEmpty() && (System.currentTimeMillis() - lastFetch < twoHoursMs)) {
                return CurrencyRatesResult(
                    ratesAgainstUsd = cached.associate { it.currencyCode to it.rateAgainstUsd },
                    lastUpdatedUtc = cached.firstOrNull()?.lastUpdatedUtc ?: "Cached",
                    fetchedAtEpochMs = lastFetch,
                    isOffline = false,
                    provider = cached.firstOrNull()?.provider ?: "ExchangeRate-API"
                )
            }
        }

        // Try primary live API: ExchangeRate-API (open.er-api.com)
        try {
            val response = apiService.getLatestRates("USD")
            val rates = response.rates
            if (rates != null && rates.isNotEmpty()) {
                val updateUtc = response.timeLastUpdateUtc ?: SimpleDateFormat("yyyy-MM-dd HH:mm:ss 'UTC'", Locale.US).format(Date())
                val providerName = response.provider ?: "ExchangeRate-API"
                val nowEpoch = System.currentTimeMillis()

                val entities = rates.map { (code, rate) ->
                    CurrencyRateEntity(
                        currencyCode = code.uppercase(),
                        rateAgainstUsd = rate,
                        lastUpdatedUtc = updateUtc,
                        fetchedAtLocalEpochMs = nowEpoch,
                        provider = providerName
                    )
                }
                currencyDao.insertAll(entities)

                return CurrencyRatesResult(
                    ratesAgainstUsd = rates,
                    lastUpdatedUtc = updateUtc,
                    fetchedAtEpochMs = nowEpoch,
                    isOffline = false,
                    provider = providerName
                )
            }
        } catch (e: Exception) {
            // Log or fallback
        }

        // Try secondary fallback API: Frankfurter API
        try {
            val fallback = apiService.getFrankfurterRates()
            val rates = fallback.rates?.toMutableMap() ?: mutableMapOf()
            rates["USD"] = 1.0 // Base is USD
            if (rates.isNotEmpty()) {
                val updateUtc = fallback.date?.let { "$it 16:00:00 CET (ECB)" } ?: "ECB Reference Rates"
                val providerName = "European Central Bank (Frankfurter)"
                val nowEpoch = System.currentTimeMillis()

                val entities = rates.map { (code, rate) ->
                    CurrencyRateEntity(
                        currencyCode = code.uppercase(),
                        rateAgainstUsd = rate,
                        lastUpdatedUtc = updateUtc,
                        fetchedAtLocalEpochMs = nowEpoch,
                        provider = providerName
                    )
                }
                currencyDao.insertAll(entities)

                return CurrencyRatesResult(
                    ratesAgainstUsd = rates,
                    lastUpdatedUtc = updateUtc,
                    fetchedAtEpochMs = nowEpoch,
                    isOffline = false,
                    provider = providerName
                )
            }
        } catch (e: Exception) {
            // Secondary also failed
        }

        // Offline Fallback: Load from Room database cache
        val cached = currencyDao.getAllCachedRates()
        val lastFetch = currencyDao.getLastFetchedTime() ?: 0L
        if (cached.isNotEmpty()) {
            val cachedDateStr = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(Date(lastFetch))
            return CurrencyRatesResult(
                ratesAgainstUsd = cached.associate { it.currencyCode to it.rateAgainstUsd },
                lastUpdatedUtc = "${cached.first().lastUpdatedUtc} (Cached $cachedDateStr)",
                fetchedAtEpochMs = lastFetch,
                isOffline = true,
                provider = "${cached.first().provider} (Offline Cache)"
            )
        }

        // Complete failure (no network and no prior cache)
        return CurrencyRatesResult(
            ratesAgainstUsd = emptyMap(),
            lastUpdatedUtc = "No Data",
            fetchedAtEpochMs = 0L,
            isOffline = true,
            provider = "None",
            errorMessage = "Internet connection required to download currency rates for the first time."
        )
    }

    /**
     * Converts an amount from source to destination currency using cross-rate through base USD.
     * Converted amount = (amount / sourceRateAgainstUsd) * destinationRateAgainstUsd
     */
    fun convert(
        amount: BigDecimal,
        fromCurrency: String,
        toCurrency: String,
        ratesAgainstUsd: Map<String, Double>
    ): ConversionResult {
        if (fromCurrency.equals(toCurrency, ignoreCase = true)) {
            return ConversionResult(
                convertedAmount = amount,
                rate = BigDecimal.ONE,
                inverseRate = BigDecimal.ONE
            )
        }

        val fromRate = ratesAgainstUsd[fromCurrency.uppercase()]
            ?: return ConversionResult(isSuccess = false, errorMessage = "Rate not found for $fromCurrency")
        val toRate = ratesAgainstUsd[toCurrency.uppercase()]
            ?: return ConversionResult(isSuccess = false, errorMessage = "Rate not found for $toCurrency")

        if (fromRate <= 0.0 || toRate <= 0.0) {
            return ConversionResult(isSuccess = false, errorMessage = "Invalid exchange rate")
        }

        val fromRateBd = BigDecimal(fromRate.toString())
        val toRateBd = BigDecimal(toRate.toString())

        // crossRate = toRate / fromRate (1 fromCurrency in toCurrency)
        val crossRate = toRateBd.divide(fromRateBd, 8, RoundingMode.HALF_UP)
        // inverseRate = fromRate / toRate (1 toCurrency in fromCurrency)
        val inverseRate = fromRateBd.divide(toRateBd, 8, RoundingMode.HALF_UP)

        val converted = amount.multiply(crossRate).setScale(4, RoundingMode.HALF_UP)

        return ConversionResult(
            convertedAmount = converted,
            rate = crossRate,
            inverseRate = inverseRate,
            isSuccess = true
        )
    }

    suspend fun clearCache() {
        currencyDao.clearCache()
    }
}

data class ConversionResult(
    val convertedAmount: BigDecimal = BigDecimal.ZERO,
    val rate: BigDecimal = BigDecimal.ONE,
    val inverseRate: BigDecimal = BigDecimal.ONE,
    val isSuccess: Boolean = true,
    val errorMessage: String? = null
)
