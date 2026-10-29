package com.shubdeep.calculatorpro.ui.components

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shubdeep.calculatorpro.ui.theme.*

import androidx.compose.animation.core.tween
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class KeypadButtonStyle {
    NUMBER,
    OPERATOR,
    EQUALS,
    CLEAR,
    BACKSPACE,
    FUNCTION,
    ACTION,
    CONVERTER_ORANGE
}

@Composable
fun KeypadButton(
    text: String,
    style: KeypadButtonStyle,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    hapticsEnabled: Boolean = true,
    fontSize: TextUnit = 28.sp,
    height: Dp? = null,
    icon: ImageVector? = null,
    onHold5Seconds: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val view = LocalView.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    var holdTriggered by remember { mutableStateOf(false) }
    val holdProgress = remember { Animatable(0f) }

    // 5-second long hold detector with sequential haptic countdown
    LaunchedEffect(isPressed) {
        if (isPressed && onHold5Seconds != null) {
            holdTriggered = false
            holdProgress.snapTo(0f)

            // Smoothly animate progress from 0f to 1f over 5 seconds
            launch {
                holdProgress.animateTo(1f, animationSpec = tween(5000, easing = LinearEasing))
            }

            // Provide progressive tactile pulses every 1 second
            for (second in 1..4) {
                delay(1000L)
                if (hapticsEnabled) {
                    performTickHaptic(context)
                }
            }
            delay(1000L) // 5000ms reached!
            holdTriggered = true
            if (hapticsEnabled) {
                performVaultUnlockedHaptic(context)
            }
            onHold5Seconds()
        } else {
            holdProgress.snapTo(0f)
            holdTriggered = false
        }
    }

    // Ultra-snappy tactile compression, bouncy spring release
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.86f else 1.0f,
        animationSpec = spring(
            dampingRatio = 0.52f,
            stiffness = if (isPressed) Spring.StiffnessHigh else Spring.StiffnessMediumLow
        ),
        label = "btn_bounce"
    )

    // Floating key preview popup animations
    val popupScale by animateFloatAsState(
        targetValue = if (isPressed) 1f else 0f,
        animationSpec = spring(
            dampingRatio = 0.58f,
            stiffness = if (isPressed) Spring.StiffnessHigh else Spring.StiffnessMediumLow
        ),
        label = "btn_popup_scale"
    )

    val popupAlpha by animateFloatAsState(
        targetValue = if (isPressed) 1f else 0f,
        animationSpec = tween(durationMillis = if (isPressed) 70 else 110),
        label = "btn_popup_alpha"
    )

    val palette = LocalSamsungColors.current
    val (backgroundColor, contentColor) = when (style) {
        KeypadButtonStyle.NUMBER -> Pair(palette.keyNumberBg, palette.keyNumberText)
        KeypadButtonStyle.OPERATOR -> Pair(palette.keyOperatorBg, palette.keyOperatorText)
        KeypadButtonStyle.EQUALS -> Pair(if (isPressed) palette.keyEqualsBgPressed else palette.keyEqualsBg, palette.keyEqualsText)
        KeypadButtonStyle.CLEAR -> Pair(palette.keyClearBg, palette.keyClearText)
        KeypadButtonStyle.BACKSPACE -> Pair(palette.keyClearBg, palette.keyClearText)
        KeypadButtonStyle.FUNCTION -> Pair(palette.keyFunctionBg, palette.keyFunctionText)
        KeypadButtonStyle.ACTION -> Pair(palette.keyClearBg, palette.keyClearText)
        KeypadButtonStyle.CONVERTER_ORANGE -> Pair(palette.keyNumberBg, palette.keyOrangeText)
    }

    val boxModifier = if (height != null) {
        modifier.height(height)
    } else {
        modifier.aspectRatio(1f)
    }

    // Outer unclipped container so floating preview can float above the button
    Box(
        modifier = boxModifier,
        contentAlignment = Alignment.Center
    ) {
        // Floating Key Magnification Pop-Up Bubble (One UI / iOS Keyboard style)
        if (popupAlpha > 0.01f) {
            val popupOffsetY = if (height != null) (-48).dp else (-56).dp
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = palette.surfaceElevated,
                shadowElevation = 14.dp,
                border = BorderStroke(
                    width = 1.5.dp,
                    color = when (style) {
                        KeypadButtonStyle.EQUALS -> palette.teal
                        KeypadButtonStyle.OPERATOR, KeypadButtonStyle.CONVERTER_ORANGE -> palette.keyOrangeText
                        KeypadButtonStyle.CLEAR, KeypadButtonStyle.BACKSPACE -> palette.keyClearText
                        else -> contentColor.copy(alpha = 0.35f)
                    }
                ),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = popupOffsetY)
                    .graphicsLayer {
                        scaleX = popupScale
                        scaleY = popupScale
                        alpha = popupAlpha
                        translationY = (1f - popupScale) * 18f
                    }
            ) {
                Box(
                    modifier = Modifier
                        .defaultMinSize(minWidth = 56.dp, minHeight = 54.dp)
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (icon != null) {
                        Icon(
                            imageVector = icon,
                            contentDescription = text,
                            tint = contentColor,
                            modifier = Modifier.size(32.dp)
                        )
                    } else {
                        Text(
                            text = text,
                            color = contentColor,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Main Circular Keypad Button
        Box(
            modifier = Modifier
                .fillMaxSize()
                .scale(scale)
                .clip(CircleShape)
                .background(backgroundColor)
                .clickable(
                    interactionSource = interactionSource,
                    indication = rememberRipple(bounded = true, color = contentColor.copy(alpha = 0.35f))
                ) {
                    // Suppress normal click if 5s secret hold completed
                    if (!holdTriggered) {
                        if (hapticsEnabled) {
                            performHaptic(view, context)
                        }
                        onClick()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            // Radiant Energy Glow Ring on Touch
            if (isPressed) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    contentColor.copy(alpha = 0.22f + (holdProgress.value * 0.28f)),
                                    Color.White.copy(alpha = 0.08f + (holdProgress.value * 0.15f)),
                                    Color.Transparent
                                )
                            )
                        )
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(
                            width = 2.dp + (1.5.dp * holdProgress.value),
                            color = contentColor.copy(alpha = 0.45f + (holdProgress.value * 0.45f)),
                            shape = CircleShape
                        )
                )
            }

            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = text,
                    tint = contentColor,
                    modifier = Modifier.size(28.dp)
                )
            } else {
                Text(
                    text = text,
                    color = contentColor,
                    fontSize = fontSize,
                    fontWeight = if (style == KeypadButtonStyle.EQUALS) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
    }
}

private fun performHaptic(view: android.view.View, context: Context) {
    try {
        val feedbackSuccess = view.performHapticFeedback(
            HapticFeedbackConstants.KEYBOARD_TAP,
            HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
        )
        if (!feedbackSuccess) {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(12, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(12)
            }
        }
    } catch (_: Exception) {
    }
}

private fun performTickHaptic(context: Context) {
    try {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(18, 120))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(20)
        }
    } catch (_: Exception) {
    }
}

private fun performVaultUnlockedHaptic(context: Context) {
    try {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createWaveform(
                    longArrayOf(0, 50, 40, 90),
                    intArrayOf(0, 200, 0, 255),
                    -1
                )
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(120)
        }
    } catch (_: Exception) {
    }
}

@Composable
fun OfflineBanner(
    lastUpdatedUtc: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = SamsungDivider,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.CloudOff,
                    contentDescription = "Offline",
                    tint = TextSecondaryDark,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Offline Mode (Cached Rates)",
                        color = TextPrimaryDark,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Rates from: $lastUpdatedUtc",
                        color = TextSecondaryDark,
                        fontSize = 11.sp
                    )
                }
            }
            IconButton(
                onClick = onRetry,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Retry",
                    tint = SamsungKeyEquals,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
