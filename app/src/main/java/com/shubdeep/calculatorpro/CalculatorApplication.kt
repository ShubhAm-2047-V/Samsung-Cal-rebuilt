package com.shubdeep.calculatorpro

import android.app.Application
import com.shubdeep.calculatorpro.data.local.AppDatabase
import com.shubdeep.calculatorpro.data.local.PreferencesManager
import com.shubdeep.calculatorpro.data.remote.CurrencyApiClient
import com.shubdeep.calculatorpro.data.repository.CurrencyRepository
import com.shubdeep.calculatorpro.data.repository.HistoryRepository

class CalculatorApplication : Application() {

    lateinit var database: AppDatabase
        private set

    lateinit var preferencesManager: PreferencesManager
        private set

    lateinit var historyRepository: HistoryRepository
        private set

    lateinit var currencyRepository: CurrencyRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        database = AppDatabase.getInstance(this)
        preferencesManager = PreferencesManager(this)
        historyRepository = HistoryRepository(database.calculationHistoryDao())
        currencyRepository = CurrencyRepository(
            apiService = CurrencyApiClient.apiService,
            currencyDao = database.currencyCacheDao()
        )
    }

    companion object {
        lateinit var instance: CalculatorApplication
            private set
    }
}
