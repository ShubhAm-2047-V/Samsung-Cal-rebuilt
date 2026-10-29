package com.shubdeep.calculatorpro.ui.converter

import android.app.DatePickerDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.launch
import com.shubdeep.calculatorpro.domain.converter.ConverterCategory
import com.shubdeep.calculatorpro.domain.converter.UnitItem
import com.shubdeep.calculatorpro.ui.components.KeypadButton
import com.shubdeep.calculatorpro.ui.components.KeypadButtonStyle
import com.shubdeep.calculatorpro.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConverterScreen(
    viewModel: ConverterViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var unitPickerTarget by remember { mutableStateOf<String?>(null) } // "from" or "to"

    // Primary categories shown first to match Samsung Calculator exactly
    val primaryCategories = remember {
        listOf(
            ConverterCategory.AREA,
            ConverterCategory.LENGTH,
            ConverterCategory.TEMPERATURE,
            ConverterCategory.VOLUME,
            ConverterCategory.MASS,
            ConverterCategory.DATA_STORAGE,
            ConverterCategory.SPEED,
            ConverterCategory.TIME,
            ConverterCategory.NUMERAL_SYSTEM,
            ConverterCategory.BMI,
            ConverterCategory.AGE,
            ConverterCategory.DATE,
            ConverterCategory.DISCOUNT,
            ConverterCategory.PRESSURE,
            ConverterCategory.ENERGY,
            ConverterCategory.POWER,
            ConverterCategory.COOKING
        )
    }
    val palette = LocalSamsungColors.current


    Column(
        modifier = modifier
            .fillMaxSize()
            .background(palette.background)
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        // -------------------------------------------------------------
        // HEADER: Back Arrow & "Unit converter" Title
        // -------------------------------------------------------------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.size(44.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = palette.textPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = "Unit converter",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = palette.textPrimary
            )
        }


        // -------------------------------------------------------------
        // HORIZONTALLY SCROLLABLE CATEGORY SELECTOR
        // Selected: Rounded gray pill, bold white text
        // Unselected: Subdued gray text, transparent background
        // -------------------------------------------------------------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            primaryCategories.forEach { category ->
                val isSelected = uiState.selectedCategory == category
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) SamsungChipSelected else Color.Transparent)
                        .clickable { viewModel.selectCategory(category) }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = category.displayName,
                        fontSize = 15.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isSelected) SamsungChipTextSelected else SamsungChipTextUnselected
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // -------------------------------------------------------------
        // MAIN CONVERSION FIELDS & KEYPAD AREA
        // -------------------------------------------------------------
        var bmiActiveField by remember { mutableStateOf("height") } // "height" or "weight"
        var discountActiveField by remember { mutableStateOf("price") } // "price" or "discount"
        var swapRotation by remember { mutableFloatStateOf(0f) }
        val animatedSwapRotation by animateFloatAsState(
            targetValue = swapRotation,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            ),
            label = "swapRotation"
        )

        AnimatedContent(
            targetState = uiState.selectedCategory,
            transitionSpec = {
                (fadeIn(tween(200)) + slideInHorizontally(spring(dampingRatio = 0.82f, stiffness = 450f)) { it / 6 })
                    .togetherWith(fadeOut(tween(140)) + slideOutHorizontally(tween(140)) { -it / 6 })
            },
            label = "converterCategorySwitch",
            modifier = Modifier.fillMaxSize()
        ) { category ->
            when (category) {
            ConverterCategory.BMI -> {
                BmiCalculatorView(
                    uiState = uiState,
                    activeField = bmiActiveField,
                    onSelectField = { bmiActiveField = it },
                    onDigit = { viewModel.onBmiKeypadDigit(it, bmiActiveField == "height") },
                    onDecimal = { viewModel.onBmiKeypadDecimal(bmiActiveField == "height") },
                    onBackspace = { viewModel.onBmiKeypadBackspace(bmiActiveField == "height") },
                    onClear = { viewModel.onBmiKeypadClear(bmiActiveField == "height") },
                    onSwap = { bmiActiveField = if (bmiActiveField == "height") "weight" else "height" },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                )
            }

            ConverterCategory.AGE -> {
                AgeCalculatorView(
                    uiState = uiState,
                    onBirthDateChanged = { viewModel.onBirthDateChanged(it) },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                )
            }

            ConverterCategory.DATE -> {
                DateDifferenceCalculatorView(
                    uiState = uiState,
                    onDateRangeChanged = { start, end -> viewModel.onDateRangeChanged(start, end) },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                )
            }

            ConverterCategory.DISCOUNT -> {
                DiscountCalculatorView(
                    uiState = uiState,
                    activeField = discountActiveField,
                    onSelectField = { discountActiveField = it },
                    onQuickDiscount = { disc ->
                        viewModel.onDiscountInputsChanged(
                            uiState.discountPrice,
                            disc,
                            uiState.discountExtraPercent,
                            uiState.discountTaxPercent
                        )
                    },
                    onDigit = { viewModel.onDiscountKeypadDigit(it, discountActiveField == "price") },
                    onDecimal = { viewModel.onDiscountKeypadDecimal(discountActiveField == "price") },
                    onBackspace = { viewModel.onDiscountKeypadBackspace(discountActiveField == "price") },
                    onClear = { viewModel.onDiscountKeypadClear(discountActiveField == "price") },
                    onSwap = { discountActiveField = if (discountActiveField == "price") "discount" else "price" },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                )
            }

            ConverterCategory.NUMERAL_SYSTEM -> {
                NumeralSystemsCalculatorView(
                    uiState = uiState,
                    onInputChanged = { input, base -> viewModel.onNumeralInputChanged(input, base) },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                )
            }

            else -> {
                // Standard Linear Unit Converter layout with stacked conversion cards & circular keypad
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Upper Conversion Area (Two stacked conversion sections)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Section 1: SOURCE UNIT & VALUE
                        ConversionSection(
                            label = uiState.fromUnit?.name ?: "Select unit",
                            symbol = uiState.fromUnit?.symbol ?: "",
                            value = uiState.inputValue,
                            isActive = true,
                            onSelectUnit = { unitPickerTarget = "from" },
                            onValueClick = { /* Already active */ }
                        )

                        // Thin Horizontal Divider with Swap Button
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            HorizontalDivider(
                                color = SamsungDivider,
                                thickness = 1.dp,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Surface(
                                onClick = {
                                    swapRotation += 180f
                                    viewModel.swapUnits()
                                },
                                shape = CircleShape,
                                color = SamsungKeyNumber,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.SwapVert,
                                        contentDescription = "Swap Units",
                                        tint = SamsungKeyOrange,
                                        modifier = Modifier
                                            .size(20.dp)
                                            .graphicsLayer { rotationZ = animatedSwapRotation }
                                    )
                                }
                            }
                        }

                        // Section 2: DESTINATION UNIT & CONVERTED VALUE
                        ConversionSection(
                            label = uiState.toUnit?.name ?: "Select unit",
                            symbol = uiState.toUnit?.symbol ?: "",
                            value = uiState.convertedResult.ifEmpty { "0" },
                            isActive = false,
                            onSelectUnit = { unitPickerTarget = "to" },
                            onValueClick = {
                                swapRotation += 180f
                                viewModel.swapUnits()
                            }
                        )
                    }

                    // Keypad: 4 Columns x 4 Rows
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Row 1: 7 | 8 | 9 | C
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            KeypadButton("7", KeypadButtonStyle.NUMBER, { viewModel.onKeypadDigit("7") }, Modifier.weight(1f))
                            KeypadButton("8", KeypadButtonStyle.NUMBER, { viewModel.onKeypadDigit("8") }, Modifier.weight(1f))
                            KeypadButton("9", KeypadButtonStyle.NUMBER, { viewModel.onKeypadDigit("9") }, Modifier.weight(1f))
                            KeypadButton("C", KeypadButtonStyle.CLEAR, { viewModel.onKeypadClear() }, Modifier.weight(1f))
                        }

                        // Row 2: 4 | 5 | 6 | ⌫
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            KeypadButton("4", KeypadButtonStyle.NUMBER, { viewModel.onKeypadDigit("4") }, Modifier.weight(1f))
                            KeypadButton("5", KeypadButtonStyle.NUMBER, { viewModel.onKeypadDigit("5") }, Modifier.weight(1f))
                            KeypadButton("6", KeypadButtonStyle.NUMBER, { viewModel.onKeypadDigit("6") }, Modifier.weight(1f))
                            KeypadButton(
                                text = "",
                                style = KeypadButtonStyle.BACKSPACE,
                                onClick = { viewModel.onKeypadBackspace() },
                                modifier = Modifier.weight(1f),
                                icon = Icons.AutoMirrored.Filled.Backspace
                            )
                        }

                        // Row 3: 1 | 2 | 3 | ⇅ Swap
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            KeypadButton("1", KeypadButtonStyle.NUMBER, { viewModel.onKeypadDigit("1") }, Modifier.weight(1f))
                            KeypadButton("2", KeypadButtonStyle.NUMBER, { viewModel.onKeypadDigit("2") }, Modifier.weight(1f))
                            KeypadButton("3", KeypadButtonStyle.NUMBER, { viewModel.onKeypadDigit("3") }, Modifier.weight(1f))
                            KeypadButton(
                                text = "",
                                style = KeypadButtonStyle.CONVERTER_ORANGE,
                                onClick = {
                                    swapRotation += 180f
                                    viewModel.swapUnits()
                                },
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.SwapVert
                            )
                        }

                        // Row 4: +/- | 0 | . | Copy
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            KeypadButton("+/-", KeypadButtonStyle.NUMBER, { viewModel.onKeypadToggleSign() }, Modifier.weight(1f), fontSize = 20.sp)
                            KeypadButton("0", KeypadButtonStyle.NUMBER, { viewModel.onKeypadDigit("0") }, Modifier.weight(1f))
                            KeypadButton(".", KeypadButtonStyle.NUMBER, { viewModel.onKeypadDecimal() }, Modifier.weight(1f))
                            KeypadButton(
                                text = "",
                                style = KeypadButtonStyle.CONVERTER_ORANGE,
                                onClick = {
                                    val clipText = "${uiState.inputValue} ${uiState.fromUnit?.symbol ?: ""} = ${uiState.convertedResult} ${uiState.toUnit?.symbol ?: ""}"
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                    clipboard?.setPrimaryClip(ClipData.newPlainText("Unit Conversion", clipText))
                                    Toast.makeText(context, "Copied $clipText", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.ContentCopy
                            )
                        }
                    }
                }
            }
        }
    }
}

    // -------------------------------------------------------------
    // UNIT SELECTOR MODAL BOTTOM SHEET / DIALOG
    // -------------------------------------------------------------
    if (unitPickerTarget != null) {
        val isFrom = unitPickerTarget == "from"
        val currentSelected = if (isFrom) uiState.fromUnit else uiState.toUnit

        Dialog(onDismissRequest = { unitPickerTarget = null }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = SurfaceElevatedDark,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 480.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = if (isFrom) "Convert from" else "Convert to",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        items(uiState.availableUnits) { unit ->
                            val isChosen = unit.id == currentSelected?.id
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isChosen) SamsungChipSelected else Color.Transparent)
                                    .clickable {
                                        if (isFrom) {
                                            viewModel.setFromUnit(unit)
                                        } else {
                                            viewModel.setToUnit(unit)
                                        }
                                        unitPickerTarget = null
                                    }
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = unit.name,
                                        fontSize = 16.sp,
                                        fontWeight = if (isChosen) FontWeight.SemiBold else FontWeight.Normal,
                                        color = if (isChosen) TextPrimaryDark else TextSecondaryDark
                                    )
                                    if (unit.symbol.isNotEmpty()) {
                                        Text(
                                            text = unit.symbol,
                                            fontSize = 12.sp,
                                            color = TextSecondaryDark
                                        )
                                    }
                                }

                                if (isChosen) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = SamsungKeyOrange,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ConversionSection(
    label: String,
    symbol: String,
    value: String,
    isActive: Boolean,
    onSelectUnit: () -> Unit,
    onValueClick: () -> Unit
) {
    val palette = LocalSamsungColors.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onValueClick)
            .padding(vertical = 8.dp)
    ) {
        // Unit Name Dropdown Selector
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = onSelectUnit)
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 16.sp,
                color = if (isActive) palette.textPrimary else palette.textSecondary,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Select Unit",
                tint = palette.textSecondary,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Large right-aligned numerical value with One UI elastic pop
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Bottom
        ) {
            if (isActive && value.length > 1) {
                val prefix = value.dropLast(1)
                val lastChar = value.takeLast(1)
                val charScale = remember(value.length) { Animatable(0.50f) }
                val charAlpha = remember(value.length) { Animatable(0.1f) }
                val charOffsetY = remember(value.length) { Animatable(18f) }

                LaunchedEffect(value.length) {
                    launch {
                        charScale.animateTo(1f, spring(0.60f, 420f))
                    }
                    launch {
                        charAlpha.animateTo(1f, tween(80))
                    }
                    launch {
                        charOffsetY.animateTo(0f, spring(0.72f, 480f))
                    }
                }

                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = prefix,
                        fontSize = if (value.length > 10) 30.sp else 38.sp,
                        fontWeight = FontWeight.Light,
                        color = palette.textPrimary,
                        maxLines = 1
                    )
                    Text(
                        text = lastChar,
                        fontSize = if (value.length > 10) 30.sp else 38.sp,
                        fontWeight = FontWeight.Light,
                        color = palette.textPrimary,
                        maxLines = 1,
                        modifier = Modifier.graphicsLayer {
                            scaleX = charScale.value
                            scaleY = charScale.value
                            alpha = charAlpha.value
                            translationY = charOffsetY.value
                        }
                    )
                }
            } else {
                Text(
                    text = value,
                    fontSize = if (value.length > 10) 30.sp else 38.sp,
                    fontWeight = FontWeight.Light,
                    color = if (isActive) palette.textPrimary else palette.textSecondary,
                    textAlign = TextAlign.End,
                    maxLines = 1
                )
            }
            if (symbol.isNotEmpty()) {
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = symbol,
                    fontSize = 16.sp,
                    color = palette.textSecondary,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }
        }
    }
}


