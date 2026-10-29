package com.shubdeep.calculatorpro.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    object Calculator : Screen("calculator", "Calculator", Icons.Default.Calculate)
    object Converter : Screen("converter", "Converter", Icons.Default.SwapHoriz)
    object Currency : Screen("currency", "Currency", Icons.Default.CurrencyExchange)
    object History : Screen("history", "History", Icons.Default.History)
    object More : Screen("more", "More", Icons.Default.Apps)

    // Sub-screens under More
    object Gst : Screen("gst", "GST Calculator")
    object Emi : Screen("emi", "Loan EMI")
    object Percentage : Screen("percentage", "Percentage")
    object Tip : Screen("tip", "Tip & Split")
    object Constants : Screen("constants", "Constants")
    object Settings : Screen("settings", "Settings")
    object ShubDeepCalsi : Screen("shubdeep_calsi", "ShubDeep Customizer", Icons.Default.Security)
}
