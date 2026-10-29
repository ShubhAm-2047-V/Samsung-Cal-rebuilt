package com.shubdeep.calculatorpro.domain.vault

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

object VaultLauncher {

    const val TARGET_PACKAGE_NAME = "com.example.shubdeep_calsi"

    fun findActivity(context: Context): FragmentActivity? {
        var current = context
        while (current is ContextWrapper) {
            if (current is FragmentActivity) return current
            current = current.baseContext
        }
        return null
    }

    fun launch(context: Context, onAuthFailed: (String) -> Unit = {}) {
        val activity = findActivity(context)
        if (activity != null) {
            authenticateWithActivity(activity, onSuccess = { launchTargetApp(activity) }, onAuthFailed = onAuthFailed)
        } else {
            launchTargetApp(context)
        }
    }

    fun authenticate(
        context: Context,
        onSuccess: () -> Unit,
        onAuthFailed: (String) -> Unit = {}
    ) {
        val activity = findActivity(context)
        if (activity != null) {
            authenticateWithActivity(activity, onSuccess, onAuthFailed)
        } else {
            onSuccess()
        }
    }

    fun authenticateWithActivity(
        activity: FragmentActivity,
        onSuccess: () -> Unit,
        onAuthFailed: (String) -> Unit = {}
    ) {
        val biometricManager = BiometricManager.from(activity)
        val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or
                BiometricManager.Authenticators.BIOMETRIC_WEAK or
                BiometricManager.Authenticators.DEVICE_CREDENTIAL

        val canAuthenticate = biometricManager.canAuthenticate(authenticators)

        if (canAuthenticate != BiometricManager.BIOMETRIC_SUCCESS &&
            canAuthenticate != BiometricManager.BIOMETRIC_STATUS_UNKNOWN
        ) {
            // Biometrics not enrolled; grant direct access with notice
            Toast.makeText(
                activity,
                "Device lock not configured. Opening Vault...",
                Toast.LENGTH_SHORT
            ).show()
            onSuccess()
            return
        }

        val executor = ContextCompat.getMainExecutor(activity)
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Unlock Private Vault")
            .setSubtitle("Confirm your fingerprint, face, or PIN to open ShubDeep Calsi")
            .setAllowedAuthenticators(authenticators)
            .build()

        val biometricPrompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    performSuccessHaptic(activity)
                    Toast.makeText(activity, "Vault Unlocked", Toast.LENGTH_SHORT).show()
                    activity.runOnUiThread {
                        onSuccess()
                    }
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    if (errorCode != BiometricPrompt.ERROR_USER_CANCELED &&
                        errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON
                    ) {
                        Toast.makeText(activity, errString, Toast.LENGTH_SHORT).show()
                        onAuthFailed(errString.toString())
                    }
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    performErrorHaptic(activity)
                }
            }
        )

        biometricPrompt.authenticate(promptInfo)
    }

    private fun launchTargetApp(context: Context) {
        val pm = context.packageManager
        val intent = pm.getLaunchIntentForPackage(TARGET_PACKAGE_NAME)
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
            context.startActivity(intent)
        } else {
            Toast.makeText(
                context,
                "ShubDeep Calsi ($TARGET_PACKAGE_NAME) is not installed on this device",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun performSuccessHaptic(context: Context) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createWaveform(
                    longArrayOf(0, 45, 60, 75),
                    intArrayOf(0, 180, 0, 255),
                    -1
                )
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(100)
        }
    }

    private fun performErrorHaptic(context: Context) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createWaveform(
                    longArrayOf(0, 60, 50, 60),
                    intArrayOf(0, 220, 0, 220),
                    -1
                )
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(150)
        }
    }
}
