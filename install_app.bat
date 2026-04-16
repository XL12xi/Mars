@echo off
REM 火星殖民地应用 - 一键安装脚本
REM 使用方法: 将此文件放在项目根目录，双击运行

echo.
echo ===================================
echo 火星殖民地 - 应用安装程序
echo ===================================
echo.

REM 检查是否在项目目录
if not exist "build.gradle" (
    echo 错误: 请在项目根目录运行此脚本！
    pause
    exit /b 1
)

REM 检查 ADB
where adb >nul 2>nul
if %errorlevel% neq 0 (
    echo 错误: 找不到 adb 命令
    echo 请确保 Android SDK 已安装并配置到环境变量中
    pause
    exit /b 1
)

REM 检查设备连接
echo [1/5] 检查设备连接...
adb devices | find /V "List"
adb get-state >nul 2>nul
if %errorlevel% neq 0 (
    echo 警告: 没有检测到连接的设备！
    echo 请连接 Android 设备或启动模拟器
    pause
    exit /b 1
)

REM 清理并构建
echo [2/5] 清理项目...
call gradlew clean >nul 2>nul

echo [3/5] 编译 APK...
call gradlew assembleDebug
if %errorlevel% neq 0 (
    echo 错误: 编译失败！
    pause
    exit /b 1
)

REM 卸载旧应用
echo [4/5] 卸载旧应用版本...
adb uninstall com.mars.colony >nul 2>nul

REM 安装新应用
echo [5/5] 安装新应用...
adb install -r app\build\outputs\apk\debug\app-debug.apk
if %errorlevel% neq 0 (
    echo 错误: 安装失败！
    pause
    exit /b 1
)

REM 启动应用
echo.
echo ===================================
echo 正在启动应用...
echo ===================================
echo.

adb shell am start -n com.mars.colony/com.mars.colony.ui.MainActivity

REM 显示实时日志
echo.
echo 应用已启动！按 Ctrl+C 停止查看日志
echo.
adb logcat -s AndroidRuntime,mars.colony

pause
