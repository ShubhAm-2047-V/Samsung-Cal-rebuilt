package com.shubdeep.calculatorpro

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.shubdeep.calculatorpro.data.local.ThemeMode
import com.shubdeep.calculatorpro.ui.calculator.CalculatorViewModel
import com.shubdeep.calculatorpro.ui.converter.ConverterViewModel
import com.shubdeep.calculatorpro.ui.currency.CurrencyViewModel
import com.shubdeep.calculatorpro.ui.history.HistoryViewModel
import com.shubdeep.calculatorpro.ui.navigation.MainAppScaffold
import com.shubdeep.calculatorpro.ui.settings.SettingsViewModel
import com.shubdeep.calculatorpro.ui.theme.ShubDeepCalculatorProTheme

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as CalculatorApplication

        setContent {
            val settingsViewModel = viewModel {
                SettingsViewModel(
                    preferencesManager = app.preferencesManager,
                    historyRepository = app.historyRepository,
                    currencyRepository = app.currencyRepository
                )
            }
            val userSettings by settingsViewModel.userSettings.collectAsState()

            val calculatorViewModel = viewModel {
                CalculatorViewModel(
                    historyRepository = app.historyRepository,
                    preferencesManager = app.preferencesManager
                )
            }
            val converterViewModel = viewModel {
                ConverterViewModel(
                    historyRepository = app.historyRepository
                )
            }
            val currencyViewModel = viewModel {
                CurrencyViewModel(
                    currencyRepository = app.currencyRepository,
                    historyRepository = app.historyRepository,
                    preferencesManager = app.preferencesManager
                )
            }
            val historyViewModel = viewModel {
                HistoryViewModel(
                    historyRepository = app.historyRepository
                )
            }

            val navController = rememberNavController()

            ShubDeepCalculatorProTheme(themeMode = userSettings.themeMode) {
                MainAppScaffold(
                    navController = navController,
                    calculatorViewModel = calculatorViewModel,
                    converterViewModel = converterViewModel,
                    currencyViewModel = currencyViewModel,
                    historyViewModel = historyViewModel,
                    settingsViewModel = settingsViewModel
                )
            }
        }
    }
}
