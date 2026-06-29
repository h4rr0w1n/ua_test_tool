@echo off
:: ============================================================
:: run.bat  —  AMHS UA Test Tool
::
:: Recommended workflow:
::   1) Build once with install-and-build.bat
::   2) Run the self-contained dist\ package: dist\run.bat
::   3) If run from the project root, this wrapper forwards into dist\
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
if "%SCRIPT_DIR:~-1%"=="\" set "SCRIPT_DIR=%SCRIPT_DIR:~0,-1%"

:: Forward root-level execution into dist\ when available.
if not exist "%SCRIPT_DIR%\ua-test-tool.jar" (
    if exist "%SCRIPT_DIR%\dist\ua-test-tool.jar" (
        set "SCRIPT_DIR=%SCRIPT_DIR%\dist"
    )
)

cd /d "%SCRIPT_DIR%"

:: ---- Locate the JAR in dist\ or current directory ----
set "JAR_FILE=%SCRIPT_DIR%\ua-test-tool.jar"

:: Native lib dir lives beside this script in dist\lib\
set "ISODE_LIB_DIR=%SCRIPT_DIR%\lib"

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
    echo WARNING: Compiled JAR not found.
    echo Expected at: %JAR_FILE%
    echo Attempting to build project automatically using Maven...
    echo.
    call mvn clean package
    if errorlevel 1 (
        echo ERROR: Maven build failed. Please install Maven or check errors.
        pause
        exit /b 1
    )
    copy /Y "%SCRIPT_DIR%\target\ua-test-tool-1.0.0.jar" "%JAR_FILE%" >nul
    xcopy /Y /S /I "%SCRIPT_DIR%\target\lib\*" "%ISODE_LIB_DIR%\" >nul
    echo Build successful.
    echo.
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
