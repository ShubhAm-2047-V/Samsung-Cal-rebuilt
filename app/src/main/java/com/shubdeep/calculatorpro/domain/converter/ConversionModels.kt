package com.shubdeep.calculatorpro.domain.converter

enum class ConverterCategory(val displayName: String, val icon: String) {
    LENGTH("Length", "📏"),
    AREA("Area", "📐"),
    VOLUME("Volume", "🧪"),
    MASS("Mass & Weight", "⚖️"),
    TEMPERATURE("Temperature", "🌡️"),
    SPEED("Speed", "🚗"),
    TIME("Time", "⏱️"),
    DATA_STORAGE("Data Storage", "💾"),
    NUMERAL_SYSTEM("Numeral Systems", "🔢"),
    BMI("BMI", "🏃"),
    AGE("Age", "🎂"),
    DATE("Date Calculation", "📅"),
    DISCOUNT("Discount", "🏷️"),
    PRESSURE("Pressure", "🔘"),
    ENERGY("Energy", "⚡"),
    POWER("Power", "💡"),
    FORCE("Force", "🔨"),
    FREQUENCY("Frequency", "📡"),
    ANGLE("Angle", "📐"),
    FUEL_ECONOMY("Fuel Economy", "⛽"),
    TORQUE("Torque", "🔧"),
    DENSITY("Density", "🧱"),
    TRANSFER_SPEED("Transfer Speed", "🚀"),
    COOKING("Cooking", "🍳")
}

data class UnitItem(
    val id: String,
    val name: String,
    val symbol: String,
    val factorToBase: Double = 1.0 // Multiplier relative to the category's standard base unit
)
