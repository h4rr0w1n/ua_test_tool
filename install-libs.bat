@echo off
:: Windows Library Installation Batch Wrapper for AMHS UA Test Tool
:: This script runs the PowerShell installer with bypassed execution policies.

title AMHS UA Test Tool - Windows Library Installer

echo ==========================================================
echo          AMHS UA Test Tool - Windows Installation
echo ==========================================================
echo.

:: Check for Maven in PATH
where mvn >nul 2>nul
if %ERRORLEVEL% neq 0 (
    echo ERROR: Maven (mvn) is not installed or not available in your PATH.
    echo Please install Apache Maven first: https://maven.apache.org/download.cgi
    echo.
    pause
    exit /b 1
)

:: Check for Java in PATH
where java >nul 2>nul
if %ERRORLEVEL% neq 0 (
    echo ERROR: Java is not installed or not available in your PATH.
    echo Please install Java 8 or higher.
    echo.
    pause
    exit /b 1
)

echo Launching PowerShell installation script...
echo.

:: Execute the PowerShell script bypassing Execution Policies
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0install-libs.ps1"
set PS_EXIT_CODE=%ERRORLEVEL%

echo.
if %PS_EXIT_CODE% neq 0 (
    echo ERROR: Installation failed or completed with errors (Exit Code: %PS_EXIT_CODE%).
    echo Please resolve any errors above and try again.
    echo.
    pause
    exit /b %PS_EXIT_CODE%
)

echo Library installation completed successfully!
echo To run the application, double-click or run: run.bat
echo.
pause
exit /b 0
