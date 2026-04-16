#!/usr/bin/env powershell
# 火星殖民地应用 - PowerShell 一键安装脚本
# 使用方法: .\install_app.ps1

param(
    [switch]$SkipBuild,
    [switch]$ShowLogs
)

Write-Host "===================================" -ForegroundColor Cyan
Write-Host "火星殖民地 - 应用安装程序" -ForegroundColor Cyan
Write-Host "===================================" -ForegroundColor Cyan
Write-Host ""

# 检查是否在项目目录
if (-not (Test-Path "build.gradle")) {
    Write-Host "错误: 请在项目根目录运行此脚本！" -ForegroundColor Red
    Read-Host "按 Enter 键退出"
    exit 1
}

# 检查 ADB
$adbPath = Get-Command adb -ErrorAction SilentlyContinue
if (-not $adbPath) {
    $sdkPath = "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe"
    if (-not (Test-Path $sdkPath)) {
        Write-Host "错误: 找不到 adb 命令" -ForegroundColor Red
        Write-Host "请确保 Android SDK 已安装并配置到环境变量中" -ForegroundColor Yellow
        Read-Host "按 Enter 键退出"
        exit 1
    }
    $adb = $sdkPath
} else {
    $adb = $adbPath.Source
}

# 检查设备连接
Write-Host "[1/5] 检查设备连接..." -ForegroundColor Green
$devices = & $adb devices | Select-Object -Skip 1 | Where-Object { $_ -and -not $_.Contains("List") }

if (-not $devices) {
    Write-Host "警告: 没有检测到连接的设备！" -ForegroundColor Yellow
    Write-Host "请连接 Android 设备或启动模拟器" -ForegroundColor Yellow
    Read-Host "按 Enter 键退出"
    exit 1
}

Write-Host "找到设备: $($devices -join ', ')" -ForegroundColor Green

# 清理并构建
if (-not $SkipBuild) {
    Write-Host "[2/5] 清理项目..." -ForegroundColor Green
    & .\gradlew clean | Out-Null

    Write-Host "[3/5] 编译 APK..." -ForegroundColor Green
    & .\gradlew assembleDebug
    if ($LASTEXITCODE -ne 0) {
        Write-Host "错误: 编译失败！" -ForegroundColor Red
        Read-Host "按 Enter 键退出"
        exit 1
    }
} else {
    Write-Host "[2/5] 跳过编译（使用 -SkipBuild 参数）" -ForegroundColor Yellow
}

# 检查 APK 是否存在
$apk = "app/build/outputs/apk/debug/app-debug.apk"
if (-not (Test-Path $apk)) {
    Write-Host "错误: APK 文件不存在: $apk" -ForegroundColor Red
    Read-Host "按 Enter 键退出"
    exit 1
}

Write-Host "APK 大小: $([math]::Round((Get-Item $apk).Length / 1MB, 2)) MB" -ForegroundColor Green

# 卸载旧应用
Write-Host "[4/5] 卸载旧应用版本..." -ForegroundColor Green
& $adb uninstall com.mars.colony | Out-Null

# 安装新应用
Write-Host "[5/5] 安装新应用..." -ForegroundColor Green
$installResult = & $adb install -r $apk
if ($installResult -notmatch "Success") {
    Write-Host "错误: 安装失败！" -ForegroundColor Red
    Write-Host $installResult -ForegroundColor Yellow
    Read-Host "按 Enter 键退出"
    exit 1
}

Write-Host "应用安装成功！" -ForegroundColor Green

# 启动应用
Write-Host ""
Write-Host "===================================" -ForegroundColor Cyan
Write-Host "正在启动应用..." -ForegroundColor Cyan
Write-Host "===================================" -ForegroundColor Cyan
Write-Host ""

& $adb shell am start -n com.mars.colony/com.mars.colony.ui.MainActivity

# 显示日志
if ($ShowLogs -or $PSBoundParameters.ContainsKey('ShowLogs')) {
    Write-Host ""
    Write-Host "显示应用日志（Ctrl+C 停止）..." -ForegroundColor Green
    Write-Host ""
    & $adb logcat -s AndroidRuntime,mars.colony
} else {
    Write-Host ""
    Write-Host "应用已启动！" -ForegroundColor Green
    Write-Host "如果要查看日志，运行: .\install_app.ps1 -ShowLogs" -ForegroundColor Yellow
}
