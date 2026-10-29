@echo off
setlocal

rem Change to project directory
cd /d "%~dp0"

echo ===================================================
echo   ShubDeep Calculator Pro - Build ^& Direct Install
echo ===================================================
echo.

rem Set JAVA_HOME (custom install or fallback)
if exist "E:\GreenMind al\studio\jbr" (
    set "JAVA_HOME=E:\GreenMind al\studio\jbr"
) else if exist "C:\Program Files\Android\Android Studio\jbr" (
    set "JAVA_HOME=C:\Program Files\Android\Android Studio\jbr"
)

rem Add ADB to PATH if not already in PATH
where adb >nul 2>&1
if %errorlevel% neq 0 (
    if exist "E:\GreenMind al\android-sdk\platform-tools" (
        set "PATH=E:\GreenMind al\android-sdk\platform-tools;%PATH%"
    ) else if exist "%LOCALAPPDATA%\Android\Sdk\platform-tools" (
        set "PATH=%LOCALAPPDATA%\Android\Sdk\platform-tools;%PATH%"
    )
)

echo [DEBUG] Current directory: %cd%
echo [DEBUG] JAVA_HOME: %JAVA_HOME%
echo.

rem Check ADB again
where adb >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] ADB not found. Please install Android Platform Tools.
    pause
    exit /b 1
)

rem Check for connected Android device
echo Checking for connected Android devices...
adb devices
echo.

adb devices | findstr /R /C:"device$" >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] No authorized Android device detected.
    echo Please make sure:
    echo  1. Your phone is connected via USB.
    echo  2. USB debugging is turned ON in Developer options.
    echo  3. Tap 'Allow USB debugging' on your phone screen if prompted.
    echo.
    pause
    exit /b 1
)

echo [OK] Device detected!
echo.
echo Installing ShubDeep Calculator Pro on your phone...
echo.

adb install -r -t "ShubDeep-Calculator-Pro.apk"
if %errorlevel% neq 0 (
    echo Direct install had an issue, running full Gradle build and install...
    call gradlew.bat installDebug
)

if %errorlevel% equ 0 (
    echo.
    echo ===================================================
    echo  [SUCCESS] App installed on your phone successfully!
    echo ===================================================
    echo Launching app...
    adb shell am start -n com.shubdeep.calculatorpro.debug/com.shubdeep.calculatorpro.MainActivity
) else (
    echo.
    echo [ERROR] Installation failed. Check the error messages above.
)

echo.
pause
endlocal
