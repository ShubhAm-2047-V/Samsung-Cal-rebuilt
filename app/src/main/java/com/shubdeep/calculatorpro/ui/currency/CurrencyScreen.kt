package com.shubdeep.calculatorpro.ui.currency

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.shubdeep.calculatorpro.data.remote.CurrencyInfo
import com.shubdeep.calculatorpro.ui.components.KeypadButton
import com.shubdeep.calculatorpro.ui.components.KeypadButtonStyle
import com.shubdeep.calculatorpro.ui.theme.*

@Composable
fun CurrencyScreen(
    viewModel: CurrencyViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showCurrencyDialogFor by remember { mutableStateOf<String?>(null) } // "from" or "to"
    var searchQuery by remember { mutableStateOf("") }

    val fromInfo = viewModel.getCurrencyInfo(uiState.fromCurrency)
    val toInfo = viewModel.getCurrencyInfo(uiState.toCurrency)

    var swapRotation by remember { mutableFloatStateOf(0f) }
    val animatedSwapRotation by animateFloatAsState(
        targetValue = swapRotation,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "currencySwapRotation"
    )

    val popularCurrencies = remember {
        listOf("INR", "USD", "EUR", "GBP", "AED", "SAR", "JPY", "CNY", "CAD", "AUD", "SGD")
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
        // HEADER: Back Arrow, Title, Refresh Button
        // -------------------------------------------------------------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
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
                    text = "Currency converter",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = palette.textPrimary
                )
            }


            IconButton(
                onClick = { viewModel.loadRates(forceRefresh = true) },
                enabled = !uiState.isLoading,
                modifier = Modifier.size(44.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = SamsungKeyOrange,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = TextSecondaryDark,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // -------------------------------------------------------------
        // POPULAR CURRENCY QUICK SELECTION CHIPS
        // -------------------------------------------------------------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            popularCurrencies.forEach { code ->
                val isSelected = uiState.toCurrency == code || uiState.fromCurrency == code
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) SamsungChipSelected else Color.Transparent)
                        .clickable {
                            if (uiState.fromCurrency != code) {
                                viewModel.onToCurrencySelected(code)
                            }
                        }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = code,
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isSelected) SamsungChipTextSelected else SamsungChipTextUnselected
                    )
                }
            }
        }

        // -------------------------------------------------------------
        // MAIN CONVERSION & KEYPAD AREA
        // -------------------------------------------------------------
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Upper Conversion Area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                // Section 1: SOURCE CURRENCY
                CurrencyConversionSection(
                    code = uiState.fromCurrency,
                    name = fromInfo.name,
                    symbol = fromInfo.symbol,
                    amount = uiState.amountInput,
                    isActive = true,
                    onSelectCurrency = {
                        searchQuery = ""
                        showCurrencyDialogFor = "from"
                    }
                )

                // Divider with Swap Action
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
                            viewModel.swapCurrencies()
                        },
                        shape = CircleShape,
                        color = SamsungKeyNumber,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.SwapVert,
                                contentDescription = "Swap Currencies",
                                tint = SamsungKeyOrange,
                                modifier = Modifier
                                    .size(20.dp)
                                    .graphicsLayer { rotationZ = animatedSwapRotation }
                            )
                        }
                    }
                }

                // Section 2: DESTINATION CURRENCY
                CurrencyConversionSection(
                    code = uiState.toCurrency,
                    name = toInfo.name,
                    symbol = toInfo.symbol,
                    amount = uiState.convertedAmount.toPlainString(),
                    isActive = false,
                    onSelectCurrency = {
                        searchQuery = ""
                        showCurrencyDialogFor = "to"
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Exchange Rate & Timestamp Summary
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "1 ${uiState.fromCurrency} = ${uiState.exchangeRate.toPlainString()} ${uiState.toCurrency}",
                        fontSize = 13.sp,
                        color = TextSecondaryDark,
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = if (uiState.isOffline) "Cached Rates" else "Live Rates",
                        fontSize = 12.sp,
                        color = if (uiState.isOffline) AccentAmber else AccentGreen
                    )
                }
            }

            // -------------------------------------------------------------
            // NUMERIC KEYPAD: 4 Columns x 4 Rows
            // -------------------------------------------------------------
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
                            viewModel.swapCurrencies()
                        },
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.SwapVert
                    )
                }

                // Row 4: Bookmark | 0 | . | Copy
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KeypadButton(
                        text = "",
                        style = KeypadButtonStyle.FUNCTION,
                        onClick = {
                            viewModel.recordConversionToHistory()
                            Toast.makeText(context, "Saved conversion to history", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.BookmarkAdd
                    )
                    KeypadButton("0", KeypadButtonStyle.NUMBER, { viewModel.onKeypadDigit("0") }, Modifier.weight(1f))
                    KeypadButton(".", KeypadButtonStyle.NUMBER, { viewModel.onKeypadDecimal() }, Modifier.weight(1f))
                    KeypadButton(
                        text = "",
                        style = KeypadButtonStyle.CONVERTER_ORANGE,
                        onClick = {
                            val clipText = "${uiState.amountInput} ${uiState.fromCurrency} = ${uiState.convertedAmount.toPlainString()} ${uiState.toCurrency}"
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                            clipboard?.setPrimaryClip(ClipData.newPlainText("Currency Conversion", clipText))
                            Toast.makeText(context, "Copied $clipText", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.ContentCopy
                    )
                }
            }
        }
    }

    // -------------------------------------------------------------
    // CURRENCY SELECTION SEARCH DIALOG
    // -------------------------------------------------------------
    if (showCurrencyDialogFor != null) {
        val isFrom = showCurrencyDialogFor == "from"
        val currentCode = if (isFrom) uiState.fromCurrency else uiState.toCurrency

        Dialog(onDismissRequest = { showCurrencyDialogFor = null }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = SurfaceElevatedDark,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 520.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = if (isFrom) "Select source currency" else "Select target currency",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search currency name or code...", color = TextSecondaryDark) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondaryDark) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SamsungKeyOrange,
                            unfocusedBorderColor = SamsungDivider,
                            focusedTextColor = TextPrimaryDark,
                            unfocusedTextColor = TextPrimaryDark
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    val filteredList = remember(searchQuery, viewModel.supportedCurrencies) {
                        if (searchQuery.isBlank()) {
                            viewModel.supportedCurrencies
                        } else {
                            viewModel.supportedCurrencies.filter {
                                it.code.contains(searchQuery, ignoreCase = true) ||
                                        it.name.contains(searchQuery, ignoreCase = true)
                            }
                        }
                    }

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(filteredList) { currency ->
                            val isSelected = currency.code == currentCode
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) SamsungChipSelected else Color.Transparent)
                                    .clickable {
                                        if (isFrom) {
                                            viewModel.onFromCurrencySelected(currency.code)
                                        } else {
                                            viewModel.onToCurrencySelected(currency.code)
                                        }
                                        showCurrencyDialogFor = null
                                    }
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = currency.symbol,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) SamsungKeyOrange else TextPrimaryDark,
                                        modifier = Modifier.width(36.dp)
                                    )

                                    Column {
                                        Text(
                                            text = currency.code,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) TextPrimaryDark else TextSecondaryDark
                                        )
                                        Text(
                                            text = currency.name,
                                            fontSize = 12.sp,
                                            color = TextSecondaryDark,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                if (isSelected) {
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
private fun CurrencyConversionSection(
    code: String,
    name: String,
    symbol: String,
    amount: String,
    isActive: Boolean,
    onSelectCurrency: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Currency Selector Dropdown
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = onSelectCurrency)
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$code - $name",
                fontSize = 16.sp,
                color = if (isActive) TextPrimaryDark else TextSecondaryDark,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Select Currency",
                tint = TextSecondaryDark,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Large right-aligned amount
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Bottom
        ) {
            val displayAmt = amount.ifEmpty { "0" }
            if (isActive && displayAmt.length > 1) {
                val prefix = displayAmt.dropLast(1)
                val lastChar = displayAmt.takeLast(1)
                val charScale = remember(displayAmt.length) { Animatable(0.40f) }
                val charAlpha = remember(displayAmt.length) { Animatable(0.2f) }
                val charOffsetY = remember(displayAmt.length) { Animatable(10f) }

                LaunchedEffect(displayAmt.length) {
                    launch {
                        charScale.animateTo(1f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium))
                    }
                    launch {
                        charAlpha.animateTo(1f, tween(70))
                    }
                    launch {
                        charOffsetY.animateTo(0f, spring(Spring.DampingRatioNoBouncy, Spring.StiffnessMedium))
                    }
                }

                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = prefix,
                        fontSize = if (displayAmt.length > 10) 28.sp else 36.sp,
                        fontWeight = FontWeight.Light,
                        color = TextPrimaryDark,
                        maxLines = 1
                    )
                    Text(
                        text = lastChar,
                        fontSize = if (displayAmt.length > 10) 28.sp else 36.sp,
                        fontWeight = FontWeight.Light,
                        color = TextPrimaryDark,
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
                    text = displayAmt,
                    fontSize = if (displayAmt.length > 10) 28.sp else 36.sp,
                    fontWeight = FontWeight.Light,
                    color = if (isActive) TextPrimaryDark else TextSecondaryDark,
                    textAlign = TextAlign.End,
                    maxLines = 1
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = symbol,
                fontSize = 16.sp,
                color = TextSecondaryDark,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }
    }
}
