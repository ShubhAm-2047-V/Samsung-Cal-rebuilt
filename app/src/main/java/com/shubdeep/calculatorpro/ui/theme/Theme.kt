package com.shubdeep.calculatorpro.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.shubdeep.calculatorpro.data.local.ThemeMode

val LocalSamsungColors = staticCompositionLocalOf { DarkSamsungPalette }

val MaterialTheme.samsungColors: SamsungCalculatorPalette
    @Composable
    get() = LocalSamsungColors.current

private val DarkColorScheme = darkColorScheme(
    primary = SamsungTeal,
    onPrimary = Color.White,
    primaryContainer = SamsungTeal,
    onPrimaryContainer = Color.White,
    secondary = SurfaceElevatedDark,
    onSecondary = Color.White,
    background = Color(0xFF000000),
    onBackground = Color(0xFFFFFFFF),
    surface = Color(0xFF141416),
    onSurface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFF1C1C1E),
    onSurfaceVariant = Color(0xFF8E8E93),
    outline = SamsungDivider
)

private val LightColorScheme = lightColorScheme(
    primary = SamsungTeal,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE6F4F1),
    onPrimaryContainer = Color(0xFF00382E),
    secondary = Color(0xFFEEF0F3),
    onSecondary = Color(0xFF1C1C1E),
    background = Color(0xFFFFFFFF),
    onBackground = Color(0xFF000000),
    surface = Color(0xFFF7F8FA),
    onSurface = Color(0xFF000000),
    surfaceVariant = Color(0xFFEEF0F3),
    onSurfaceVariant = Color(0xFF6E6E73),
    outline = Color(0xFFE5E7EB)
)

@Composable
fun ShubDeepCalculatorProTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val palette = if (darkTheme) DarkSamsungPalette else LightSamsungPalette
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(LocalSamsungColors provides palette) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

