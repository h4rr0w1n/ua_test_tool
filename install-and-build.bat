@echo off
:: ============================================================
:: install-and-build.bat
:: AMHS UA Test Tool - Windows
::
:: Step 1: Install Isode + ATTech JARs into local Maven repo
:: Step 2: Run "mvn clean package" to compile and package the tool
::
:: Run this script once when setting up a new machine, and again
:: whenever you update the source code or replace library JARs.
:: After this completes successfully, use run.bat to launch the tool.
:: ============================================================

title AMHS UA Test Tool - Install & Build

echo ==========================================================
echo     AMHS UA Test Tool - Install ^& Build Script
echo ==========================================================
echo.

setlocal enabledelayedexpansion

:: Change to the directory where this script lives
set "SCRIPT_DIR=%~dp0"
cd /d "%SCRIPT_DIR%"

:: ============================================================
:: SECTION 1 — JAVA AUTO-DETECTION
:: ============================================================

if not "%JAVA_HOME%"=="" (
    echo Using existing JAVA_HOME: %JAVA_HOME%
    goto :java_found
)

:: Try PATH first
for /f "delims=" %%i in ('where java 2^>nul') do (
    set "JAVA_PATH=%%i"
    for %%j in ("%%i") do set "JAVA_BIN_DIR=%%~dpj"
    for %%k in ("!JAVA_BIN_DIR!..") do set "JAVA_HOME=%%~fk"
    echo Auto-detected JAVA_HOME from PATH: !JAVA_HOME!
    goto :java_found
)

:: Search common Java install directories
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
                echo Auto-detected JAVA_HOME from install directory: !JAVA_HOME!
                goto :java_found
            )
        )
    )
)

:: Registry fallback
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
set "PATH=!JAVA_HOME!\bin;%PATH%"

if not exist "!JAVA_HOME!\bin\java.exe" (
    echo ERROR: java.exe not found at !JAVA_HOME!\bin\java.exe
    echo Please reinstall Java 8 or higher, or update JAVA_HOME.
    echo.
    pause
    exit /b 1
)

echo.
echo Java version:
java -version
echo.

:: ============================================================
:: SECTION 2 — MAVEN AUTO-DETECTION
:: ============================================================

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

:: Search common Maven install directories
for %%B in (
    "C:\Program Files\Apache Software Foundation"
    "C:\Program Files (x86)\Apache Software Foundation"
    "C:\Program Files\Maven"
    "C:\tools\maven"
    "C:\ProgramData\chocolatey\lib"
    "C:\ProgramData\chocolatey\lib\maven"
    "C:\ProgramData\chocolatey\lib\maven\tools"
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

:: NetBeans bundled Maven fallback
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

:: Registry last resort
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
echo Or via Chocolatey: choco install maven
echo Or via Scoop:      scoop install maven
echo Alternatively, set M2_HOME or MAVEN_HOME to your Maven installation directory.
echo.
pause
exit /b 1

:mvn_done

:: ============================================================
:: SECTION 3 — INSTALL LOCAL ISODE + ATTECH JARS INTO MAVEN REPO
:: ============================================================

echo.
echo ----------------------------------------------------------
echo  Step 1/2: Installing Isode + ATTech JARs into local Maven repo
echo ----------------------------------------------------------
echo.

if not exist "lib\" (
    echo ERROR: The lib\ directory was not found in: %SCRIPT_DIR%
    echo Place all Isode and ATTech JAR files under lib\ and re-run this script.
    echo.
    pause
    exit /b 1
)

call :installJar "lib/isode-x400.jar"               com.isode.x400    isode-x400           1.0.0
call :installJar "lib/isode-lib.jar"                com.isode         isode-lib            1.0.0
call :installJar "lib/isode-asn.jar"                com.isode         isode-asn            1.0.0
call :installJar "lib/isode-crypto.jar"             com.isode         isode-crypto         1.0.0
call :installJar "lib/isode-dsapi.jar"              com.isode         isode-dsapi          1.0.0
call :installJar "lib/isode-dsapigui.jar"           com.isode         isode-dsapigui       1.0.0
call :installJar "lib/isode-emmash.jar"             com.isode         isode-emmash         1.0.0
call :installJar "lib/isode-hlxja.jar"              com.isode         isode-hlxja          1.0.0
call :installJar "lib/isode-mvc.jar"                com.isode         isode-mvc            1.0.0
call :installJar "lib/isode-nettrace.jar"           com.isode         isode-nettrace       1.0.0
call :installJar "lib/isode-rbac.jar"               com.isode         isode-rbac           1.0.0
call :installJar "lib/isode-ca.jar"                 com.isode         isode-ca             1.0.0
call :installJar "lib/jswrapper.jar"                com.isode         jswrapper            1.0.0
call :installJar "lib/com.attech.amhs.ua.db.jar"    com.attech.amhs.ua com.attech.amhs.ua.db     1.0.0
call :installJar "lib/com.attech.amhs.ua.common.jar" com.attech.amhs.ua com.attech.amhs.ua.common 1.0.0

echo.
echo All local JARs installed successfully.

:: ============================================================
:: SECTION 4 — MAVEN BUILD
:: ============================================================

echo.
echo ----------------------------------------------------------
echo  Step 2/2: Building the tool with Maven (clean package)
echo ----------------------------------------------------------
echo.

call mvn clean package -DskipTests
if !ERRORLEVEL! neq 0 (
    echo.
    echo ERROR: Maven build failed!
    echo Check the error messages above.
    echo.
    pause
    exit /b 1
)

echo.
echo ==========================================================
echo   Install ^& Build completed successfully!
echo ==========================================================
echo.
echo Next step: launch the tool with   run.bat
echo.

endlocal
exit /b 0

:: ============================================================
:: HELPER: installJar ^<jar-path^> ^<groupId^> ^<artifactId^> ^<version^>
:: ============================================================
:installJar
set "JAR_PATH=%~1"
set "GROUP_ID=%~2"
set "ARTIFACT_ID=%~3"
set "VERSION=%~4"

if not exist "%JAR_PATH%" (
    echo [SKIP] JAR not found, skipping: %JAR_PATH%
    exit /b 0
)

echo Installing %JAR_PATH% ...
call mvn install:install-file -Dfile="%JAR_PATH%" -DgroupId=%GROUP_ID% -DartifactId=%ARTIFACT_ID% -Dversion=%VERSION% -Dpackaging=jar -q
if errorlevel 1 (
    echo ERROR: Failed to install %JAR_PATH%
    pause
    exit /b 1
)
exit /b 0