// -------------------------------------------------------------
// 1. BMI CALCULATOR VIEW
// -------------------------------------------------------------
@Composable
private fun BmiCalculatorView(
    uiState: ConverterUiState,
    activeField: String,
    onSelectField: (String) -> Unit,
    onDigit: (String) -> Unit,
    onDecimal: () -> Unit,
    onBackspace: () -> Unit,
    onClear: () -> Unit,
    onSwap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val bmiResult = uiState.bmiResult

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Upper Inputs & Result Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            // Stacked Input Cards for Height and Weight
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Height Card
                val isHeightActive = activeField == "height"
                Surface(
                    onClick = { onSelectField("height") },
                    shape = RoundedCornerShape(16.dp),
                    color = if (isHeightActive) SurfaceElevatedDark else SurfaceDark,
                    border = if (isHeightActive) androidx.compose.foundation.BorderStroke(1.5.dp, SamsungKeyOrange) else null,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Height", fontSize = 13.sp, color = TextSecondaryDark)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = uiState.bmiHeightCm.ifEmpty { "0" },
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isHeightActive) TextPrimaryDark else TextSecondaryDark
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("cm", fontSize = 14.sp, color = TextSecondaryDark, modifier = Modifier.padding(bottom = 4.dp))
                        }
                    }
                }

                // Weight Card
                val isWeightActive = activeField == "weight"
                Surface(
                    onClick = { onSelectField("weight") },
                    shape = RoundedCornerShape(16.dp),
                    color = if (isWeightActive) SurfaceElevatedDark else SurfaceDark,
                    border = if (isWeightActive) androidx.compose.foundation.BorderStroke(1.5.dp, SamsungKeyOrange) else null,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Weight", fontSize = 13.sp, color = TextSecondaryDark)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = uiState.bmiWeightKg.ifEmpty { "0" },
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isWeightActive) TextPrimaryDark else TextSecondaryDark
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("kg", fontSize = 14.sp, color = TextSecondaryDark, modifier = Modifier.padding(bottom = 4.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // BMI Result Display
            if (bmiResult != null) {
                val categoryColor = when (bmiResult.category) {
                    "Underweight" -> SamsungKeyOrange
                    "Normal weight" -> SamsungKeyEquals
                    "Overweight" -> SamsungKeyOrange
                    else -> SamsungKeyClear
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SurfaceDark,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Body Mass Index (BMI)", fontSize = 13.sp, color = TextSecondaryDark)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "%.1f".format(bmiResult.bmi),
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Bold,
                            color = categoryColor
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = categoryColor.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = bmiResult.category,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = categoryColor,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Healthy weight: ${bmiResult.healthyWeightRangeKg}",
                            fontSize = 12.sp,
                            color = TextSecondaryDark
                        )
                    }
                }
            }
        }

        // Bottom Keypad
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                KeypadButton("7", KeypadButtonStyle.NUMBER, { onDigit("7") }, Modifier.weight(1f))
                KeypadButton("8", KeypadButtonStyle.NUMBER, { onDigit("8") }, Modifier.weight(1f))
                KeypadButton("9", KeypadButtonStyle.NUMBER, { onDigit("9") }, Modifier.weight(1f))
                KeypadButton("C", KeypadButtonStyle.CLEAR, { onClear() }, Modifier.weight(1f))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                KeypadButton("4", KeypadButtonStyle.NUMBER, { onDigit("4") }, Modifier.weight(1f))
                KeypadButton("5", KeypadButtonStyle.NUMBER, { onDigit("5") }, Modifier.weight(1f))
                KeypadButton("6", KeypadButtonStyle.NUMBER, { onDigit("6") }, Modifier.weight(1f))
                KeypadButton("", KeypadButtonStyle.BACKSPACE, { onBackspace() }, Modifier.weight(1f), icon = Icons.AutoMirrored.Filled.Backspace)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                KeypadButton("1", KeypadButtonStyle.NUMBER, { onDigit("1") }, Modifier.weight(1f))
                KeypadButton("2", KeypadButtonStyle.NUMBER, { onDigit("2") }, Modifier.weight(1f))
                KeypadButton("3", KeypadButtonStyle.NUMBER, { onDigit("3") }, Modifier.weight(1f))
                KeypadButton("", KeypadButtonStyle.CONVERTER_ORANGE, { onSwap() }, Modifier.weight(1f), icon = Icons.Default.SwapVert)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                KeypadButton("+/-", KeypadButtonStyle.NUMBER, {}, Modifier.weight(1f), fontSize = 20.sp)
                KeypadButton("0", KeypadButtonStyle.NUMBER, { onDigit("0") }, Modifier.weight(1f))
                KeypadButton(".", KeypadButtonStyle.NUMBER, { onDecimal() }, Modifier.weight(1f))
                KeypadButton(
                    text = "",
                    style = KeypadButtonStyle.CONVERTER_ORANGE,
                    onClick = {
                        val text = "BMI: ${bmiResult?.bmi ?: ""} (${bmiResult?.category ?: ""})"
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                        clipboard?.setPrimaryClip(ClipData.newPlainText("BMI Result", text))
                        Toast.makeText(context, "Copied $text", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.ContentCopy
                )
            }
        }
    }
}

