package com.shubdeep.calculatorpro.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyCacheDao {

    @Query("SELECT * FROM currency_rates_cache")
    suspend fun getAllCachedRates(): List<CurrencyRateEntity>

    @Query("SELECT * FROM currency_rates_cache")
    fun observeAllCachedRates(): Flow<List<CurrencyRateEntity>>

    @Query("SELECT * FROM currency_rates_cache WHERE currencyCode = :code LIMIT 1")
    suspend fun getRateByCode(code: String): CurrencyRateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(rates: List<CurrencyRateEntity>)

    @Query("DELETE FROM currency_rates_cache")
    suspend fun clearCache()

    @Query("SELECT MAX(fetchedAtLocalEpochMs) FROM currency_rates_cache")
    suspend fun getLastFetchedTime(): Long?
}
