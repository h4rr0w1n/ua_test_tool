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

:: 2. MAVEN AUTO-DETECTION
where mvn 2>nul
if %ERRORLEVEL% equ 0 goto :mvn_done
where mvn.cmd 2>nul
if %ERRORLEVEL% equ 0 goto :mvn_done
where mvn.bat 2>nul
if %ERRORLEVEL% equ 0 goto :mvn_done
if not "%M2_HOME%"=="" (
    if exist "%M2_HOME%\bin\mvn.cmd" (
        set "PATH=%M2_HOME%\bin;!PATH!"
        echo Auto-detected Maven from M2_HOME: %M2_HOME%
        goto :mvn_done
    )
)
if not "%MAVEN_HOME%"=="" (
    if exist "%MAVEN_HOME%\bin\mvn.cmd" (
        set "PATH=%MAVEN_HOME%\bin;!PATH!"
        echo Auto-detected Maven from MAVEN_HOME: %MAVEN_HOME%
        goto :mvn_done
    )
)

:: Search common Maven install directories (any version, any distribution)
for %%B in (
    "C:\Program Files\Apache Software Foundation",
    "C:\Program Files (x86)\Apache Software Foundation",
    "C:\Program Files\Maven",
    "C:\tools\maven",
    "C:\ProgramData\chocolatey\lib",
    "C:\ProgramData\chocolatey\lib\maven",
    "C:\ProgramData\chocolatey\lib\maven\tools",
    "C:\Users\%USERNAME%\scoop\apps",
    "C:\Users\%USERNAME%\.sdkman\candidates\maven"
) do (
    if exist "%%~B" (
        for /d %%d in ("%%~B\maven*" "%%~B\apache-maven*" "%%~B\maven\*" "%%~B\current") do (
            if exist "%%~fd\bin\mvn.cmd" (
                set "MAVEN_HOME=%%~fd"
                set "PATH=%%~fd\bin;!PATH!"
                echo Auto-detected Maven from install directory: %%~fd
                goto :mvn_done
            )
            if exist "%%~fd\bin\mvn.bat" (
                set "MAVEN_HOME=%%~fd"
                set "PATH=%%~fd\bin;!PATH!"
                echo Auto-detected Maven from install directory: %%~fd
                goto :mvn_done
            )
        )
    )
)

:: Search NetBeans bundled Maven (any NetBeans version)
for %%B in ("C:\Program Files (x86)\NetBeans*" "C:\Program Files\NetBeans*") do (
    for /d %%d in ("%%~B") do (
        if exist "%%~fd\java\maven\bin\mvn.bat" (
            set "MAVEN_HOME=%%~fd\java\maven"
            set "PATH=%%~fd\java\maven\bin;!PATH!"
            echo Auto-detected Maven from NetBeans: %%~fd\java\maven
            goto :mvn_done
        )
    )
)

:: Last resort: check registry
for /f "tokens=2*" %%a in ('reg query "HKLM\SOFTWARE\Apache Software Foundation\Maven" /v "M2_HOME" 2^>nul') do (
    if exist "%%b\bin\mvn.cmd" (
        set "MAVEN_HOME=%%b"
        set "PATH=%%b\bin;!PATH!"
        echo Auto-detected Maven from registry: %%b
        goto :mvn_done
    )
)

echo ERROR: Maven is not installed or not available in your PATH.
echo Please install Apache Maven: https://maven.apache.org/download.cgi
echo Or, if you have NetBeans installed, ensure its bundled Maven is detected.
echo You can also install Maven via Chocolatey: choco install maven
echo Or via Scoop: scoop install maven
echo Alternatively, set M2_HOME or MAVEN_HOME to your Maven installation directory.
echo.
pause
exit /b 1

:mvn_done

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
