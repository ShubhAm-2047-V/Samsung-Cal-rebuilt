package com.shubdeep.calculatorpro.domain.converter

import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import java.time.LocalDate
import java.time.Period
import java.time.temporal.ChronoUnit

object UnitConverterEngine {

    fun getUnitsForCategory(category: ConverterCategory): List<UnitItem> {
        return when (category) {
            ConverterCategory.LENGTH -> listOf(
                UnitItem("mm", "Millimeter", "mm", 0.001),
                UnitItem("cm", "Centimeter", "cm", 0.01),
                UnitItem("m", "Meter", "m", 1.0), // Base
                UnitItem("km", "Kilometer", "km", 1000.0),
                UnitItem("in", "Inch", "in", 0.0254),
                UnitItem("ft", "Foot", "ft", 0.3048),
                UnitItem("yd", "Yard", "yd", 0.9144),
                UnitItem("mi", "Mile", "mi", 1609.344),
                UnitItem("nmi", "Nautical Mile", "nmi", 1852.0)
            )

            ConverterCategory.AREA -> listOf(
                UnitItem("mm2", "Square Millimeter", "mm²", 0.000001),
                UnitItem("cm2", "Square Centimeter", "cm²", 0.0001),
                UnitItem("m2", "Square Meter", "m²", 1.0), // Base
                UnitItem("ha", "Hectare", "ha", 10000.0),
                UnitItem("ac", "Acre", "ac", 4046.8564224),
                UnitItem("km2", "Square Kilometer", "km²", 1000000.0),
                UnitItem("in2", "Square Inch", "sq in", 0.00064516),
                UnitItem("ft2", "Square Foot", "sq ft", 0.09290304),
                UnitItem("yd2", "Square Yard", "sq yd", 0.83612736),
                UnitItem("mi2", "Square Mile", "sq mi", 2589988.110336)
            )

            ConverterCategory.VOLUME -> listOf(
                UnitItem("ml", "Milliliter", "mL", 0.001),
                UnitItem("l", "Liter", "L", 1.0), // Base
                UnitItem("cm3", "Cubic Centimeter", "cm³", 0.001),
                UnitItem("m3", "Cubic Meter", "m³", 1000.0),
                UnitItem("us_gal", "US Gallon", "US gal", 3.785411784),
                UnitItem("imp_gal", "Imperial Gallon", "Imp gal", 4.54609),
                UnitItem("us_floz", "US Fluid Ounce", "US fl oz", 0.0295735295625),
                UnitItem("cup", "Cup (US)", "cup", 0.2365882365),
                UnitItem("tbsp", "Tablespoon (US)", "tbsp", 0.01478676478125),
                UnitItem("tsp", "Teaspoon (US)", "tsp", 0.00492892159375)
            )

            ConverterCategory.MASS -> listOf(
                UnitItem("mg", "Milligram", "mg", 0.000001),
                UnitItem("g", "Gram", "g", 0.001),
                UnitItem("kg", "Kilogram", "kg", 1.0), // Base
                UnitItem("t", "Metric Tonne", "t", 1000.0),
                UnitItem("oz", "Ounce", "oz", 0.028349523125),
                UnitItem("lb", "Pound", "lb", 0.45359237),
                UnitItem("st", "Stone", "st", 6.35029318)
            )

            ConverterCategory.TEMPERATURE -> listOf(
                UnitItem("c", "Celsius", "°C", 1.0),
                UnitItem("f", "Fahrenheit", "°F", 1.0),
                UnitItem("k", "Kelvin", "K", 1.0)
            )

            ConverterCategory.SPEED -> listOf(
                UnitItem("mps", "Meters per second", "m/s", 1.0), // Base
                UnitItem("kmh", "Kilometers per hour", "km/h", 0.2777777777777778),
                UnitItem("mph", "Miles per hour", "mph", 0.44704),
                UnitItem("fps", "Feet per second", "ft/s", 0.3048),
                UnitItem("knot", "Knot", "kn", 0.5144444444444445)
            )

            ConverterCategory.TIME -> listOf(
                UnitItem("ms", "Millisecond", "ms", 0.001),
                UnitItem("s", "Second", "s", 1.0), // Base
                UnitItem("min", "Minute", "min", 60.0),
                UnitItem("h", "Hour", "h", 3600.0),
                UnitItem("d", "Day", "d", 86400.0),
                UnitItem("wk", "Week", "wk", 604800.0),
                UnitItem("mo", "Month (Avg 30.4375d)", "mo", 2629800.0),
                UnitItem("yr", "Year (365.25d)", "yr", 31557600.0)
            )

            ConverterCategory.DATA_STORAGE -> listOf(
                UnitItem("bit", "Bit", "b", 0.125),
                UnitItem("byte", "Byte", "B", 1.0), // Base
                // Decimal SI Units
                UnitItem("kb", "Kilobyte (SI)", "kB", 1000.0),
                UnitItem("mb", "Megabyte (SI)", "MB", 1000000.0),
                UnitItem("gb", "Gigabyte (SI)", "GB", 1000000000.0),
                UnitItem("tb", "Terabyte (SI)", "TB", 1000000000000.0),
                UnitItem("pb", "Petabyte (SI)", "PB", 1000000000000000.0),
                // Binary IEC Units
                UnitItem("kib", "Kibibyte (IEC)", "KiB", 1024.0),
                UnitItem("mib", "Mebibyte (IEC)", "MiB", 1048576.0),
                UnitItem("gib", "Gibibyte (IEC)", "GiB", 1073741824.0),
                UnitItem("tib", "Tebibyte (IEC)", "TiB", 1099511627776.0),
                UnitItem("pib", "Pebibyte (IEC)", "PiB", 1125899906842624.0)
            )

            ConverterCategory.PRESSURE -> listOf(
                UnitItem("pa", "Pascal", "Pa", 1.0), // Base
                UnitItem("kpa", "Kilopascal", "kPa", 1000.0),
                UnitItem("bar", "Bar", "bar", 100000.0),
                UnitItem("psi", "Pounds per sq inch", "psi", 6894.757293),
                UnitItem("atm", "Standard Atmosphere", "atm", 101325.0),
                UnitItem("mmhg", "Millimeters of Mercury", "mmHg", 133.322387415)
            )

            ConverterCategory.ENERGY -> listOf(
                UnitItem("j", "Joule", "J", 1.0), // Base
                UnitItem("kj", "Kilojoule", "kJ", 1000.0),
                UnitItem("cal", "Calorie", "cal", 4.184),
                UnitItem("kcal", "Kilocalorie", "kcal", 4184.0),
                UnitItem("wh", "Watt-hour", "Wh", 3600.0),
                UnitItem("kwh", "Kilowatt-hour", "kWh", 3600000.0),
                UnitItem("btu", "British Thermal Unit", "BTU", 1055.05585)
            )

            ConverterCategory.POWER -> listOf(
                UnitItem("w", "Watt", "W", 1.0), // Base
                UnitItem("kw", "Kilowatt", "kW", 1000.0),
                UnitItem("mw", "Megawatt", "MW", 1000000.0),
                UnitItem("hp", "Horsepower (Mechanical)", "hp", 745.699872)
            )

            ConverterCategory.FORCE -> listOf(
                UnitItem("n", "Newton", "N", 1.0), // Base
                UnitItem("kn", "Kilonewton", "kN", 1000.0),
                UnitItem("lbf", "Pound-force", "lbf", 4.4482216),
                UnitItem("dyn", "Dyne", "dyn", 0.00001)
            )

            ConverterCategory.FREQUENCY -> listOf(
                UnitItem("hz", "Hertz", "Hz", 1.0), // Base
                UnitItem("khz", "Kilohertz", "kHz", 1000.0),
                UnitItem("mhz", "Megahertz", "MHz", 1000000.0),
                UnitItem("ghz", "Gigahertz", "GHz", 1000000000.0)
            )

            ConverterCategory.ANGLE -> listOf(
                UnitItem("deg", "Degree", "°", 1.0), // Base
                UnitItem("rad", "Radian", "rad", 57.29577951308232),
                UnitItem("grad", "Gradian", "grad", 0.9)
            )

            ConverterCategory.FUEL_ECONOMY -> listOf(
                UnitItem("l100km", "Liters per 100km", "L/100km", 1.0),
                UnitItem("mpg_us", "Miles per gallon (US)", "mpg (US)", 1.0),
                UnitItem("mpg_uk", "Miles per gallon (UK)", "mpg (UK)", 1.0),
                UnitItem("kml", "Kilometers per liter", "km/L", 1.0)
            )

            ConverterCategory.TORQUE -> listOf(
                UnitItem("nm", "Newton-meter", "N·m", 1.0), // Base
                UnitItem("lbfft", "Pound-force foot", "lbf·ft", 1.355818),
                UnitItem("kgfm", "Kilogram-force meter", "kgf·m", 9.80665)
            )

            ConverterCategory.DENSITY -> listOf(
                UnitItem("kgm3", "Kilogram per cubic meter", "kg/m³", 1.0), // Base
                UnitItem("gcm3", "Gram per cubic centimeter", "g/cm³", 1000.0),
                UnitItem("lbft3", "Pound per cubic foot", "lb/ft³", 16.018463),
                UnitItem("lbin3", "Pound per cubic inch", "lb/in³", 27679.904)
            )

            ConverterCategory.TRANSFER_SPEED -> listOf(
                UnitItem("bps", "Bits per second", "bps", 0.125),
                UnitItem("kbps", "Kilobits per second", "kbps", 125.0),
                UnitItem("mbps", "Megabits per second", "Mbps", 125000.0),
                UnitItem("gbps", "Gigabits per second", "Gbps", 125000000.0),
                UnitItem("kbs", "Kilobytes per second", "kB/s", 1000.0),
                UnitItem("mbs", "Megabytes per second", "MB/s", 1000000.0)
            )

            ConverterCategory.COOKING -> listOf(
                UnitItem("tsp", "Teaspoon", "tsp", 4.92892), // mL base
                UnitItem("tbsp", "Tablespoon", "tbsp", 14.7868),
                UnitItem("cup", "Cup", "cup", 240.0),
                UnitItem("floz", "Fluid Ounce", "fl oz", 29.5735),
                UnitItem("ml", "Milliliter", "mL", 1.0), // Base
                UnitItem("l", "Liter", "L", 1000.0),
                UnitItem("pinch", "Pinch", "pinch", 0.3)
            )

            // Non-linear categories with specialized calculations
            ConverterCategory.NUMERAL_SYSTEM,
            ConverterCategory.BMI,
            ConverterCategory.AGE,
            ConverterCategory.DATE,
            ConverterCategory.DISCOUNT -> emptyList()
        }
    }

