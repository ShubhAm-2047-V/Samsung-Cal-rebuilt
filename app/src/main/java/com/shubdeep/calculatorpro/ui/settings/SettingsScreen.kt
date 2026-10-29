package com.shubdeep.calculatorpro.ui.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shubdeep.calculatorpro.data.local.NumberFormatPreference
import com.shubdeep.calculatorpro.data.local.ThemeMode
import com.shubdeep.calculatorpro.ui.theme.*

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.userSettings.collectAsState()
    val context = LocalContext.current

    var showAboutDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryDark
        )
        Text(
            text = "Personalize your calculator experience",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondaryDark
        )

        Spacer(modifier = Modifier.height(18.dp))

        // THEME SELECTION
        SettingsSectionHeader("Appearance")
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Theme Mode", color = TextPrimaryDark, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ThemeMode.values().forEach { mode ->
                        val isSelected = settings.themeMode == mode
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setThemeMode(mode) },
                            label = { Text(mode.name.lowercase().replaceFirstChar { it.uppercase() }) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SamsungOrange,
                                selectedLabelColor = PureBlack,
                                containerColor = SurfaceElevatedDark,
                                labelColor = TextPrimaryDark
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // NUMBER FORMATTING & PRECISION
        SettingsSectionHeader("Calculation & Display")
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Number Grouping Format", color = TextPrimaryDark, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = settings.numberFormat == NumberFormatPreference.INDIAN,
                        onClick = { viewModel.setNumberFormat(NumberFormatPreference.INDIAN) },
                        label = { Text("Indian (₹ 1,00,000.00)", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SamsungOrange,
                            selectedLabelColor = PureBlack,
                            containerColor = SurfaceElevatedDark,
                            labelColor = TextPrimaryDark
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = settings.numberFormat == NumberFormatPreference.INTERNATIONAL,
                        onClick = { viewModel.setNumberFormat(NumberFormatPreference.INTERNATIONAL) },
                        label = { Text("International (100,000.00)", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SamsungOrange,
                            selectedLabelColor = PureBlack,
                            containerColor = SurfaceElevatedDark,
                            labelColor = TextPrimaryDark
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = SurfaceHighlightDark)
                Spacer(modifier = Modifier.height(14.dp))

                Text("Decimal Precision", color = TextPrimaryDark, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("AUTO", "2", "4", "6", "8").forEach { p ->
                        val isSelected = settings.decimalPrecision == p
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setDecimalPrecision(p) },
                            label = { Text(p, fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SamsungOrange,
                                selectedLabelColor = PureBlack,
                                containerColor = SurfaceElevatedDark,
                                labelColor = TextPrimaryDark
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // FEEDBACK SETTINGS
        SettingsSectionHeader("Haptics & Sound")
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Vibration / Haptic Feedback", color = TextPrimaryDark, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        Text("Vibrate on keypad button press", color = TextSecondaryDark, fontSize = 12.sp)
                    }
                    Switch(
                        checked = settings.hapticFeedback,
                        onCheckedChange = { viewModel.setHapticFeedback(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = PureBlack, checkedTrackColor = SamsungOrange)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // DATA & CACHE
        SettingsSectionHeader("Data Management")
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                SettingsActionRow(
                    title = "Clear Calculation History",
                    subtitle = "Permanently delete all calculation logs",
                    icon = Icons.Default.DeleteOutline,
                    onClick = {
                        viewModel.clearHistory()
                        Toast.makeText(context, "Calculation history cleared", Toast.LENGTH_SHORT).show()
                    }
                )
                HorizontalDivider(color = SurfaceHighlightDark)
                SettingsActionRow(
                    title = "Clear Currency Cache",
                    subtitle = "Remove cached rates so fresh rates will be fetched",
                    icon = Icons.Default.Cached,
                    onClick = {
                        viewModel.clearCurrencyCache()
                        Toast.makeText(context, "Currency rate cache cleared", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ABOUT & PRIVACY
        SettingsSectionHeader("About & Legal")
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                SettingsActionRow(
                    title = "About ShubDeep Calculator Pro",
                    subtitle = "Version 1.0.0 • Architecture & Features",
                    icon = Icons.Default.Info,
                    onClick = { showAboutDialog = true }
                )
                HorizontalDivider(color = SurfaceHighlightDark)
                SettingsActionRow(
                    title = "Privacy Policy",
                    subtitle = "Zero tracking • Local storage only",
                    icon = Icons.Default.Security,
                    onClick = { showPrivacyDialog = true }
                )
            }
        }
    }

    // About Dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("ShubDeep Calculator Pro", color = TextPrimaryDark, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Version 1.0.0 (Production Release)", color = SamsungOrange, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "ShubDeep Calculator Pro is a powerful, offline-first Android calculator built for Samsung Galaxy and modern Android devices.\n\n" +
                        "• Standard & Scientific Math with PEMDAS precedence\n" +
                        "• Real-time international currency conversion with INR priority\n" +
                        "• 24+ Engineering & Daily Unit conversion categories\n" +
                        "• Indian GST & Loan EMI Productivity suites\n" +
                        "• Room database local calculation persistence\n" +
                        "• Built with Kotlin & Jetpack Compose Material 3",
                        color = TextSecondaryDark,
                        fontSize = 13.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("OK", color = SamsungOrange, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = SurfaceDark
        )
    }

    // Privacy Policy Dialog
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text("Privacy Policy", color = TextPrimaryDark, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        "Your privacy is our highest priority:\n\n" +
                        "1. Local Storage: All your calculations, currency conversions, and preferences are stored exclusively on your device using Room and DataStore.\n\n" +
                        "2. No Account or Tracking: ShubDeep Calculator Pro does not require an account, has no analytics trackers, and collects zero personal data.\n\n" +
                        "3. Internet Access: Network access is used solely to query public exchange rate providers (ExchangeRate-API and European Central Bank via Frankfurter) to deliver accurate currency quotes.",
                        color = TextSecondaryDark,
                        fontSize = 13.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) {
                    Text("Got It", color = SamsungOrange, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = SurfaceDark
        )
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        color = SamsungOrangeLight,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun SettingsActionRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = SamsungOrange, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = TextPrimaryDark, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Text(text = subtitle, color = TextSecondaryDark, fontSize = 12.sp)
        }
        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondaryDark)
    }
}
