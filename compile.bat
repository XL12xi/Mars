@echo off
REM Console Java compile helper for the Mars Colony source model.

setlocal enabledelayedexpansion
cd /d "%~dp0"

if not exist bin mkdir bin

echo === Compiling Mars Colony Java sources ===

cd src\main\java

echo [1/5] Compiling model package...
javac -d ..\..\..\bin -encoding UTF-8 com\mars\colony\model\*.java
if !errorlevel! neq 0 goto :fail

echo [2/5] Compiling ability package...
javac -d ..\..\..\bin -cp ..\..\..\bin -encoding UTF-8 com\mars\colony\ability\*.java
if !errorlevel! neq 0 goto :fail

echo [3/5] Compiling upgrade package...
javac -d ..\..\..\bin -cp ..\..\..\bin -encoding UTF-8 com\mars\colony\upgrade\*.java
if !errorlevel! neq 0 goto :fail

echo [4/5] Compiling game package...
javac -d ..\..\..\bin -cp ..\..\..\bin -encoding UTF-8 com\mars\colony\game\*.java
if !errorlevel! neq 0 goto :fail

echo [5/5] Compiling console demos...
javac -d ..\..\..\bin -cp ..\..\..\bin -encoding UTF-8 com\mars\colony\*.java
if !errorlevel! neq 0 goto :fail

cd /d "%~dp0"
echo Compile complete.
echo.
echo Available demos:
echo [1] UpgradeSystemTest
echo [2] InteractiveBattleDemo
echo [3] GlobalCrystalSystemDemo
echo [Q] Quit
echo.

set /p choice=Choose an option:

if "%choice%"=="1" (
    java -cp bin com.mars.colony.UpgradeSystemTest
) else if "%choice%"=="2" (
    java -cp bin com.mars.colony.InteractiveBattleDemo
) else if "%choice%"=="3" (
    java -cp bin com.mars.colony.GlobalCrystalSystemDemo
) else (
    echo Done.
)

pause
exit /b 0

:fail
cd /d "%~dp0"
echo Compile failed.
pause
exit /b 1