    /**
     * General Multiplicative Unit Conversion
     */
    fun convertLinear(
        value: Double,
        fromUnit: UnitItem,
        toUnit: UnitItem
    ): Double {
        if (fromUnit.id == toUnit.id) return value
        val baseValue = value * fromUnit.factorToBase
        return baseValue / toUnit.factorToBase
    }

    /**
     * Temperature conversion (Affine transformation)
     */
    fun convertTemperature(value: Double, fromUnitId: String, toUnitId: String): Double {
        if (fromUnitId == toUnitId) return value

        // 1. Convert source to Celsius
        val celsius = when (fromUnitId) {
            "c" -> value
            "f" -> (value - 32.0) * (5.0 / 9.0)
            "k" -> value - 273.15
            else -> value
        }

        // 2. Convert Celsius to target
        return when (toUnitId) {
            "c" -> celsius
            "f" -> (celsius * (9.0 / 5.0)) + 32.0
            "k" -> celsius + 273.15
            else -> celsius
        }
    }

    /**
     * Fuel economy conversion (non-linear reciprocals between L/100km and mpg)
     */
    fun convertFuelEconomy(value: Double, fromUnitId: String, toUnitId: String): Double {
        if (fromUnitId == toUnitId) return value
        if (value <= 0.0) return 0.0

        // Convert to standard km/L
        val kmPerLiter = when (fromUnitId) {
            "kml" -> value
            "l100km" -> 100.0 / value
            "mpg_us" -> value * 0.425144
            "mpg_uk" -> value * 0.354006
            else -> value
        }

        // Convert from km/L to target
        return when (toUnitId) {
            "kml" -> kmPerLiter
            "l100km" -> 100.0 / kmPerLiter
            "mpg_us" -> kmPerLiter / 0.425144
            "mpg_uk" -> kmPerLiter / 0.354006
            else -> kmPerLiter
        }
    }

