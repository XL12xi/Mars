@echo off
REM ========================================
REM 火星殖民地 - 技能升级系统编译脚本
REM ========================================

setlocal enabledelayedexpansion

cd /d "%~dp0"

echo.
echo ╔════════════════════════════════════════════╗
echo ║   编译技能升级系统                         ║
echo ╚════════════════════════════════════════════╝
echo.

REM 创建bin目录
if not exist bin mkdir bin
echo [✓] 输出目录: bin\

REM 编译各个包
echo.
echo [1/5] 编译 model 包...
cd src\main\java
javac -d ..\..\..\bin -encoding UTF-8 com\mars\colony\model\*.java
if !errorlevel! neq 0 (
    echo [✗] model 编译失败
    cd /d "%~dp0"
    exit /b 1
)
echo [✓] model 编译成功

echo [2/5] 编译 ability 包...
javac -d ..\..\..\bin -cp ..\..\..\bin -encoding UTF-8 com\mars\colony\ability\*.java
if !errorlevel! neq 0 (
    echo [✗] ability 编译失败
    cd /d "%~dp0"
    exit /b 1
)
echo [✓] ability 编译成功

echo [3/5] 编译 upgrade 包...
javac -d ..\..\..\bin -cp ..\..\..\bin -encoding UTF-8 com\mars\colony\upgrade\*.java
if !errorlevel! neq 0 (
    echo [✗] upgrade 编译失败
    cd /d "%~dp0"
    exit /b 1
)
echo [✓] upgrade 编译成功

echo [4/5] 编译 game 包...
javac -d ..\..\..\bin -cp ..\..\..\bin -encoding UTF-8 com\mars\colony\game\*.java
if !errorlevel! neq 0 (
    echo [✗] game 编译失败
    cd /d "%~dp0"
    exit /b 1
)
echo [✓] game 编译成功

echo [5/5] 编译根目录和演示程序...
javac -d ..\..\..\bin -cp ..\..\..\bin -encoding UTF-8 com\mars\colony\*.java
if !errorlevel! neq 0 (
    echo [✗] 演示程序编译失败
    cd /d "%~dp0"
    exit /b 1
)
echo [✓] 演示程序编译成功

cd /d "%~dp0"

echo.
echo ╔════════════════════════════════════════════╗
echo ║   编译完成！                               ║
echo ╚════════════════════════════════════════════╝
echo.

echo 选择要运行的程序:
echo.
echo [1] UpgradeSystemDemo     - 升级系统演示
echo [2] UpgradeSystemTest     - 升级系统单元测试
echo [3] GameDemo              - 游戏基础演示
echo [4] 查看帮助
echo [Q] 退出
echo.

set /p choice=请选择 (1-4, Q): 

if "%choice%"=="1" (
    echo.
    echo 运行 UpgradeSystemDemo...
    echo ════════════════════════════════════════════
    java -cp bin com.mars.colony.UpgradeSystemDemo
) else if "%choice%"=="2" (
    echo.
    echo 运行 UpgradeSystemTest...
    echo ════════════════════════════════════════════
    java -cp bin com.mars.colony.UpgradeSystemTest
) else if "%choice%"=="3" (
    echo.
    echo 运行 GameDemo...
    echo ════════════════════════════════════════════
    java -cp bin com.mars.colony.GameDemo
) else if /i "%choice%"=="4" (
    echo.
    echo 编译命令:
    echo   javac -d bin -encoding UTF-8 ^
    echo     src\main\java\com\mars\colony\model\*.java ^
    echo     src\main\java\com\mars\colony\ability\*.java ^
    echo     src\main\java\com\mars\colony\upgrade\*.java ^
    echo     src\main\java\com\mars\colony\game\*.java ^
    echo     src\main\java\com\mars\colony\*.java
    echo.
    echo 运行演示:
    echo   java -cp bin com.mars.colony.UpgradeSystemDemo
    echo   java -cp bin com.mars.colony.UpgradeSystemTest
    echo.
) else if /i "%choice%"=="Q" (
    echo 退出
) else (
    echo 无效选择
)

pause
