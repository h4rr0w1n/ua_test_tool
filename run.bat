@echo off
:: Execution script for AMHS UA Test Tool on Windows
:: This script checks dependencies, compiles the app if needed, and launches the UI.

title AMHS UA Test Tool - Runner

echo ==========================================================
echo               AMHS UA Test Tool - Runner                  
echo ==========================================================
echo.

setlocal enabledelayedexpansion

:: Get current directory
set "SCRIPT_DIR=%~dp0"
cd /d "%SCRIPT_DIR%"

:: Define target paths outside of IF blocks to avoid parenthesis syntax errors
set "OJDK_SEARCH_DIR=C:\Program Files (x86)\ojdkbuild"
set "NB_MAVEN=C:\Program Files (x86)\NetBeans 8.2\java\maven\bin"
set "JAR_FILE=%SCRIPT_DIR%target\ua-test-tool-1.0.0-jar-with-dependencies.jar"
set "ISODE_LIB_DIR=lib"

:: 1. JAVA HOME AND JAVA PATH AUTO-DETECTION
if "%JAVA_HOME%"=="" (
    :: Try to find via 'where java'
    for /f "delims=" %%i in ('where java 2^>nul') do (
        set "JAVA_PATH=%%i"
        :: Get folder of java.exe (bin)
        for %%j in ("!JAVA_PATH!") do set "JAVA_BIN_DIR=%%~dpj"
        :: Get parent of bin (grandparent of java.exe)
        for %%k in ("!JAVA_BIN_DIR!..") do set "JAVA_HOME=%%~fk"
        echo Auto-detected JAVA_HOME from PATH: !JAVA_HOME!
        goto :java_done
    )
    
    :: Try ojdkbuild standard folder
    if exist "!OJDK_SEARCH_DIR!" (
        for /d %%d in ("!OJDK_SEARCH_DIR!\java-1.8.0*") do (
            set "JAVA_HOME=%%~fd"
            set "PATH=!JAVA_HOME!\bin;%PATH%"
            echo Auto-detected JAVA_HOME from ojdkbuild: !JAVA_HOME!
            goto :java_done
        )
    )
) else (
    echo Using existing JAVA_HOME: %JAVA_HOME%
    :: Ensure bin is in path
    set "PATH=%JAVA_HOME%\bin;%PATH%"
)

:java_done

:: Verify Java is available
where java >nul 2>nul
if %ERRORLEVEL% neq 0 (
    echo ERROR: Java is not installed or not available in your PATH.
    echo Please install Java 8 or higher.
    echo.
    pause
    exit /b 1
)

:: Display Java version for confirmation
echo.
echo Java version:
java -version
echo.

:: 2. MAVEN AUTO-DETECTION
where mvn >nul 2>nul
if %ERRORLEVEL% neq 0 (
    if exist "!NB_MAVEN!\mvn.bat" (
        set "PATH=!NB_MAVEN!;%PATH%"
        echo Auto-detected Maven from NetBeans: !NB_MAVEN!
    ) else (
        echo ERROR: Maven is not installed or not available in your PATH.
        echo Please install Apache Maven first: https://maven.apache.org/download.cgi
        echo.
        pause
        exit /b 1
    )
)

:: Check if the compiled JAR exists, otherwise trigger a Maven build
if not exist "%JAR_FILE%" (
    echo.
    echo Compiled JAR not found. Building the application...
    echo.
    
    :: Check if lib directory exists
    if not exist "%SCRIPT_DIR%lib" (
        echo WARNING: lib directory not found.
        echo The required Maven libraries may not be installed.
        echo Please run install-libs.bat first if you have the dependency JAR files.
        echo.
        echo Attempting to build anyway...
        echo.
    )
    
    :: Build using Maven package
    call mvn clean package
    if !ERRORLEVEL! neq 0 (
        echo.
        echo ERROR: Maven build failed^!
        echo Please ensure all required dependencies are installed.
        echo If you have the Isode/ATTech libraries, run: install-libs.bat
        echo.
        pause
        exit /b 1
    )
    echo.
    echo Build successful^!
    echo.
)

echo Starting AMHS UA Test Tool UI...
echo ==========================================================
echo.

:: Run the application with appropriate library path if lib exists
if exist "%ISODE_LIB_DIR%" (
    echo Found Isode native library path: "%ISODE_LIB_DIR%"
    java -Djava.library.path="%ISODE_LIB_DIR%" -jar "%JAR_FILE%"
) else (
    echo WARNING: Isode native library path not found: "%ISODE_LIB_DIR%"
    echo Please ensure the native libraries are placed in the lib directory.
    echo Running with default JVM settings...
    echo.
    java -jar "%JAR_FILE%"
)

if %ERRORLEVEL% neq 0 (
    echo.
    echo Application exited with code %ERRORLEVEL%.
    pause
)

endlocal
