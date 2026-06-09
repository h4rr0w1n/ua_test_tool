@echo off
:: ============================================================
:: run.bat  —  AMHS UA Test Tool
::
:: Works in two modes automatically:
::   DIST mode  : run from a dist\ folder (ua-test-tool.jar beside this script)
::   DEV mode   : run from the project root (uses target\ JAR)
::
:: Target machine needs ONLY Java 8+  — no Maven, no .m2
:: ============================================================

title AMHS UA Test Tool

echo ==========================================================
echo          AMHS UA Test Tool
echo ==========================================================
echo.

setlocal enabledelayedexpansion

set "SCRIPT_DIR=%~dp0"
cd /d "%SCRIPT_DIR%"

:: ---- Locate the JAR (dist mode first, then dev mode) ------
set "JAR_FILE=%SCRIPT_DIR%ua-test-tool.jar"
if not exist "%JAR_FILE%" (
    set "JAR_FILE=%SCRIPT_DIR%target\ua-test-tool-1.0.0-jar-with-dependencies.jar"
)

:: Native lib dir lives beside this script in dist\lib\,
:: or in the project root lib\ for dev mode.
set "ISODE_LIB_DIR=%SCRIPT_DIR%lib"

:: ============================================================
:: JAVA AUTO-DETECTION
:: ============================================================

if not "%JAVA_HOME%"=="" (
    echo Using existing JAVA_HOME: %JAVA_HOME%
    goto :java_found
)

for /f "delims=" %%i in ('where java 2^>nul') do (
    set "JAVA_PATH=%%i"
    for %%j in ("%%i") do set "JAVA_BIN_DIR=%%~dpj"
    for %%k in ("!JAVA_BIN_DIR!..") do set "JAVA_HOME=%%~fk"
    echo Auto-detected JAVA_HOME from PATH: !JAVA_HOME!
    goto :java_found
)

for %%B in (
    "C:\Program Files\Java"
    "C:\Program Files (x86)\Java"
    "C:\Program Files\Eclipse Adoptium"
    "C:\Program Files\Microsoft"
    "C:\Program Files\Amazon Corretto"
    "C:\Program Files\Azul"
    "C:\Program Files\BellSoft"
    "C:\Program Files\ojdkbuild"
    "C:\Program Files (x86)\ojdkbuild"
) do (
    if exist "%%~B" (
        for /d %%d in ("%%~B\jdk*" "%%~B\jre*" "%%~B\java-*" "%%~B\openjdk*") do (
            if exist "%%~fd\bin\java.exe" (
                set "JAVA_HOME=%%~fd"
                echo Auto-detected JAVA_HOME: !JAVA_HOME!
                goto :java_found
            )
        )
    )
)

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

echo ERROR: Java is not installed or not found in PATH.
echo Please install Java 8 or higher.
echo.
pause
exit /b 1

:java_found
set "PATH=!JAVA_HOME!\bin;%PATH%"

if not exist "!JAVA_HOME!\bin\java.exe" (
    echo ERROR: java.exe not found at !JAVA_HOME!\bin\java.exe
    echo Please reinstall Java 8 or update JAVA_HOME.
    echo.
    pause
    exit /b 1
)

echo.
echo Java version:
java -version
echo.

:: ============================================================
:: CHECK JAR
:: ============================================================

if not exist "%JAR_FILE%" (
    echo ERROR: Compiled JAR not found.
    echo Expected at: %JAR_FILE%
    echo.
    echo Please run install-and-build.bat first.
    echo.
    pause
    exit /b 1
)

:: ============================================================
:: LAUNCH
:: ============================================================

echo Starting AMHS UA Test Tool...
echo JAR : %JAR_FILE%
if exist "%ISODE_LIB_DIR%" (
    echo LIBS: %ISODE_LIB_DIR%
)
echo ==========================================================
echo.

if exist "%ISODE_LIB_DIR%" (
    java -Disode.bindir="%ISODE_LIB_DIR%" -Djava.library.path="%ISODE_LIB_DIR%" -jar "%JAR_FILE%"
) else (
    java -jar "%JAR_FILE%"
)

if %ERRORLEVEL% neq 0 (
    echo.
    echo Application exited with error code %ERRORLEVEL%.
    pause
)

endlocal
