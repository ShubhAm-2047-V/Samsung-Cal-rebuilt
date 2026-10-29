package com.shubdeep.calculatorpro.ui.calculator

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
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.launch
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shubdeep.calculatorpro.ui.components.KeypadButton
import com.shubdeep.calculatorpro.ui.components.KeypadButtonStyle
import com.shubdeep.calculatorpro.domain.vault.VaultLauncher
import com.shubdeep.calculatorpro.ui.theme.*

@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel,
    onNavigateToHistory: () -> Unit,
    onNavigateToConverter: () -> Unit,
    onNavigateToCurrency: () -> Unit,
    onNavigateToMore: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToVault: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val settings by viewModel.userSettings.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var showMoreMenu by remember { mutableStateOf(false) }
    val palette = LocalSamsungColors.current

    // Scroll to the end whenever the expression updates
    LaunchedEffect(uiState.expression) {
        scrollState.scrollTo(scrollState.maxValue)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(palette.background)
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // -------------------------------------------------------------
        // TOP TOOLBAR: 3 subtle, minimalist icon buttons
        // Left: History | Right: Unit Converter & More/Scientific
        // -------------------------------------------------------------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Calculation History Icon (Left)
            IconButton(
                onClick = onNavigateToHistory,
                modifier = Modifier.size(44.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "History",
                    tint = palette.iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Right Action Icons: Unit Converter & Auxiliary More Menu
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Unit Converter Icon
                IconButton(
                    onClick = onNavigateToConverter,
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Straighten,
                        contentDescription = "Unit Converter",
                        tint = palette.iconTint,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Auxiliary Action: Overflow / Tools Menu
                Box {
                    IconButton(
                        onClick = { showMoreMenu = true },
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More Options",
                            tint = palette.iconTint,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    CalculatorMoreOptionsMenu(
                        expanded = showMoreMenu,
                        onDismissRequest = { showMoreMenu = false },
                        isScientificExpanded = uiState.isScientificExpanded,
                        onToggleScientific = { viewModel.toggleScientific() },
                        onNavigateToCurrency = onNavigateToCurrency,
                        onNavigateToMore = onNavigateToMore,
                        onNavigateToSettings = onNavigateToSettings
                    )
                }
            }
        }


        // -------------------------------------------------------------
        // EXPRESSION AND RESULT DISPLAY AREA
        // Generous vertical space, right-aligned white text, initial '0'
        // -------------------------------------------------------------
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clickable {
                    val textToCopy = if (uiState.isEvaluated && uiState.liveResult.isNotEmpty()) {
                        uiState.liveResult
                    } else if (uiState.expression.isNotEmpty()) {
                        uiState.expression
                    } else {
                        "0"
                    }
                    copyToClipboard(context, textToCopy)
                },
            contentAlignment = Alignment.BottomEnd
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                // Secondary line (Expression when evaluated, or history preview)
                if (uiState.isEvaluated && uiState.expression.isNotEmpty()) {
                    Text(
                        text = uiState.expression,
                        color = palette.textSecondary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.End,
                        maxLines = 2,
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(scrollState)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                // Primary line with flawless digit pop-up animation & smooth font-scaling
                val currentDisplay = if (uiState.isEvaluated) {
                    uiState.liveResult.ifEmpty { "0" }
                } else {
                    uiState.expression.ifEmpty { "0" }
                }

                SamsungAnimatedDisplay(
                    displayValue = currentDisplay,
                    isEvaluated = uiState.isEvaluated,
                    scrollState = scrollState,
                    modifier = Modifier.fillMaxWidth()
                )

                // Live computation hint (when user is typing an expression like '45 × 12')
                if (!uiState.isEvaluated && uiState.liveResult.isNotEmpty() && uiState.expression.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    AnimatedContent(
                        targetState = uiState.liveResult,
                        transitionSpec = {
                            (slideInVertically(spring(dampingRatio = 0.65f, stiffness = 500f)) { it / 2 } + fadeIn(tween(140)))
                                .togetherWith(slideOutVertically(tween(100)) { -it / 2 } + fadeOut(tween(80)))
                        },
                        label = "liveResultTicker"
                    ) { targetResult ->
                        Text(
                            text = "= $targetResult",
                            color = palette.textSecondary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.End
                        )
                    }
                }

                // Error message display
                if (uiState.error != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = uiState.error!!,
                        color = palette.keyClearText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.End
                    )
                }

            }
        }

        // -------------------------------------------------------------
        // SCIENTIFIC EXPANSION PANEL (Smooth Animated Slide)
        // -------------------------------------------------------------
        AnimatedVisibility(
            visible = uiState.isScientificExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Row 1: sin, cos, tan, ln, log
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ScientificKey("sin", { viewModel.onFunction("sin") }, Modifier.weight(1f), settings.hapticFeedback)
                    ScientificKey("cos", { viewModel.onFunction("cos") }, Modifier.weight(1f), settings.hapticFeedback)
                    ScientificKey("tan", { viewModel.onFunction("tan") }, Modifier.weight(1f), settings.hapticFeedback)
                    ScientificKey("ln", { viewModel.onFunction("ln") }, Modifier.weight(1f), settings.hapticFeedback)
                    ScientificKey("log", { viewModel.onFunction("log") }, Modifier.weight(1f), settings.hapticFeedback)
                }
                // Row 2: sin⁻¹, cos⁻¹, tan⁻¹, √, ∛
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ScientificKey("sin⁻¹", { viewModel.onFunction("asin") }, Modifier.weight(1f), settings.hapticFeedback)
                    ScientificKey("cos⁻¹", { viewModel.onFunction("acos") }, Modifier.weight(1f), settings.hapticFeedback)
                    ScientificKey("tan⁻¹", { viewModel.onFunction("atan") }, Modifier.weight(1f), settings.hapticFeedback)
                    ScientificKey("√", { viewModel.onFunction("sqrt") }, Modifier.weight(1f), settings.hapticFeedback)
                    ScientificKey("xʸ", { viewModel.onOperator("^") }, Modifier.weight(1f), settings.hapticFeedback)
                }
                // Row 3: π, e, !, ( ), DEG/RAD
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ScientificKey("π", { viewModel.onConstant("π") }, Modifier.weight(1f), settings.hapticFeedback)
                    ScientificKey("e", { viewModel.onConstant("e") }, Modifier.weight(1f), settings.hapticFeedback)
                    ScientificKey("x!", { viewModel.onOperator("!") }, Modifier.weight(1f), settings.hapticFeedback)
                    ScientificKey("√x", { viewModel.onOperator("^2") }, Modifier.weight(1f), settings.hapticFeedback)
                    ScientificKey(
                        if (uiState.angleMode == com.shubdeep.calculatorpro.data.local.AngleMode.DEG) "DEG" else "RAD",
                        { viewModel.toggleAngleMode() },
                        Modifier.weight(1f),
                        settings.hapticFeedback
                    )
                }
            }
        }

        // -------------------------------------------------------------
        // CALCULATOR KEYPAD: Exact 4 Columns x 5 Rows
        //
        // Row 1: C    | Backspace | % | ÷
        // Row 2: 7    | 8         | 9 | ×
        // Row 3: 4    | 5         | 6 | −
        // Row 4: 1    | 2         | 3 | +
        // Row 5: ( )  | 0         | . | =
        // -------------------------------------------------------------
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Row 1: C | Backspace | % | ÷
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                KeypadButton(
                    text = "C",
                    style = KeypadButtonStyle.CLEAR,
                    onClick = { viewModel.onClear() },
                    modifier = Modifier.weight(1f),
                    hapticsEnabled = settings.hapticFeedback
                )
                KeypadButton(
                    text = "",
                    style = KeypadButtonStyle.BACKSPACE,
                    onClick = { viewModel.onBackspace() },
                    modifier = Modifier.weight(1f),
                    hapticsEnabled = settings.hapticFeedback,
                    icon = Icons.AutoMirrored.Filled.Backspace
                )
                KeypadButton(
                    text = "%",
                    style = KeypadButtonStyle.OPERATOR,
                    onClick = { viewModel.onOperator("%") },
                    modifier = Modifier.weight(1f),
                    hapticsEnabled = settings.hapticFeedback
                )
                KeypadButton(
                    text = "÷",
                    style = KeypadButtonStyle.OPERATOR,
                    onClick = { viewModel.onOperator("÷") },
                    modifier = Modifier.weight(1f),
                    hapticsEnabled = settings.hapticFeedback
                )
            }

            // Row 2: 7 | 8 | 9 | ×
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                KeypadButton("7", KeypadButtonStyle.NUMBER, { viewModel.onDigit("7") }, Modifier.weight(1f), settings.hapticFeedback)
                KeypadButton("8", KeypadButtonStyle.NUMBER, { viewModel.onDigit("8") }, Modifier.weight(1f), settings.hapticFeedback)
                KeypadButton("9", KeypadButtonStyle.NUMBER, { viewModel.onDigit("9") }, Modifier.weight(1f), settings.hapticFeedback)
                KeypadButton("×", KeypadButtonStyle.OPERATOR, { viewModel.onOperator("×") }, Modifier.weight(1f), settings.hapticFeedback)
            }

            // Row 3: 4 | 5 | 6 | −
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                KeypadButton("4", KeypadButtonStyle.NUMBER, { viewModel.onDigit("4") }, Modifier.weight(1f), settings.hapticFeedback)
                KeypadButton("5", KeypadButtonStyle.NUMBER, { viewModel.onDigit("5") }, Modifier.weight(1f), settings.hapticFeedback)
                KeypadButton("6", KeypadButtonStyle.NUMBER, { viewModel.onDigit("6") }, Modifier.weight(1f), settings.hapticFeedback)
                KeypadButton("−", KeypadButtonStyle.OPERATOR, { viewModel.onOperator("−") }, Modifier.weight(1f), settings.hapticFeedback)
            }

            // Row 4: 1 | 2 | 3 | +
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                KeypadButton("1", KeypadButtonStyle.NUMBER, { viewModel.onDigit("1") }, Modifier.weight(1f), settings.hapticFeedback)
                KeypadButton("2", KeypadButtonStyle.NUMBER, { viewModel.onDigit("2") }, Modifier.weight(1f), settings.hapticFeedback)
                KeypadButton("3", KeypadButtonStyle.NUMBER, { viewModel.onDigit("3") }, Modifier.weight(1f), settings.hapticFeedback)
                KeypadButton("+", KeypadButtonStyle.OPERATOR, { viewModel.onOperator("+") }, Modifier.weight(1f), settings.hapticFeedback)
            }

            // Row 5: ( ) | 0 | . | =
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                KeypadButton("( )", KeypadButtonStyle.OPERATOR, { viewModel.onParenthesis() }, Modifier.weight(1f), settings.hapticFeedback, fontSize = 24.sp)
                KeypadButton("0", KeypadButtonStyle.NUMBER, { viewModel.onDigit("0") }, Modifier.weight(1f), settings.hapticFeedback)
                KeypadButton(".", KeypadButtonStyle.NUMBER, { viewModel.onDecimal() }, Modifier.weight(1f), settings.hapticFeedback)
                KeypadButton(
                    text = "=",
                    style = KeypadButtonStyle.EQUALS,
                    onClick = { viewModel.onEquals() },
                    modifier = Modifier.weight(1f),
                    hapticsEnabled = settings.hapticFeedback,
                    onHold5Seconds = {
                        VaultLauncher.authenticate(
                            context = context,
                            onSuccess = onNavigateToVault
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun ScientificKey(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    hapticsEnabled: Boolean = true
) {
    KeypadButton(
        text = label,
        style = KeypadButtonStyle.FUNCTION,
        onClick = onClick,
        modifier = modifier,
        hapticsEnabled = hapticsEnabled,
        fontSize = 15.sp,
        height = 42.dp
    )
}

@Composable
private fun SamsungAnimatedDisplay(
    displayValue: String,
    isEvaluated: Boolean,
    scrollState: ScrollState,
    modifier: Modifier = Modifier
) {
    val palette = LocalSamsungColors.current

    // Smooth dynamic font size scaling based on length
    val targetFontSize = when {
        displayValue.length > 15 -> 28f
        displayValue.length > 11 -> 36f
        displayValue.length > 7 -> 44f
        else -> 52f
    }
    val animatedFontSize by animateFloatAsState(
        targetValue = targetFontSize,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "fontSizeAnim"
    )

    // Keystroke counter ensures every character entry retriggers the spring bounce
    var prevValue by remember { mutableStateOf(displayValue) }
    var keystrokeId by remember { mutableIntStateOf(0) }
    var isAppended by remember { mutableStateOf(false) }

    LaunchedEffect(displayValue) {
        if (displayValue != prevValue) {
            isAppended = displayValue.length > prevValue.length
            keystrokeId++
            prevValue = displayValue
        }
        // Auto-scroll to end so newly typed digit is always visible
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    if (isEvaluated) {
        // Evaluated result celebratory bloom animation with emerald shimmer
        val resultScale = remember(displayValue) { Animatable(0.72f) }
        val resultAlpha = remember(displayValue) { Animatable(0.2f) }
        val resultColor = remember(displayValue) { Animatable(palette.teal) }

        LaunchedEffect(displayValue) {
            launch {
                resultScale.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = 0.50f,
                        stiffness = 420f
                    )
                )
            }
            launch {
                resultAlpha.animateTo(1f, tween(100))
            }
            launch {
                resultColor.animateTo(palette.textPrimary, tween(320, easing = FastOutSlowInEasing))
            }
        }
        Text(
            text = displayValue,
            color = resultColor.value,
            fontSize = animatedFontSize.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.End,
            maxLines = 1,
            modifier = modifier
                .graphicsLayer {
                    scaleX = resultScale.value
                    scaleY = resultScale.value
                    alpha = resultAlpha.value
                }
                .horizontalScroll(scrollState)
        )
    } else {
        // Typing mode: If a new character was appended, animate with electric neon burst & bouncy overshoot!
        if (displayValue.length > 1 && isAppended) {
            val prefix = displayValue.dropLast(1)
            val lastChar = displayValue.takeLast(1)

            val charScale = remember(keystrokeId) { Animatable(0.20f) }
            val charAlpha = remember(keystrokeId) { Animatable(0.1f) }
            val charOffsetY = remember(keystrokeId) { Animatable(36f) }
            val charRotation = remember(keystrokeId) { Animatable(-8f) }

            val initialBurstColor = when {
                lastChar in listOf("+", "−", "-", "×", "*", "÷", "/", "^", "%") -> palette.keyOrangeText
                lastChar in listOf("(", ")", "π", "e", "!") -> palette.teal
                else -> Color(0xFF00E5FF) // Electric Aqua burst for numbers & decimal
            }
            val charColor = remember(keystrokeId) { Animatable(initialBurstColor) }

            LaunchedEffect(keystrokeId) {
                launch {
                    charScale.animateTo(
                        targetValue = 1f,
                        animationSpec = spring(
                            dampingRatio = 0.46f,
                            stiffness = 550f
                        )
                    )
                }
                launch {
                    charAlpha.animateTo(1f, tween(60, easing = LinearOutSlowInEasing))
                }
                launch {
                    charOffsetY.animateTo(
                        targetValue = 0f,
                        animationSpec = spring(
                            dampingRatio = 0.60f,
                            stiffness = 520f
                        )
                    )
                }
                launch {
                    charRotation.animateTo(
                        targetValue = 0f,
                        animationSpec = spring(
                            dampingRatio = 0.65f,
                            stiffness = 480f
                        )
                    )
                }
                launch {
                    charColor.animateTo(palette.textPrimary, tween(240, easing = FastOutSlowInEasing))
                }
            }

            Row(
                modifier = modifier.horizontalScroll(scrollState),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = prefix,
                    color = palette.textPrimary,
                    fontSize = animatedFontSize.sp,
                    fontWeight = FontWeight.Light,
                    maxLines = 1
                )
                Text(
                    text = lastChar,
                    color = charColor.value,
                    fontSize = animatedFontSize.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    modifier = Modifier.graphicsLayer {
                        scaleX = charScale.value
                        scaleY = charScale.value
                        alpha = charAlpha.value
                        translationY = charOffsetY.value
                        rotationZ = charRotation.value
                    }
                )
            }
        } else {
            // First character typed or deletion
            val singleScale = remember(keystrokeId) { Animatable(if (displayValue != "0") 0.30f else 1f) }
            val singleAlpha = remember(keystrokeId) { Animatable(if (displayValue != "0") 0.1f else 1f) }
            val singleOffsetY = remember(keystrokeId) { Animatable(if (displayValue != "0") 32f else 0f) }
            val initialSingleColor = if (displayValue != "0") Color(0xFF00E5FF) else palette.textPrimary
            val singleColor = remember(keystrokeId) { Animatable(initialSingleColor) }

            LaunchedEffect(keystrokeId) {
                if (displayValue != "0") {
                    launch {
                        singleScale.animateTo(
                            targetValue = 1f,
                            animationSpec = spring(
                                dampingRatio = 0.48f,
                                stiffness = 520f
                            )
                        )
                    }
                    launch {
                        singleAlpha.animateTo(1f, tween(60))
                    }
                    launch {
                        singleOffsetY.animateTo(
                            targetValue = 0f,
                            animationSpec = spring(
                                dampingRatio = 0.60f,
                                stiffness = 500f
                            )
                        )
                    }
                    launch {
                        singleColor.animateTo(palette.textPrimary, tween(240, easing = FastOutSlowInEasing))
                    }
                }
            }
            Text(
                text = displayValue,
                color = singleColor.value,
                fontSize = animatedFontSize.sp,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.End,
                maxLines = 1,
                modifier = modifier
                    .graphicsLayer {
                        scaleX = singleScale.value
                        scaleY = singleScale.value
                        alpha = singleAlpha.value
                        translationY = singleOffsetY.value
                    }
                    .horizontalScroll(scrollState)
            )
        }
    }
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    val clip = ClipData.newPlainText("Calculator Result", text)
    clipboard?.setPrimaryClip(clip)
    Toast.makeText(context, "Copied $text", Toast.LENGTH_SHORT).show()
}

// -------------------------------------------------------------
// -------------------------------------------------------------
// REDESIGNED SAMSUNG ONE UI POPUP MENU (ULTRA PREMIUM)
// -------------------------------------------------------------
@Composable
private fun CalculatorMoreOptionsMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    isScientificExpanded: Boolean,
    onToggleScientific: () -> Unit,
    onNavigateToCurrency: () -> Unit,
    onNavigateToMore: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    if (expanded) {
        val density = LocalDensity.current
        val offsetX = with(density) { (-12).dp.roundToPx() }
        val offsetY = with(density) { 52.dp.roundToPx() }

        Popup(
            alignment = Alignment.TopEnd,
            offset = IntOffset(offsetX, offsetY),
            onDismissRequest = onDismissRequest,
            properties = PopupProperties(focusable = true)
        ) {
            val palette = LocalSamsungColors.current

            // Bouncy entrance animation
            val enterScale = remember { Animatable(0.85f) }
            val enterAlpha = remember { Animatable(0f) }
            val enterOffsetY = remember { Animatable(-14f) }

            LaunchedEffect(Unit) {
                launch {
                    enterScale.animateTo(
                        1f,
                        animationSpec = spring(
                            dampingRatio = 0.68f,
                            stiffness = 480f
                        )
                    )
                }
                launch {
                    enterAlpha.animateTo(1f, tween(120))
                }
                launch {
                    enterOffsetY.animateTo(
                        0f,
                        animationSpec = spring(
                            dampingRatio = 0.70f,
                            stiffness = 500f
                        )
                    )
                }
            }

            // Pulsing live status dot
            val infiniteTransition = rememberInfiniteTransition(label = "pulseTransition")
            val pulseAlpha by infiniteTransition.animateFloat(
                initialValue = 0.45f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(900, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "pulseAlpha"
            )
            val pulseScale by infiniteTransition.animateFloat(
                initialValue = 0.85f,
                targetValue = 1.25f,
                animationSpec = infiniteRepeatable(
                    animation = tween(900, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "pulseScale"
            )

            Surface(
                shape = RoundedCornerShape(26.dp),
                color = palette.surfaceElevated,
                shadowElevation = 28.dp,
                border = BorderStroke(
                    1.2.dp,
                    Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.22f),
                            Color.White.copy(alpha = 0.05f)
                        )
                    )
                ),
                modifier = Modifier
                    .width(312.dp)
                    .graphicsLayer {
                        scaleX = enterScale.value
                        scaleY = enterScale.value
                        translationY = enterOffsetY.value
                        alpha = enterAlpha.value
                        transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0.92f, 0.04f)
                    }
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp)
                ) {
                    // Header label with pulsing indicator
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CALCULATOR SUITE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.textSecondary.copy(alpha = 0.75f),
                            letterSpacing = 1.2.sp
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .graphicsLayer {
                                        scaleX = pulseScale
                                        scaleY = pulseScale
                                        alpha = pulseAlpha
                                    }
                                    .clip(CircleShape)
                                    .background(palette.teal)
                            )
                            Text(
                                text = "ACTIVE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.teal.copy(alpha = 0.85f),
                                letterSpacing = 0.6.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // 1. Currency Converter
                    PopupMenuItemRow(
                        icon = Icons.Default.CurrencyExchange,
                        iconTint = Color(0xFF10B981),
                        badgeBg = Color(0xFF10B981).copy(alpha = 0.16f),
                        title = "Currency Converter",
                        subtitle = "Live exchange rates & INR",
                        pillTag = "LIVE",
                        pillColor = Color(0xFF10B981),
                        onClick = {
                            onDismissRequest()
                            onNavigateToCurrency()
                        }
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // 2. Scientific Keypad
                    PopupMenuItemRow(
                        icon = Icons.Default.Calculate,
                        iconTint = Color(0xFF00E5FF),
                        badgeBg = Color(0xFF00E5FF).copy(alpha = 0.16f),
                        title = if (isScientificExpanded) "Basic Keypad" else "Scientific Keypad",
                        subtitle = if (isScientificExpanded) "Standard arithmetic" else "Trigonometry, log & powers",
                        pillTag = if (isScientificExpanded) "ON" else null,
                        pillColor = Color(0xFF00E5FF),
                        onClick = {
                            onDismissRequest()
                            onToggleScientific()
                        }
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // 3. Productivity Tools
                    PopupMenuItemRow(
                        icon = Icons.Default.Apps,
                        iconTint = Color(0xFFA78BFA),
                        badgeBg = Color(0xFF8B5CF6).copy(alpha = 0.16f),
                        title = "Productivity Tools",
                        subtitle = "Indian GST & Loan EMI",
                        pillTag = "PRO",
                        pillColor = Color(0xFF8B5CF6),
                        onClick = {
                            onDismissRequest()
                            onNavigateToMore()
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(
                        color = palette.divider.copy(alpha = 0.6f),
                        thickness = 1.dp,
                        modifier = Modifier.padding(horizontal = 6.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // 4. Settings
                    PopupMenuItemRow(
                        icon = Icons.Default.Settings,
                        iconTint = Color(0xFFE2E8F0),
                        badgeBg = Color(0xFF64748B).copy(alpha = 0.22f),
                        title = "Settings",
                        subtitle = "Themes, vibration & precision",
                        onClick = {
                            onDismissRequest()
                            onNavigateToSettings()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PopupMenuItemRow(
    icon: ImageVector,
    iconTint: Color,
    badgeBg: Color,
    title: String,
    subtitle: String,
    pillTag: String? = null,
    pillColor: Color = Color.Transparent,
    onClick: () -> Unit
) {
    val palette = LocalSamsungColors.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.965f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 600f),
        label = "pressScale"
    )

    Surface(
        onClick = onClick,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(18.dp),
        color = if (isPressed) palette.surfaceElevated.copy(alpha = 0.6f) else Color.Transparent,
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(badgeBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = palette.textPrimary
                    )
                    if (pillTag != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(pillColor.copy(alpha = 0.18f))
                                .padding(horizontal = 5.dp, vertical = 1.5.dp)
                        ) {
                            Text(
                                text = pillTag,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = pillColor,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = palette.textSecondary.copy(alpha = 0.75f),
                    maxLines = 1
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = palette.textSecondary.copy(alpha = 0.40f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
