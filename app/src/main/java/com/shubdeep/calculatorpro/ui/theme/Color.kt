package com.shubdeep.calculatorpro.ui.theme

import androidx.compose.ui.graphics.Color

// Samsung Calculator Authentic Color Palette
val PureBlack = Color(0xFF000000)
val DarkCharcoal = Color(0xFF121214)

// Samsung Keypad Colors
val SamsungKeyNumber = Color(0xFF1C1C1E)         // Dark gray circular keys (~#191919 / #1C1C1E)
val SamsungKeyOperator = Color(0xFF1C1C1E)       // Dark gray with white symbols
val SamsungKeyEquals = Color(0xFF168577)         // Iconic Samsung Teal accent (~#168577)
val SamsungKeyEqualsPressed = Color(0xFF10685D)  // Deeper teal on press
val SamsungKeyClear = Color(0xFFFF6259)          // Muted red/pink for C and Backspace
val SamsungKeyOrange = Color(0xFFFF9F0A)         // Samsung Converter accent orange

// Surface and Container Colors
val SurfaceDark = Color(0xFF141416)
val SurfaceElevatedDark = Color(0xFF1C1C1E)
val SurfaceHighlightDark = Color(0xFF2C2C2E)
val SamsungDivider = Color(0xFF262628)
val SamsungChipSelected = Color(0xFF2C2C2E)
val SamsungChipTextSelected = Color(0xFFFFFFFF)
val SamsungChipTextUnselected = Color(0xFF8E8E93)

// Accents
val SamsungTeal = Color(0xFF168577)
val SamsungOrange = Color(0xFFFF7A00)
val SamsungOrangeLight = Color(0xFFFF9533)
val AccentGreen = Color(0xFF34C759)
val AccentAmber = Color(0xFFFF9F0A)
val AccentRed = Color(0xFFFF453A)
val AccentBlue = Color(0xFF0A84FF)

// Text Colors
val TextPrimaryDark = Color(0xFFFFFFFF)
val TextSecondaryDark = Color(0xFF8E8E93)
val TextTertiaryDark = Color(0xFF545458)

// Light Theme Samsung One UI Palette
val BackgroundLight = Color(0xFFFFFFFF)
val SurfaceLight = Color(0xFFF7F8FA)
val SurfaceElevatedLight = Color(0xFFEEF0F3)
val TextPrimaryLight = Color(0xFF000000)
val TextSecondaryLight = Color(0xFF6E6E73)

// Samsung Palette Data Structure for complete Dynamic Theme switching
data class SamsungCalculatorPalette(
    val background: Color,
    val surface: Color,
    val surfaceElevated: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val divider: Color,
    val chipSelected: Color,
    val chipTextSelected: Color,
    val chipTextUnselected: Color,
    
    // Keypad colors
    val keyNumberBg: Color,
    val keyNumberText: Color,
    val keyOperatorBg: Color,
    val keyOperatorText: Color,
    val keyEqualsBg: Color,
    val keyEqualsBgPressed: Color,
    val keyEqualsText: Color,
    val keyClearBg: Color,
    val keyClearText: Color,
    val keyFunctionBg: Color,
    val keyFunctionText: Color,
    val keyOrangeText: Color,
    
    // Accents
    val teal: Color,
    val orange: Color,
    val iconTint: Color
)

val DarkSamsungPalette = SamsungCalculatorPalette(
    background = Color(0xFF000000),
    surface = Color(0xFF141416),
    surfaceElevated = Color(0xFF1C1C1E),
    textPrimary = Color(0xFFFFFFFF),
    textSecondary = Color(0xFF8E8E93),
    divider = Color(0xFF262628),
    chipSelected = Color(0xFF2C2C2E),
    chipTextSelected = Color(0xFFFFFFFF),
    chipTextUnselected = Color(0xFF8E8E93),
    keyNumberBg = Color(0xFF1C1C1E),
    keyNumberText = Color(0xFFFFFFFF),
    keyOperatorBg = Color(0xFF1C1C1E),
    keyOperatorText = Color(0xFFFFFFFF),
    keyEqualsBg = Color(0xFF168577),
    keyEqualsBgPressed = Color(0xFF10685D),
    keyEqualsText = Color(0xFFFFFFFF),
    keyClearBg = Color(0xFF1C1C1E),
    keyClearText = Color(0xFFFF6259),
    keyFunctionBg = Color(0xFF2C2C2E),
    keyFunctionText = Color(0xFF8E8E93),
    keyOrangeText = Color(0xFFFF9F0A),
    teal = Color(0xFF168577),
    orange = Color(0xFFFF9F0A),
    iconTint = Color(0xFF8E8E93)
)

val LightSamsungPalette = SamsungCalculatorPalette(
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFF7F8FA),
    surfaceElevated = Color(0xFFEEF0F3),
    textPrimary = Color(0xFF000000),
    textSecondary = Color(0xFF6E6E73),
    divider = Color(0xFFE5E7EB),
    chipSelected = Color(0xFFE5E7EB),
    chipTextSelected = Color(0xFF000000),
    chipTextUnselected = Color(0xFF6E6E73),
    keyNumberBg = Color(0xFFF2F3F5),
    keyNumberText = Color(0xFF000000),
    keyOperatorBg = Color(0xFFE6E8EB),
    keyOperatorText = Color(0xFF000000),
    keyEqualsBg = Color(0xFF27AE60),
    keyEqualsBgPressed = Color(0xFF219653),
    keyEqualsText = Color(0xFFFFFFFF),
    keyClearBg = Color(0xFFF2F3F5),
    keyClearText = Color(0xFFE53935),
    keyFunctionBg = Color(0xFFE4E6EA),
    keyFunctionText = Color(0xFF424242),
    keyOrangeText = Color(0xFFFF9533),
    teal = Color(0xFF168577),
    orange = Color(0xFFFF9533),
    iconTint = Color(0xFF555555)
)

