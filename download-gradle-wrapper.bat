@echo off
REM Download gradle-wrapper.jar for Gradle 8.0

setlocal enabledelayedexpansion

set GRADLE_VERSION=8.0
set WRAPPER_JAR=gradle\wrapper\gradle-wrapper.jar
set DOWNLOAD_URL=https://services.gradle.org/distributions/gradle-8.0-wrapper.jar

echo Downloading Gradle %GRADLE_VERSION% wrapper JAR...
echo From: %DOWNLOAD_URL%

REM Create the wrapper directory if it doesn't exist
if not exist gradle\wrapper mkdir gradle\wrapper

REM Try using PowerShell to download (more reliable)
powershell -Command "try { [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; (New-Object System.Net.WebClient).DownloadFile('%DOWNLOAD_URL%', '%WRAPPER_JAR%'); Write-Host 'Download completed successfully'; exit 0 } catch { Write-Host 'Download failed'; exit 1 }"

if %ERRORLEVEL% equ 0 (
    echo.
    echo ✓ gradle-wrapper.jar downloaded successfully!
    echo You can now run: .\gradlew.bat build
) else (
    echo.
    echo ✗ Download failed. You may need to:
    echo 1. Download manually from https://services.gradle.org/distributions/gradle-8.0-wrapper.jar
    echo 2. Place the JAR in gradle\wrapper\gradle-wrapper.jar
    exit /b 1
)

endlocal
