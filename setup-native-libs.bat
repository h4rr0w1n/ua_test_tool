@echo off
REM Windows Setup Script for Isode X.400 Native Libraries
REM This script helps setup the required native DLL files for Isode X.400

title AMHS UA Test Tool - Native Library Setup

echo ==========================================================
echo      AMHS UA Test Tool - Native Library Setup
echo ==========================================================
echo.

setlocal enabledelayedexpansion

REM Get the directory where this script is located
set SCRIPT_DIR=%~dp0
set LIB_DIR=%SCRIPT_DIR%lib
set AMDDIR=%LIB_DIR%\amd64

REM Check if lib directory exists
if not exist "%LIB_DIR%" (
    echo Creating lib directory at: %LIB_DIR%
    mkdir "%LIB_DIR%"
    echo.
)

REM Check if amd64 directory exists
if not exist "%AMDDIR%" (
    echo Creating lib\amd64 directory for 64-bit native libraries
    mkdir "%AMDDIR%"
    echo.
)

echo.
echo Native Library Setup Instructions:
echo ==========================================================
echo.
echo The Isode X.400 library requires the following native DLL files:
echo.
echo Required 64-bit DLLs (for lib\amd64\):
echo   - pthreadvc2.dll
echo   - CJavaInterface.dll
echo.
echo If you have the Isode X.400 SDK installed, copy the DLL files from:
echo   C:\Program Files\Isode\lib\amd64\ (or your installation path)
echo.
echo Alternative locations to check:
echo   - Your Isode X.400 installation directory
echo   - The SDK package you received from Isode
echo.
echo Current setup directories:
echo   - 64-bit libraries: %AMDDIR%
echo   - All libraries: %LIB_DIR%
echo.
echo Steps to complete setup:
echo   1. Locate your Isode X.400 SDK installation
echo   2. Copy pthreadvc2.dll and CJavaInterface.dll to: %AMDDIR%
echo   3. Alternatively, place them in: %LIB_DIR%
echo   4. Run: mvn clean package
echo   5. Run: run.bat
echo.
echo ==========================================================
echo.

REM Check if the DLLs exist
if exist "%AMDDIR%\pthreadvc2.dll" (
    echo Found: pthreadvc2.dll in lib\amd64
) else (
    echo Missing: pthreadvc2.dll - Please copy it to lib\amd64
)

if exist "%AMDDIR%\CJavaInterface.dll" (
    echo Found: CJavaInterface.dll in lib\amd64
) else (
    echo Missing: CJavaInterface.dll - Please copy it to lib\amd64
)

if exist "%LIB_DIR%\pthreadvc2.dll" (
    echo Found: pthreadvc2.dll in lib
) else (
    echo Missing: pthreadvc2.dll - Please copy it to lib
)

if exist "%LIB_DIR%\CJavaInterface.dll" (
    echo Found: CJavaInterface.dll in lib
) else (
    echo Missing: CJavaInterface.dll - Please copy it to lib
)

echo.
echo For more information, see: README.md and TEST_FRAMEWORK_README.md
echo.
pause
exit /b 0
