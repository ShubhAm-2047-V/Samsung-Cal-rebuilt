package com.shubdeep.calculatorpro.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

enum class ThemeMode {
    DARK,
    LIGHT,
    SYSTEM
}

enum class NumberFormatPreference {
    INDIAN,       // 1,00,000.00
    INTERNATIONAL // 100,000.00
}

enum class AngleMode {
    DEG,
    RAD
}

data class UserSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val hapticFeedback: Boolean = true,
    val soundFeedback: Boolean = false,
    val numberFormat: NumberFormatPreference = NumberFormatPreference.INDIAN,
    val decimalPrecision: String = "AUTO", // "2", "4", "6", "8", "AUTO"
    val defaultBaseCurrency: String = "INR",
    val defaultTargetCurrency: String = "USD",
    val angleMode: AngleMode = AngleMode.DEG
)

class PreferencesManager(private val context: Context) {

    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val HAPTIC_FEEDBACK = booleanPreferencesKey("haptic_feedback")
        val SOUND_FEEDBACK = booleanPreferencesKey("sound_feedback")
        val NUMBER_FORMAT = stringPreferencesKey("number_format")
        val DECIMAL_PRECISION = stringPreferencesKey("decimal_precision")
        val DEFAULT_BASE_CURRENCY = stringPreferencesKey("default_base_currency")
        val DEFAULT_TARGET_CURRENCY = stringPreferencesKey("default_target_currency")
        val ANGLE_MODE = stringPreferencesKey("angle_mode")
    }

    val userSettingsFlow: Flow<UserSettings> = context.dataStore.data.map { preferences ->
        val themeString = preferences[Keys.THEME_MODE] ?: ThemeMode.SYSTEM.name
        val theme = try { ThemeMode.valueOf(themeString) } catch (e: Exception) { ThemeMode.SYSTEM }

        val numberFormatString = preferences[Keys.NUMBER_FORMAT] ?: NumberFormatPreference.INDIAN.name
        val numberFormat = try { NumberFormatPreference.valueOf(numberFormatString) } catch (e: Exception) { NumberFormatPreference.INDIAN }

        val angleModeString = preferences[Keys.ANGLE_MODE] ?: AngleMode.DEG.name
        val angleMode = try { AngleMode.valueOf(angleModeString) } catch (e: Exception) { AngleMode.DEG }

        UserSettings(
            themeMode = theme,
            hapticFeedback = preferences[Keys.HAPTIC_FEEDBACK] ?: true,
            soundFeedback = preferences[Keys.SOUND_FEEDBACK] ?: false,
            numberFormat = numberFormat,
            decimalPrecision = preferences[Keys.DECIMAL_PRECISION] ?: "AUTO",
            defaultBaseCurrency = preferences[Keys.DEFAULT_BASE_CURRENCY] ?: "INR",
            defaultTargetCurrency = preferences[Keys.DEFAULT_TARGET_CURRENCY] ?: "USD",
            angleMode = angleMode
        )
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { it[Keys.THEME_MODE] = mode.name }
    }

    suspend fun setHapticFeedback(enabled: Boolean) {
        context.dataStore.edit { it[Keys.HAPTIC_FEEDBACK] = enabled }
    }

    suspend fun setSoundFeedback(enabled: Boolean) {
        context.dataStore.edit { it[Keys.SOUND_FEEDBACK] = enabled }
    }

    suspend fun setNumberFormat(format: NumberFormatPreference) {
        context.dataStore.edit { it[Keys.NUMBER_FORMAT] = format.name }
    }

    suspend fun setDecimalPrecision(precision: String) {
        context.dataStore.edit { it[Keys.DECIMAL_PRECISION] = precision }
    }

    suspend fun setDefaultBaseCurrency(currencyCode: String) {
        context.dataStore.edit { it[Keys.DEFAULT_BASE_CURRENCY] = currencyCode }
    }

    suspend fun setDefaultTargetCurrency(currencyCode: String) {
        context.dataStore.edit { it[Keys.DEFAULT_TARGET_CURRENCY] = currencyCode }
    }

    suspend fun setAngleMode(mode: AngleMode) {
        context.dataStore.edit { it[Keys.ANGLE_MODE] = mode.name }
    }
}