// -------------------------------------------------------------
// 2. AGE CALCULATOR VIEW
// -------------------------------------------------------------
@Composable
private fun AgeCalculatorView(
    uiState: ConverterUiState,
    onBirthDateChanged: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val ageResult = uiState.ageResult
    val formatter = remember { DateTimeFormatter.ofPattern("MMM dd, yyyy") }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Date of Birth Card
        Surface(
            onClick = {
                val current = uiState.ageBirthDate
                DatePickerDialog(
                    context,
                    { _, y, m, d -> onBirthDateChanged(LocalDate.of(y, m + 1, d)) },
                    current.year,
                    current.monthValue - 1,
                    current.dayOfMonth
                ).show()
            },
            shape = RoundedCornerShape(16.dp),
            color = SurfaceDark,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Date of birth", fontSize = 13.sp, color = TextSecondaryDark)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = uiState.ageBirthDate.format(formatter),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimaryDark
                    )
                }
                Icon(Icons.Default.CalendarToday, contentDescription = "Pick Date", tint = SamsungKeyOrange, modifier = Modifier.size(24.dp))
            }
        }

        // Today's Date Card
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = SurfaceDark,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Today's date", fontSize = 13.sp, color = TextSecondaryDark)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = LocalDate.now().format(formatter),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimaryDark
                    )
                }
                Icon(Icons.Default.Today, contentDescription = null, tint = TextSecondaryDark, modifier = Modifier.size(24.dp))
            }
        }

        // Age Result Hero Card
        if (ageResult != null) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = SurfaceElevatedDark,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Age", fontSize = 14.sp, color = TextSecondaryDark)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "${ageResult.years}",
                            fontSize = 54.sp,
                            fontWeight = FontWeight.Bold,
                            color = SamsungKeyOrange
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "years",
                            fontSize = 22.sp,
                            color = TextPrimaryDark,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = SurfaceDark
                    ) {
                        Text(
                            text = "${ageResult.months} months | ${ageResult.days} days",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimaryDark,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = SamsungDivider)
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Total Days Lived", fontSize = 12.sp, color = TextSecondaryDark)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "%,d".format(ageResult.totalDays),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryDark
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Next Birthday", fontSize = 12.sp, color = TextSecondaryDark)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "in ${ageResult.daysUntilNextBirthday} days 🎂",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = SamsungKeyEquals
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 3. DATE DIFFERENCE CALCULATOR VIEW
// -------------------------------------------------------------
@Composable
private fun DateDifferenceCalculatorView(
    uiState: ConverterUiState,
    onDateRangeChanged: (LocalDate, LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val diffResult = uiState.dateDiffResult
    val formatter = remember { DateTimeFormatter.ofPattern("MMM dd, yyyy") }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Start Date Card
        Surface(
            onClick = {
                val current = uiState.dateStart
                DatePickerDialog(
                    context,
                    { _, y, m, d -> onDateRangeChanged(LocalDate.of(y, m + 1, d), uiState.dateEnd) },
                    current.year,
                    current.monthValue - 1,
                    current.dayOfMonth
                ).show()
            },
            shape = RoundedCornerShape(16.dp),
            color = SurfaceDark,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("From", fontSize = 13.sp, color = TextSecondaryDark)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = uiState.dateStart.format(formatter),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimaryDark
                    )
                }
                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = SamsungKeyOrange, modifier = Modifier.size(22.dp))
            }
        }

        // End Date Card
        Surface(
            onClick = {
                val current = uiState.dateEnd
                DatePickerDialog(
                    context,
                    { _, y, m, d -> onDateRangeChanged(uiState.dateStart, LocalDate.of(y, m + 1, d)) },
                    current.year,
                    current.monthValue - 1,
                    current.dayOfMonth
                ).show()
            },
            shape = RoundedCornerShape(16.dp),
            color = SurfaceDark,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("To", fontSize = 13.sp, color = TextSecondaryDark)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = uiState.dateEnd.format(formatter),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimaryDark
                    )
                }
                Icon(Icons.Default.Event, contentDescription = null, tint = SamsungKeyEquals, modifier = Modifier.size(22.dp))
            }
        }

        // Difference Result Hero Card
        if (diffResult != null) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = SurfaceElevatedDark,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Difference", fontSize = 14.sp, color = TextSecondaryDark)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "${diffResult.totalDays}",
                            fontSize = 54.sp,
                            fontWeight = FontWeight.Bold,
                            color = SamsungKeyEquals
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "days",
                            fontSize = 22.sp,
                            color = TextPrimaryDark,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = SurfaceDark
                    ) {
                        Text(
                            text = diffResult.weeksAndDays,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimaryDark,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = diffResult.periodString,
                        fontSize = 13.sp,
                        color = TextSecondaryDark
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 4. DISCOUNT CALCULATOR VIEW
// -------------------------------------------------------------
@Composable
private fun DiscountCalculatorView(
    uiState: ConverterUiState,
    activeField: String,
    onSelectField: (String) -> Unit,
    onQuickDiscount: (String) -> Unit,
    onDigit: (String) -> Unit,
    onDecimal: () -> Unit,
    onBackspace: () -> Unit,
    onClear: () -> Unit,
    onSwap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val discResult = uiState.discountResult

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Upper Inputs & Result Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            // Price & Discount Input Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Price Card
                val isPriceActive = activeField == "price"
                Surface(
                    onClick = { onSelectField("price") },
                    shape = RoundedCornerShape(16.dp),
                    color = if (isPriceActive) SurfaceElevatedDark else SurfaceDark,
                    border = if (isPriceActive) androidx.compose.foundation.BorderStroke(1.5.dp, SamsungKeyOrange) else null,
                    modifier = Modifier.weight(1.3f)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Original Price", fontSize = 13.sp, color = TextSecondaryDark)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "₹ ${uiState.discountPrice.ifEmpty { "0" }}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isPriceActive) TextPrimaryDark else TextSecondaryDark
                        )
                    }
                }

                // Discount % Card
                val isDiscActive = activeField == "discount"
                Surface(
                    onClick = { onSelectField("discount") },
                    shape = RoundedCornerShape(16.dp),
                    color = if (isDiscActive) SurfaceElevatedDark else SurfaceDark,
                    border = if (isDiscActive) androidx.compose.foundation.BorderStroke(1.5.dp, SamsungKeyOrange) else null,
                    modifier = Modifier.weight(0.9f)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Discount", fontSize = 13.sp, color = TextSecondaryDark)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${uiState.discountPercent.ifEmpty { "0" }} %",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDiscActive) TextPrimaryDark else TextSecondaryDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Percentage Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("10", "15", "20", "25", "30", "50").forEach { pct ->
                    val isChosen = uiState.discountPercent == pct
                    Surface(
                        onClick = { onQuickDiscount(pct) },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isChosen) SamsungKeyOrange else SurfaceDark
                    ) {
                        Text(
                            text = "$pct%",
                            fontSize = 13.sp,
                            fontWeight = if (isChosen) FontWeight.Bold else FontWeight.Normal,
                            color = if (isChosen) PureBlack else TextPrimaryDark,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Final Price Hero Card
            if (discResult != null) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SurfaceDark,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Final Price", fontSize = 13.sp, color = TextSecondaryDark)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "₹ %.2f".format(discResult.finalPrice),
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Bold,
                            color = SamsungKeyEquals
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = SamsungKeyEquals.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "You save ₹ %.2f (${uiState.discountPercent}%%)".format(discResult.totalSaved),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = SamsungKeyEquals,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Bottom Keypad
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                KeypadButton("7", KeypadButtonStyle.NUMBER, { onDigit("7") }, Modifier.weight(1f))
                KeypadButton("8", KeypadButtonStyle.NUMBER, { onDigit("8") }, Modifier.weight(1f))
                KeypadButton("9", KeypadButtonStyle.NUMBER, { onDigit("9") }, Modifier.weight(1f))
                KeypadButton("C", KeypadButtonStyle.CLEAR, { onClear() }, Modifier.weight(1f))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                KeypadButton("4", KeypadButtonStyle.NUMBER, { onDigit("4") }, Modifier.weight(1f))
                KeypadButton("5", KeypadButtonStyle.NUMBER, { onDigit("5") }, Modifier.weight(1f))
                KeypadButton("6", KeypadButtonStyle.NUMBER, { onDigit("6") }, Modifier.weight(1f))
                KeypadButton("", KeypadButtonStyle.BACKSPACE, { onBackspace() }, Modifier.weight(1f), icon = Icons.AutoMirrored.Filled.Backspace)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                KeypadButton("1", KeypadButtonStyle.NUMBER, { onDigit("1") }, Modifier.weight(1f))
                KeypadButton("2", KeypadButtonStyle.NUMBER, { onDigit("2") }, Modifier.weight(1f))
                KeypadButton("3", KeypadButtonStyle.NUMBER, { onDigit("3") }, Modifier.weight(1f))
                KeypadButton("", KeypadButtonStyle.CONVERTER_ORANGE, { onSwap() }, Modifier.weight(1f), icon = Icons.Default.SwapVert)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                KeypadButton("+/-", KeypadButtonStyle.NUMBER, {}, Modifier.weight(1f), fontSize = 20.sp)
                KeypadButton("0", KeypadButtonStyle.NUMBER, { onDigit("0") }, Modifier.weight(1f))
                KeypadButton(".", KeypadButtonStyle.NUMBER, { onDecimal() }, Modifier.weight(1f))
                KeypadButton(
                    text = "",
                    style = KeypadButtonStyle.CONVERTER_ORANGE,
                    onClick = {
                        val text = "Final: ₹${discResult?.finalPrice ?: ""} (Saved ₹${discResult?.totalSaved ?: ""})"
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                        clipboard?.setPrimaryClip(ClipData.newPlainText("Discount Result", text))
                        Toast.makeText(context, "Copied $text", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.ContentCopy
                )
            }
        }
    }
}

// -------------------------------------------------------------
// 5. NUMERAL SYSTEMS VIEW
// -------------------------------------------------------------
@Composable
private fun NumeralSystemsCalculatorView(
    uiState: ConverterUiState,
    onInputChanged: (String, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        listOf(
            Triple("Decimal (DEC)", uiState.numeralDec, 10),
            Triple("Binary (BIN)", uiState.numeralBin, 2),
            Triple("Octal (OCT)", uiState.numeralOct, 8),
            Triple("Hexadecimal (HEX)", uiState.numeralHex, 16)
        ).forEach { (label, value, base) ->
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SurfaceDark,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(label, fontSize = 12.sp, color = TextSecondaryDark)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = value.ifEmpty { "0" },
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimaryDark
                        )
                    }
                    IconButton(onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                        clipboard?.setPrimaryClip(ClipData.newPlainText(label, value))
                        Toast.makeText(context, "Copied $value", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = TextSecondaryDark, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

