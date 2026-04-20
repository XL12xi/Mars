#!/usr/bin/env powershell
# Build, install, and launch the Mars Colony Android debug app.

param(
    [switch]$SkipBuild,
    [switch]$ShowLogs
)

Write-Host "===================================" -ForegroundColor Cyan
Write-Host "Mars Colony App Installer" -ForegroundColor Cyan
Write-Host "===================================" -ForegroundColor Cyan
Write-Host ""

if (-not (Test-Path "build.gradle")) {
    Write-Host "Error: run this script from the project root." -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

$adbPath = Get-Command adb -ErrorAction SilentlyContinue
if (-not $adbPath) {
    $sdkPath = "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe"
    if (-not (Test-Path $sdkPath)) {
        Write-Host "Error: adb was not found." -ForegroundColor Red
        Write-Host "Install Android SDK platform-tools or add adb to PATH." -ForegroundColor Yellow
        Read-Host "Press Enter to exit"
        exit 1
    }
    $adb = $sdkPath
} else {
    $adb = $adbPath.Source
}

Write-Host "[1/5] Checking connected device..." -ForegroundColor Green
$devices = & $adb devices | Select-Object -Skip 1 | Where-Object { $_ -and -not $_.Contains("List") }

if (-not $devices) {
    Write-Host "Error: no Android device or emulator detected." -ForegroundColor Yellow
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host "Device found: $($devices -join ', ')" -ForegroundColor Green

if (-not $SkipBuild) {
    Write-Host "[2/5] Cleaning project..." -ForegroundColor Green
    & .\gradlew clean | Out-Null

    Write-Host "[3/5] Building debug APK..." -ForegroundColor Green
    & .\gradlew assembleDebug
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Error: build failed." -ForegroundColor Red
        Read-Host "Press Enter to exit"
        exit 1
    }
} else {
    Write-Host "[2/5] Skipping build because -SkipBuild was used." -ForegroundColor Yellow
}

$apk = "app/build/outputs/apk/debug/app-debug.apk"
if (-not (Test-Path $apk)) {
    Write-Host "Error: APK file was not found: $apk" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host "APK size: $([math]::Round((Get-Item $apk).Length / 1MB, 2)) MB" -ForegroundColor Green

Write-Host "[4/5] Uninstalling previous app if present..." -ForegroundColor Green
& $adb uninstall com.mars.colony | Out-Null

Write-Host "[5/5] Installing debug APK..." -ForegroundColor Green
$installResult = & $adb install -r $apk
if ($installResult -notmatch "Success") {
    Write-Host "Error: install failed." -ForegroundColor Red
    Write-Host $installResult -ForegroundColor Yellow
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host "App installed successfully." -ForegroundColor Green
& $adb shell am start -n com.mars.colony/com.mars.colony.ui.MainActivity

if ($ShowLogs -or $PSBoundParameters.ContainsKey('ShowLogs')) {
    Write-Host "Showing app logs. Press Ctrl+C to stop." -ForegroundColor Green
    & $adb logcat -s AndroidRuntime,mars.colony
} else {
    Write-Host "App launched. Run .\install_app.ps1 -ShowLogs to view logs." -ForegroundColor Green
}
