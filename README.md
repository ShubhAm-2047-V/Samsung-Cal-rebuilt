# ShubDeep Calculator Pro

**ShubDeep Calculator Pro** is a modern, production-grade Android calculator application crafted specifically for Samsung Galaxy smartphones and modern Android devices. Designed with a Samsung One UI-inspired aesthetic, the application combines a standard and scientific calculation engine, a live international currency converter with Indian Rupee (INR) priority, a comprehensive 24+ category unit converter, and specialized productivity tools (Indian GST & Loan EMI calculators).

Built strictly with an **offline-first** philosophy, all mathematical, unit, and productivity computations operate without any network dependency. Currency rates are cached locally in a Room database so you can seamlessly calculate conversions even with no internet connection.

---

## 🌟 Key Highlights & Features

### 1. 🧮 Standard & Scientific Calculator
* **Mathematical Precision**: Safe, deterministic mathematical expression parser built from scratch with standard PEMDAS/BODMAS operator precedence.
* **Basic Operations**: Addition, subtraction, multiplication, division, percentage, sign toggle (`+/-`), backspace, and parentheses.
* **Scientific Functions**:
  * Trigonometric: `sin`, `cos`, `tan`, `asin`, `acos`, `atan`
  * Hyperbolic: `sinh`, `cosh`, `tanh`
  * Logarithmic: `log` (base 10) and `ln` (natural logarithm)
  * Roots & Powers: `√` (square root), `∛` (cube root), `x^y`, `x²`, `x³`, `1/x`
  * Factorial: `n!`
  * Physical & Mathematical Constants: `π` (Pi), `e` (Euler's number)
  * Angle Switcher: **DEG** (Degrees) and **RAD** (Radians) with instant recalculation.
* **Safe Error Handling**: Prevents crashes from division by zero (`Cannot divide by zero`), invalid domains (e.g. `sqrt(-1)` or `log(0)`), unmatched parentheses, and floating-point overflow.
* **Ergonomics**: Smooth haptic feedback on button presses (user-configurable), live expression evaluation preview, and tap-to-copy results.

---

### 2. 💱 Live International Currency Converter (INR-First Experience)
* **Real-Time & Reference Data**: Integrates with live, reputable exchange rate services:
  * Primary: `https://open.er-api.com/v6/latest/USD` (160+ world currencies, real UTC timestamps, no API key required).
  * Fallback: European Central Bank reference rates via Frankfurter API.
* **INR-First Quick Conversion Shortcuts**:
  * `USD → INR`, `EUR → INR`, `GBP → INR`, `AED → INR`, `SAR → INR`, `CAD → INR`, `JPY → INR`, `AUD → INR`
  * `INR → USD`, `INR → EUR`, `INR → AED`, etc.
* **Cross-Currency Precision**: Calculations use decimal-safe `BigDecimal` arithmetic via base currency normalisation, avoiding premature floating-point rounding errors.
* **Offline Resilience**:
  * When internet is unavailable, conversions continue using the latest cached rates from the local Room database.
  * Explicit amber banner alerts the user: `⚠️ Offline Mode: Using rates from [timestamp]`.
  * Preserves original rate timestamps; never fabricates live rates.
* **Currency Search & Tools**:
  * Search dialog for 160+ currencies by name or ISO code.
  * Animated swap button.
  * Direct clipboard copying and sharing of converted quotes.
  * Save conversion to history with exact exchange rate metadata.

---

### 3. 📐 Comprehensive Unit Converter (24+ Categories)
Engineered with a testable conversion engine supporting both linear multiplicative and affine non-linear transformations:
1. **Length**: Millimeter, Centimeter, Meter, Kilometer, Inch, Foot, Yard, Mile, Nautical Mile
2. **Area**: Square Millimeter, Square Centimeter, Square Meter, Hectare, Acre, Square Kilometer, Square Inch, Square Foot, Square Yard, Square Mile
3. **Volume**: Milliliter, Liter, Cubic Centimeter, Cubic Meter, US Gallon, Imperial Gallon, US Fluid Ounce, Cup, Tablespoon, Teaspoon
4. **Mass & Weight**: Milligram, Gram, Kilogram, Metric Tonne, Ounce, Pound, Stone
5. **Temperature**: Celsius, Fahrenheit, Kelvin (with affine transformation formulas)
6. **Speed**: m/s, km/h, mph, ft/s, knots
7. **Time**: Millisecond, Second, Minute, Hour, Day, Week, Average Month, Year
8. **Data Storage**: Decimal SI (`kB`, `MB`, `GB`, `TB`, `PB`) vs. Binary IEC (`KiB`, `MiB`, `GiB`, `TiB`, `PiB`)
9. **Numeral Systems**: Instant conversion between Binary (base 2), Octal (base 8), Decimal (base 10), and Hexadecimal (base 16)
10. **BMI Calculator**: Adult Body Mass Index calculator with healthy weight range guide and health disclaimer
11. **Age Calculator**: Calendar-aware exact age in years, months, days, total days lived, and countdown to next birthday
12. **Date Calculator**: Duration between dates (days, weeks, period) and date addition/subtraction
13. **Discount Calculator**: Original price, discount %, secondary discount %, and sales tax breakdown
14. **Pressure**: Pascal, Kilopascal, Bar, PSI, Standard Atmosphere, mmHg
15. **Energy**: Joule, Kilojoule, Calorie, Kilocalorie, Watt-hour, Kilowatt-hour, BTU
16. **Power**: Watt, Kilowatt, Horsepower, Megawatt
17. **Force**: Newton, Kilonewton, Pound-force, Dyne
18. **Frequency**: Hertz, Kilohertz, Megahertz, Gigahertz
19. **Angle**: Degree, Radian, Gradian
20. **Fuel Economy**: L/100km, MPG (US), MPG (UK), km/L
21. **Torque**: N·m, lbf·ft, kgf·m
22. **Density**: kg/m³, g/cm³, lb/ft³, lb/in³
23. **Digital Transfer Speed**: bps, kbps, Mbps, Gbps, kB/s, MB/s
24. **Cooking Measurements**: Teaspoon, Tablespoon, Cup, Fluid Ounce, Milliliter, Liter, Pinch

---

### 4. 💼 Productivity Suites
* **Indian GST Calculator**:
  * Modes: **Add GST** (Exclusive price) and **Extract GST** (Inclusive price).
  * Standard Slabs: `0%`, `5%`, `12%`, `18%`, `28%`, and Custom %.
  * Intra-State breakdown: Automatically splits GST into **CGST (50%)** and **SGST (50%)**.
  * Inter-State breakdown: Computes **IGST (100%)**.
* **Loan EMI Calculator**:
  * Inputs: Principal amount, annual interest rate (%), tenure in years or months.
  * Outputs: Monthly EMI, Total Interest payable, and Total Repayment amount.
  * Edge cases: Correctly handles 0% interest financing without division errors.
* **Everyday Math**:
  * Percentage calculator (3 interactive modes).
  * Tip & Split Bill calculator.
  * Simple & Compound Interest calculator.
  * Scientific physical constants browser ($c, h, G, N_A, k_B, e, m_e, etc.$) with one-tap copy.

---

### 5. 📜 Calculation History & Persistence
* **Room Database**: Stores calculations, timestamps, operation types, and conversion rates locally.
* **Filter & Search**: Quickly find past entries by category (All, Calculator, Currency, Unit, Tools) or keyword search.
* **Reusability**: Tap any history item to reload its expression into the live calculator.
* **Data Management**: Delete individual entries or wipe all history with a single tap.

---

### 6. ⚙️ User Settings & Personalization
* **Themes**: Dark (Samsung deep charcoal default), Light, and System theme.
* **Number Formatting**:
  * **Indian Grouping**: `₹ 1,00,000.00`
  * **International Grouping**: `$ 100,000.00`
* **Decimal Precision**: `AUTO`, `2`, `4`, `6`, `8` decimals.
* **Feedback Toggles**: Enable/disable haptic vibrations and click sounds.
* **Cache Management**: One-click currency cache clearance.

---

## 🏗️ Architecture & Technology Stack

| Layer | Component | Description |
|---|---|---|
| **Language** | Kotlin 1.9.22 | Clean, expressive, modern Android development |
| **UI Framework** | Jetpack Compose + Material 3 | Declarative, smooth, responsive design |
| **Design Pattern** | MVVM + Clean Architecture | Clear separation between UI, Domain, and Data |
| **Local Database** | Room 2.6.1 + KSP | SQLite abstraction for calculation and currency persistence |
| **Settings Storage** | DataStore Preferences 1.0.0 | Coroutine-first asynchronous key-value persistence |
| **Networking** | Retrofit 2.9.0 + OkHttp 4.12.0 | High-performance HTTP client for currency quotes |
| **JSON Serialization** | Gson | Robust parsing of currency rate schemas |
| **Concurrency** | Kotlin Coroutines & Flow | Non-blocking background evaluation and state collection |
| **Testing** | JUnit 4 | Unit testing of core arithmetic, trig, currency, and unit engines |

---

## 🧪 Automated Test Results

The application includes automated JUnit tests covering all core requirements:

```
Test Summary:
Total Tests Run: 25
Failures: 0
Ignored: 0
Success Rate: 100%
Duration: 0.149s
```

### Verified Test Suites:
1. `ArithmeticAndScientificTest`:
   * Basic operations: $2 + 3 = 5$, $10 - 4 = 6$, $6 \times 7 = 42$, $15 \div 3 = 5$.
   * Precedence & Parentheses: $2 + 3 \times 4 = 14$, $(2 + 3) \times 4 = 20$, nested brackets.
   * Safe Division by Zero error reporting.
   * Trigonometric degree mode ($\sin(90^\circ)=1$, $\cos(0^\circ)=1$, $\tan(45^\circ)=1$) and radian mode.
   * Domain boundary verification: $\sqrt{-4}$, $\log_{10}(0)$, $5! = 120$, $2^{10} = 1024$.
2. `CurrencyConversionTest`:
   * Test fixture: 10 USD at 83 INR = 830 INR.
   * Swapping inverse rate: 830 INR back to 10 USD.
   * Cross-currency conversion: EUR to INR via base USD.
   * Missing rates error detection.
   * Room cache timestamp retention under offline conditions.
3. `UnitConverterEngineTest`:
   * $1\text{ km} = 1,000\text{ m}$, $1\text{ m} = 100\text{ cm}$, $1\text{ kg} = 1,000\text{ g}$.
   * Temperature affine math: $0^\circ\text{C} = 32^\circ\text{F}$, $100^\circ\text{C} = 212^\circ\text{F}$, $0^\circ\text{C} = 273.15\text{ K}$.
   * SI vs IEC data storage: $1\text{ kB} = 1,000\text{ bytes}$ vs $1\text{ KiB} = 1,024\text{ bytes}$.
   * Numeral systems: decimal 42 to binary `101010` and hex `2A`.
   * BMI scoring, Age, and Date durations.
4. `ProductivityCalculatorsTest`:
   * Indian GST Add mode and Extract mode with CGST/SGST 50% split.
   * Loan EMI formula validation (principal ₹10,00,000 at 8.5% for 20 years = EMI ₹8,678.23).
   * 0% interest loan handling.
   * Dining tip and split bill distribution.

---

## 🚀 How to Build and Run

### Prerequisites
* **JDK**: OpenJDK 17 or 21 (configured in `gradle.properties`).
* **Android SDK**: API 34 (compileSdk / targetSdk), minSdk 26 (Android 8.0+).
* **Android Studio**: Iguana (2023.2.1) or newer.

### Build via Command Line
Run the included Gradle wrapper:

```bash
# Run all unit tests
.\gradlew.bat testDebugUnitTest

# Assemble Debug APK
.\gradlew.bat assembleDebug

# The built APK is output at:
# app/build/outputs/apk/debug/app-debug.apk
# Also mirrored at root: ./ShubDeep-Calculator-Pro.apk
```

### Install onto Connected Android Phone
Ensure USB Debugging is enabled on your Samsung Galaxy / Android smartphone:

```bash
adb install -r ShubDeep-Calculator-Pro.apk
```

---

## 🔒 Privacy & Permissions

* **No Login Required**: The app is completely open and requires no authentication or account.
* **No Analytics / Tracking**: No third-party ad networks, telemetry, or analytics SDKs are bundled.
* **Minimal Permissions**:
  * `android.permission.INTERNET`: Used strictly to query public exchange rate providers for currency rates.
  * `android.permission.ACCESS_NETWORK_STATE`: Checks whether the device is online or offline.
  * `android.permission.VIBRATE`: Provides physical haptic feedback when pressing keypad buttons.

---

## 📄 License & Attribution
Designed and built for **ShubDeep Calculator Pro**. Inspired by Samsung One UI dark aesthetics.
All rights reserved.
