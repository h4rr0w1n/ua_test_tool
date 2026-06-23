@echo off
:: ============================================================
:: run.bat  -  AMHS UA Test Tool
::
:: This script will automatically compile the project using Maven
:: if the target JAR does not exist, and then launch it.
:: ============================================================

title AMHS UA Test Tool

setlocal enabledelayedexpansion

set "SCRIPT_DIR=%~dp0"
if "%SCRIPT_DIR:~-1%"=="\" set "SCRIPT_DIR=%SCRIPT_DIR:~0,-1%"
cd /d "%SCRIPT_DIR%"

set "JAR_FILE=%SCRIPT_DIR%\target\ua-test-tool-1.0.0-jar-with-dependencies.jar"
set "ISODE_LIB_DIR=%SCRIPT_DIR%\libs"

if not exist "%JAR_FILE%" (
    echo ==========================================================
    echo Building the project with Maven...
    echo ==========================================================
    call mvn clean package -DskipTests
    if !ERRORLEVEL! neq 0 (
        echo.
        echo ERROR: Maven build failed!
        pause
        exit /b 1
    )
)

echo.
echo ==========================================================
echo Starting AMHS UA Test Tool...
echo ==========================================================
echo JAR : %JAR_FILE%
echo LIBS: %ISODE_LIB_DIR%
echo ==========================================================
echo.

java -Disode.bindir="%ISODE_LIB_DIR%" -Djava.library.path="%ISODE_LIB_DIR%" -jar "%JAR_FILE%"

if %ERRORLEVEL% neq 0 (
    echo.
    echo Application exited with error code %ERRORLEVEL%.
    pause
)

endlocal