    /**
     * Numeral system converter
     */
    fun convertNumeral(input: String, fromBase: Int, toBase: Int): String {
        val clean = input.trim().uppercase()
        if (clean.isEmpty()) return ""
        return try {
            val bigInt = BigInteger(clean, fromBase)
            when (toBase) {
                2 -> bigInt.toString(2)
                8 -> bigInt.toString(8)
                10 -> bigInt.toString(10)
                16 -> bigInt.toString(16).uppercase()
                else -> bigInt.toString(toBase)
            }
        } catch (e: Exception) {
            "Invalid base $fromBase number"
        }
    }

    /**
     * BMI Calculation & Category
     */
    data class BmiResult(
        val bmi: Double,
        val category: String,
        val healthyWeightRangeKg: String,
        val disclaimer: String = "BMI is a screening metric, not a definitive diagnosis of body fatness or health."
    )

    fun calculateBmi(heightCm: Double, weightKg: Double): BmiResult? {
        if (heightCm <= 0 || weightKg <= 0) return null
        val heightM = heightCm / 100.0
        val bmi = weightKg / (heightM * heightM)

        val category = when {
            bmi < 18.5 -> "Underweight"
            bmi < 25.0 -> "Normal weight"
            bmi < 30.0 -> "Overweight"
            bmi < 35.0 -> "Obese (Class 1)"
            bmi < 40.0 -> "Obese (Class 2)"
            else -> "Severely Obese (Class 3)"
        }

        val minNormalWeight = 18.5 * (heightM * heightM)
        val maxNormalWeight = 24.9 * (heightM * heightM)
        val rangeStr = "${"%.1f".format(minNormalWeight)} - ${"%.1f".format(maxNormalWeight)} kg"

        return BmiResult(
            bmi = BigDecimal(bmi).setScale(1, RoundingMode.HALF_UP).toDouble(),
            category = category,
            healthyWeightRangeKg = rangeStr
        )
    }

