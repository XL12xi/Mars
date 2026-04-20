@echo off
REM Build, install, and launch the Android debug app.

cd /d "%~dp0"

echo === Mars Colony App Installer ===

if not exist "build.gradle" (
    echo Error: run this script from the project root.
    pause
    exit /b 1
)

where adb >nul 2>nul
if %errorlevel% neq 0 (
    echo Error: adb was not found. Add Android SDK platform-tools to PATH.
    pause
    exit /b 1
)

echo [1/5] Checking connected device...
adb devices | find /V "List"
adb get-state >nul 2>nul
if %errorlevel% neq 0 (
    echo Error: no Android device or emulator detected.
    pause
    exit /b 1
)

echo [2/5] Cleaning project...
call gradlew clean >nul 2>nul

echo [3/5] Building debug APK...
call gradlew assembleDebug
if %errorlevel% neq 0 (
    echo Error: build failed.
    pause
    exit /b 1
)

echo [4/5] Uninstalling previous app if present...
adb uninstall com.mars.colony >nul 2>nul

echo [5/5] Installing debug APK...
adb install -r app\build\outputs\apk\debug\app-debug.apk
if %errorlevel% neq 0 (
    echo Error: install failed.
    pause
    exit /b 1
)

echo Launching app...
adb shell am start -n com.mars.colony/com.mars.colony.ui.MainActivity

echo App launched. Press Ctrl+C to stop logcat.
adb logcat -s AndroidRuntime,mars.colony

pause
