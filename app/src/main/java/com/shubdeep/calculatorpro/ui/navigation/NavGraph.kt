package com.shubdeep.calculatorpro.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.shubdeep.calculatorpro.ui.calculator.CalculatorScreen
import com.shubdeep.calculatorpro.ui.calculator.CalculatorViewModel
import com.shubdeep.calculatorpro.ui.converter.ConverterScreen
import com.shubdeep.calculatorpro.ui.converter.ConverterViewModel
import com.shubdeep.calculatorpro.ui.currency.CurrencyScreen
import com.shubdeep.calculatorpro.ui.currency.CurrencyViewModel
import com.shubdeep.calculatorpro.ui.history.HistoryScreen
import com.shubdeep.calculatorpro.ui.history.HistoryViewModel
import com.shubdeep.calculatorpro.ui.more.*
import com.shubdeep.calculatorpro.ui.settings.SettingsScreen
import androidx.compose.animation.core.CubicBezierEasing
import com.shubdeep.calculatorpro.ui.settings.SettingsViewModel
import com.shubdeep.calculatorpro.ui.customizer.ShubDeepCustomizerScreen

@Composable
fun MainAppScaffold(
    navController: NavHostController,
    calculatorViewModel: CalculatorViewModel,
    converterViewModel: ConverterViewModel,
    currencyViewModel: CurrencyViewModel,
    historyViewModel: HistoryViewModel,
    settingsViewModel: SettingsViewModel
) {
    // Dynamic One UI surface container
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val oneUiEasing = CubicBezierEasing(0.22f, 1f, 0.36f, 1f)
        NavHost(
            navController = navController,
            startDestination = Screen.Calculator.route,
            modifier = Modifier.fillMaxSize(),
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(320, easing = oneUiEasing)) +
                        scaleIn(initialScale = 0.92f, animationSpec = tween(320, easing = oneUiEasing)) +
                        fadeIn(tween(240))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(300, easing = oneUiEasing)) +
                        scaleOut(targetScale = 0.92f, animationSpec = tween(300, easing = oneUiEasing)) +
                        fadeOut(tween(180))
            },
            popEnterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(320, easing = oneUiEasing)) +
                        scaleIn(initialScale = 0.92f, animationSpec = tween(320, easing = oneUiEasing)) +
                        fadeIn(tween(240))
            },
            popExitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(300, easing = oneUiEasing)) +
                        scaleOut(targetScale = 0.92f, animationSpec = tween(300, easing = oneUiEasing)) +
                        fadeOut(tween(180))
            }
        ) {
            composable(Screen.Calculator.route) {
                CalculatorScreen(
                    viewModel = calculatorViewModel,
                    onNavigateToHistory = { navController.navigate(Screen.History.route) },
                    onNavigateToConverter = { navController.navigate(Screen.Converter.route) },
                    onNavigateToCurrency = { navController.navigate(Screen.Currency.route) },
                    onNavigateToMore = { navController.navigate(Screen.More.route) },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                    onNavigateToVault = { navController.navigate(Screen.ShubDeepCalsi.route) }
                )
            }

            composable(Screen.Converter.route) {
                ConverterScreen(
                    viewModel = converterViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Currency.route) {
                CurrencyScreen(
                    viewModel = currencyViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.History.route) {
                HistoryScreen(
                    viewModel = historyViewModel,
                    onReuseCalculation = { expr ->
                        calculatorViewModel.setExpression(expr)
                        navController.navigate(Screen.Calculator.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                        }
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.More.route) {
                MoreScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToGst = { navController.navigate(Screen.Gst.route) },
                    onNavigateToEmi = { navController.navigate(Screen.Emi.route) },
                    onNavigateToPercentage = { navController.navigate(Screen.Percentage.route) },
                    onNavigateToTip = { navController.navigate(Screen.Tip.route) },
                    onNavigateToConstants = { navController.navigate(Screen.Constants.route) },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
                )
            }

            composable(Screen.Gst.route) {
                GstScreen(onNavigateBack = { navController.popBackStack() })
            }

            composable(Screen.Emi.route) {
                EmiScreen(onNavigateBack = { navController.popBackStack() })
            }

            composable(Screen.Percentage.route) {
                PercentageScreen()
            }

            composable(Screen.Tip.route) {
                TipSplitScreen()
            }

            composable(Screen.Constants.route) {
                ConstantsScreen()
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    viewModel = settingsViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.ShubDeepCalsi.route) {
                ShubDeepCustomizerScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
