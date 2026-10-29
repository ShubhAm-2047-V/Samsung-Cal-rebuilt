package com.shubdeep.calculatorpro

import com.shubdeep.calculatorpro.data.local.CurrencyCacheDao
import com.shubdeep.calculatorpro.data.local.CurrencyRateEntity
import com.shubdeep.calculatorpro.data.remote.CurrencyApiService
import com.shubdeep.calculatorpro.data.remote.ExchangeRateApiResponse
import com.shubdeep.calculatorpro.data.remote.FrankfurterApiResponse
import com.shubdeep.calculatorpro.data.repository.CurrencyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import java.math.BigDecimal
import java.math.RoundingMode

class CurrencyConversionTest {

    // Deterministic test rates against base USD
    private val testRates = mapOf(
        "USD" to 1.0,
        "INR" to 83.0,
        "EUR" to 0.83,
        "AED" to 3.67
    )

    private val fakeDao = object : CurrencyCacheDao {
        val storage = mutableListOf<CurrencyRateEntity>()

        override suspend fun getAllCachedRates(): List<CurrencyRateEntity> = storage
        override fun observeAllCachedRates(): Flow<List<CurrencyRateEntity>> = flowOf(storage)
        override suspend fun getRateByCode(code: String): CurrencyRateEntity? = storage.find { it.currencyCode == code }
        override suspend fun insertAll(rates: List<CurrencyRateEntity>) {
            storage.clear()
            storage.addAll(rates)
        }
        override suspend fun clearCache() { storage.clear() }
        override suspend fun getLastFetchedTime(): Long? = storage.firstOrNull()?.fetchedAtLocalEpochMs
    }

    private val fakeApi = object : CurrencyApiService {
        override suspend fun getLatestRates(base: String): ExchangeRateApiResponse {
            return ExchangeRateApiResponse(
                result = "success",
                provider = "https://www.exchangerate-api.com",
                baseCode = "USD",
                timeLastUpdateUtc = "Wed, 28 Oct 2026 00:00:00 +0000",
                timeNextUpdateUtc = "Thu, 29 Oct 2026 00:00:00 +0000",
                rates = testRates
            )
        }

        override suspend fun getFrankfurterRates(): FrankfurterApiResponse {
            return FrankfurterApiResponse(1.0, "USD", "2026-10-28", testRates)
        }
    }

    private val repository = CurrencyRepository(fakeApi, fakeDao)

    @Test
    fun testUsdToInrConversion() {
        // Given known test rate 1 USD = 83 INR, converting 10 USD should produce 830 INR
        val res = repository.convert(
            amount = BigDecimal("10"),
            fromCurrency = "USD",
            toCurrency = "INR",
            ratesAgainstUsd = testRates
        )
        assertTrue(res.isSuccess)
        assertEquals(BigDecimal("830.0000"), res.convertedAmount)
        assertEquals(BigDecimal("83.00000000"), res.rate)
    }

    @Test
    fun testCurrencySwapAndInverseRate() {
        // Swapping INR back to USD: converting 830 INR at 1 USD = 83 INR should yield 10 USD
        val res = repository.convert(
            amount = BigDecimal("830"),
            fromCurrency = "INR",
            toCurrency = "USD",
            ratesAgainstUsd = testRates
        )
        assertTrue(res.isSuccess)
        assertEquals(BigDecimal("10.0000"), res.convertedAmount)
        // Inverse rate = 1 / 83 = 0.01204819
        assertEquals(BigDecimal("0.01204819"), res.rate)
    }

    @Test
    fun testCrossCurrencyConversion() {
        // EUR to INR: 1 USD = 0.83 EUR, 1 USD = 83 INR
        // 1 EUR = 83 / 0.83 = 100 INR
        // Converting 10 EUR should produce 1,000 INR
        val res = repository.convert(
            amount = BigDecimal("10"),
            fromCurrency = "EUR",
            toCurrency = "INR",
            ratesAgainstUsd = testRates
        )
        assertTrue(res.isSuccess)
        assertEquals(BigDecimal("1000.0000"), res.convertedAmount)
        assertEquals(BigDecimal("100.00000000"), res.rate)
    }

    @Test
    fun testMissingCurrencyRateError() {
        val res = repository.convert(
            amount = BigDecimal("100"),
            fromCurrency = "XYZ", // Non-existent currency code
            toCurrency = "INR",
            ratesAgainstUsd = testRates
        )
        assertFalse(res.isSuccess)
        assertEquals("Rate not found for XYZ", res.errorMessage)
    }

    @Test
    fun testOfflineCachingRetainsTimestamp() = runBlocking {
        // 1. Fetch rates and check caching
        val result1 = repository.getExchangeRates(forceRefresh = true)
        assertEquals("Wed, 28 Oct 2026 00:00:00 +0000", result1.lastUpdatedUtc)
        assertFalse(result1.isOffline)

        // Verify stored in Room DAO cache
        val cached = fakeDao.getAllCachedRates()
        assertTrue(cached.isNotEmpty())
        assertEquals(83.0, cached.find { it.currencyCode == "INR" }?.rateAgainstUsd)

        // 2. Simulate offline repository by failing the network
        val failingApi = object : CurrencyApiService {
            override suspend fun getLatestRates(base: String): ExchangeRateApiResponse {
                throw java.io.IOException("Network unreachable")
            }
            override suspend fun getFrankfurterRates(): FrankfurterApiResponse {
                throw java.io.IOException("Network unreachable")
            }
        }
        val offlineRepo = CurrencyRepository(failingApi, fakeDao)
        val offlineResult = offlineRepo.getExchangeRates(forceRefresh = true)

        assertTrue(offlineResult.isOffline)
        assertTrue(offlineResult.lastUpdatedUtc.contains("Wed, 28 Oct 2026 00:00:00 +0000"))
        assertEquals(83.0, offlineResult.ratesAgainstUsd["INR"])
    }
}