    /**
     * Calendar-aware Age Calculator
     */
    data class AgeResult(
        val years: Int,
        val months: Int,
        val days: Int,
        val totalDays: Long,
        val daysUntilNextBirthday: Long
    )

    fun calculateAge(birthDate: LocalDate, today: LocalDate = LocalDate.now()): AgeResult? {
        if (birthDate.isAfter(today)) return null
        val period = Period.between(birthDate, today)
        val totalDays = ChronoUnit.DAYS.between(birthDate, today)

        var nextBday = birthDate.withYear(today.year)
        if (nextBday.isBefore(today) || nextBday.isEqual(today)) {
            nextBday = nextBday.plusYears(1)
        }
        val daysUntil = ChronoUnit.DAYS.between(today, nextBday)

        return AgeResult(
            years = period.years,
            months = period.months,
            days = period.days,
            totalDays = totalDays,
            daysUntilNextBirthday = daysUntil
        )
    }

    /**
     * Date Calculator: Days between dates
     */
    data class DateDifferenceResult(
        val totalDays: Long,
        val weeksAndDays: String,
        val periodString: String
    )

    fun calculateDateDifference(start: LocalDate, end: LocalDate): DateDifferenceResult {
        val (earlier, later) = if (start.isBefore(end) || start.isEqual(end)) Pair(start, end) else Pair(end, start)
        val totalDays = ChronoUnit.DAYS.between(earlier, later)
        val weeks = totalDays / 7
        val remDays = totalDays % 7
        val period = Period.between(earlier, later)

        return DateDifferenceResult(
            totalDays = totalDays,
            weeksAndDays = "$weeks weeks, $remDays days",
            periodString = "${period.years} years, ${period.months} months, ${period.days} days"
        )
    }

    /**
     * Discount Calculator
     */
    data class DiscountResult(
        val discountAmount: Double,
        val finalPrice: Double,
        val totalSaved: Double
    )

    fun calculateDiscount(
        originalPrice: Double,
        discountPercent: Double,
        extraDiscountPercent: Double = 0.0,
        taxPercent: Double = 0.0
    ): DiscountResult {
        if (originalPrice <= 0.0) return DiscountResult(0.0, 0.0, 0.0)

        val firstDiscount = originalPrice * (discountPercent / 100.0)
        val priceAfterFirst = originalPrice - firstDiscount

        val secondDiscount = priceAfterFirst * (extraDiscountPercent / 100.0)
        val priceAfterSecond = priceAfterFirst - secondDiscount

        val taxAmount = priceAfterSecond * (taxPercent / 100.0)
        val finalPrice = priceAfterSecond + taxAmount
        val totalSaved = originalPrice - priceAfterSecond

        return DiscountResult(
            discountAmount = firstDiscount + secondDiscount,
            finalPrice = BigDecimal(finalPrice).setScale(2, RoundingMode.HALF_UP).toDouble(),
            totalSaved = BigDecimal(totalSaved).setScale(2, RoundingMode.HALF_UP).toDouble()
        )
    }
}
