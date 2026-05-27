@echo off
:: Run script for AMHS UA Test Tool on Windows
:: This script launches the pre-built JAR with proper settings.
:: Use build.bat first to compile the tool, then use this script to run it.

title AMHS UA Test Tool - Runner

echo ==========================================================
echo            AMHS UA Test Tool - Run Script                
echo ==========================================================
echo.

setlocal enabledelayedexpansion

:: Get current directory
set "SCRIPT_DIR=%~dp0"
cd /d "%SCRIPT_DIR%"

:: Define target paths
set "JAR_FILE=%SCRIPT_DIR%target\ua-test-tool-1.0.0-jar-with-dependencies.jar"
set "ISODE_LIB_DIR=lib"

:: 1. JAVA HOME AND JAVA PATH AUTO-DETECTION
if not "%JAVA_HOME%"=="" (
    echo Using existing JAVA_HOME: %JAVA_HOME%
    goto :java_found
)

:: Try to find via 'where java' first
for /f "delims=" %%i in ('where java 2^>nul') do (
    set "JAVA_PATH=%%i"
    for %%j in ("%%i") do set "JAVA_BIN_DIR=%%~dpj"
    for %%k in ("!JAVA_BIN_DIR!..") do set "JAVA_HOME=%%~fk"
    echo Auto-detected JAVA_HOME from PATH: !JAVA_HOME!
    goto :java_found
)

:: Search common Java install directories for any JDK/JRE version
for %%B in ("C:\Program Files\Java" "C:\Program Files (x86)\Java" "C:\Program Files\Eclipse Adoptium" "C:\Program Files\Microsoft" "C:\Program Files\Amazon Corretto" "C:\Program Files\Azul" "C:\Program Files\BellSoft" "C:\Program Files\ojdkbuild" "C:\Program Files (x86)\ojdkbuild") do (
    if exist "%%~B" (
        for /d %%d in ("%%~B\jdk*" "%%~B\jre*" "%%~B\java-*" "%%~B\openjdk*") do (
            if exist "%%~fd\bin\java.exe" (
                set "JAVA_HOME=%%~fd"
                echo Auto-detected JAVA_HOME from install directory: !JAVA_HOME!
                goto :java_found
            )
        )
    )
)

:: Last resort: check registry for JDK CurrentVersion
for /f "tokens=2*" %%a in ('reg query "HKLM\SOFTWARE\JavaSoft\Java Development Kit" /v CurrentVersion 2^>nul') do (
    set "JDK_VER=%%b"
    for /f "tokens=2*" %%c in ('reg query "HKLM\SOFTWARE\JavaSoft\Java Development Kit\!JDK_VER!" /v JavaHome 2^>nul') do (
        set "JAVA_HOME=%%d"
        echo Auto-detected JAVA_HOME from registry: !JAVA_HOME!
        goto :java_found
    )
)
for /f "tokens=2*" %%a in ('reg query "HKLM\SOFTWARE\JavaSoft\JDK" /v CurrentVersion 2^>nul') do (
    set "JDK_VER=%%b"
    for /f "tokens=2*" %%c in ('reg query "HKLM\SOFTWARE\JavaSoft\JDK\!JDK_VER!" /v JavaHome 2^>nul') do (
        set "JAVA_HOME=%%d"
        echo Auto-detected JAVA_HOME from registry (JDK): !JAVA_HOME!
        goto :java_found
    )
)

echo ERROR: Java is not installed or not available in your PATH.
echo Please install Java 8 or higher.
echo.
pause
exit /b 1

:java_found
:: Always prepend detected JAVA_HOME\bin to PATH so the correct java.exe is used
set "PATH=!JAVA_HOME!\bin;%PATH%"

:java_done

:: Verify Java is actually executable
if not exist "!JAVA_HOME!\bin\java.exe" (
    echo ERROR: java.exe not found at !JAVA_HOME!\bin\java.exe
    echo Please reinstall Java 8 or higher, or update JAVA_HOME.
    echo.
    pause
    exit /b 1
)

:: Display Java version for confirmation
echo.
echo Java version:
java -version
echo.

:: 2. CHECK IF JAR FILE EXISTS
if not exist "%JAR_FILE%" (
    echo.
    echo ERROR: Compiled JAR not found at:
    echo %JAR_FILE%
    echo.
    echo Please run build.bat first to compile the application.
    echo.
    pause
    exit /b 1
)

echo.
echo Starting AMHS UA Test Tool...
echo ==========================================================
echo.

:: 3. RUN THE APPLICATION
if exist "%ISODE_LIB_DIR%" (
    echo Found Isode native library path: "%ISODE_LIB_DIR%"
    java -Disode.bindir="%SCRIPT_DIR%lib" -Djava.library.path="%ISODE_LIB_DIR%" -jar "%JAR_FILE%"
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
