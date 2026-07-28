@echo off
:: ============================================================
:: install-and-build.bat  —  AMHS UA Test Tool
::
:: Installs all local dependencies (Isode, ATTech UA, 3rd party JARs)
:: into the project local repository (m2repo) and user's .m2 repository,
:: then compiles and packages the project into dist\
:: ============================================================

title Install Dependencies and Build AMHS UA Test Tool

echo ==========================================================
echo    Installing Dependencies and Building AMHS UA Test Tool
echo ==========================================================
echo.

setlocal enabledelayedexpansion

set "SCRIPT_DIR=%~dp0"
if "%SCRIPT_DIR:~-1%"=="\" set "SCRIPT_DIR=%SCRIPT_DIR:~0,-1%"
cd /d "%SCRIPT_DIR%"

:: ============================================================
:: JAVA AUTO-DETECTION
:: ============================================================

if defined JAVA_HOME (
    if exist "!JAVA_HOME!\bin\java.exe" (
        echo Using valid JAVA_HOME: !JAVA_HOME!
        goto :java_found
    ) else (
        echo WARNING: Existing JAVA_HOME points to missing path: !JAVA_HOME!
        echo Searching for valid Java installation...
        set "JAVA_HOME="
    )
)

if exist "%~dp0jre\bin\java.exe" (
    set "JAVA_HOME=%~dp0jre"
    echo Auto-detected local JRE: !JAVA_HOME!
    goto :java_found
)

if exist "%SCRIPT_DIR%\jre\bin\java.exe" (
    set "JAVA_HOME=%SCRIPT_DIR%\jre"
    echo Auto-detected local JRE: !JAVA_HOME!
    goto :java_found
)

for /f "delims=" %%i in ('dir /b /s "%USERPROFILE%\.antigravity-ide\extensions\java.exe" "%USERPROFILE%\.antigravity\extensions\java.exe" "%USERPROFILE%\.vscode\extensions\java.exe" 2^>nul') do (
    for %%j in ("%%~dpi..") do (
        set "JAVA_HOME=%%~fj"
        echo Auto-detected JAVA_HOME: !JAVA_HOME!
        goto :java_found
    )
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

for /f "delims=" %%i in ('where java 2^>nul') do (
    set "JAVA_PATH=%%i"
    for %%j in ("%%i") do set "JAVA_BIN_DIR=%%~dpj"
    for %%k in ("!JAVA_BIN_DIR!..") do set "JAVA_HOME=%%~fk"
    if exist "!JAVA_HOME!\bin\java.exe" (
        echo Auto-detected JAVA_HOME from PATH: !JAVA_HOME!
        goto :java_found
    )
)

for /f "tokens=2*" %%a in ('reg query "HKLM\SOFTWARE\JavaSoft\Java Development Kit" /v CurrentVersion 2^>nul') do (
    set "JDK_VER=%%b"
    for /f "tokens=2*" %%c in ('reg query "HKLM\SOFTWARE\JavaSoft\Java Development Kit\!JDK_VER!" /v JavaHome 2^>nul') do (
        if exist "%%~d\bin\java.exe" (
            set "JAVA_HOME=%%~d"
            echo Auto-detected JAVA_HOME from registry: !JAVA_HOME!
            goto :java_found
        )
    )
)

echo ERROR: Java is not installed or not found.
echo Please install Java 8 or higher and set JAVA_HOME.
echo.
pause
exit /b 1

:java_found
set "PATH=!JAVA_HOME!\bin;%PATH%"
echo Using Java at: !JAVA_HOME!
echo.

:: ---- 1. Check Maven ----
where mvn >nul 2>&1
if errorlevel 1 (
    echo ERROR: Maven ^(mvn^) is not installed or not in PATH.
    echo Please install Apache Maven and try again.
    pause
    exit /b 1
)

:: ---- 2. Check lib directory ----
if not exist "%SCRIPT_DIR%\lib" (
    echo ERROR: lib directory not found at %SCRIPT_DIR%\lib
    pause
    exit /b 1
)

echo [1/3] Installing local JARs into project repository ^(m2repo^) and ~/.m2/repository...
echo.

call :install_jar "lib\isode-x400-1.0.0.jar" "com.isode.x400" "isode-x400" "1.0.0"
call :install_jar "lib\isode-lib-1.0.0.jar" "com.isode" "isode-lib" "1.0.0"
call :install_jar "lib\isode-asn-1.0.0.jar" "com.isode" "isode-asn" "1.0.0"
call :install_jar "lib\isode-crypto-1.0.0.jar" "com.isode" "isode-crypto" "1.0.0"
call :install_jar "lib\isode-dsapi-1.0.0.jar" "com.isode" "isode-dsapi" "1.0.0"
call :install_jar "lib\isode-dsapigui-1.0.0.jar" "com.isode" "isode-dsapigui" "1.0.0"
call :install_jar "lib\isode-emmash-1.0.0.jar" "com.isode" "isode-emmash" "1.0.0"
call :install_jar "lib\isode-hlxja-1.0.0.jar" "com.isode" "isode-hlxja" "1.0.0"
call :install_jar "lib\isode-mvc-1.0.0.jar" "com.isode" "isode-mvc" "1.0.0"
call :install_jar "lib\isode-nettrace-1.0.0.jar" "com.isode" "isode-nettrace" "1.0.0"
call :install_jar "lib\isode-rbac-1.0.0.jar" "com.isode" "isode-rbac" "1.0.0"
call :install_jar "lib\isode-ca-1.0.0.jar" "com.isode" "isode-ca" "1.0.0"
call :install_jar "lib\jswrapper-1.0.0.jar" "com.isode" "jswrapper" "1.0.0"

call :install_jar "lib\com.attech.amhs.ua.db-1.0.0.jar" "com.attech.amhs.ua" "com.attech.amhs.ua.db" "1.0.0"
call :install_jar "lib\com.attech.amhs.ua.common-1.0.0.jar" "com.attech.amhs.ua" "com.attech.amhs.ua.common" "1.0.0"

call :install_jar "lib\javax.persistence-api-2.2.jar" "javax.persistence" "javax.persistence-api" "2.2"
call :install_jar "lib\hibernate-core-4.3.11.Final.jar" "org.hibernate" "hibernate-core" "4.3.11.Final"
call :install_jar "lib\slf4j-api-2.0.7.jar" "org.slf4j" "slf4j-api" "2.0.7"
call :install_jar "lib\log4j-1.2.17.jar" "log4j" "log4j" "1.2.17"
call :install_jar "lib\commons-lang-2.6.jar" "commons-lang" "commons-lang" "2.6"
call :install_jar "lib\bcprov-jdk15on-1.47.jar" "org.bouncycastle" "bcprov-jdk15on" "1.47"
call :install_jar "lib\bcpkix-jdk15on-1.47.jar" "org.bouncycastle" "bcpkix-jdk15on" "1.47"
call :install_jar "lib\jna-3.4.0.jar" "net.java.dev.jna" "jna" "3.4.0"
call :install_jar "lib\poi-ooxml-3.17.jar" "org.apache.poi" "poi-ooxml" "3.17"
call :install_jar "lib\poi-3.17.jar" "org.apache.poi" "poi" "3.17"

echo.
echo [2/3] Building application with Maven...
echo.

call mvn clean package
if errorlevel 1 (
    echo.
    echo ERROR: Maven build failed.
    exit /b 1
)

echo.
echo [3/3] Assembling standalone distribution package ^(dist\^)...
echo.

if not exist "%SCRIPT_DIR%\dist" mkdir "%SCRIPT_DIR%\dist"
if not exist "%SCRIPT_DIR%\dist\lib" mkdir "%SCRIPT_DIR%\dist\lib"

copy /Y "%SCRIPT_DIR%\target\ua-test-tool-1.0.0.jar" "%SCRIPT_DIR%\ua-test-tool.jar" >nul
copy /Y "%SCRIPT_DIR%\target\ua-test-tool-1.0.0.jar" "%SCRIPT_DIR%\dist\ua-test-tool.jar" >nul

xcopy /Y /S /I "%SCRIPT_DIR%\lib\*" "%SCRIPT_DIR%\dist\lib\" >nul

if exist "%SCRIPT_DIR%\connection.properties" (
    if not exist "%SCRIPT_DIR%\dist\connection.properties" (
        copy /Y "%SCRIPT_DIR%\connection.properties" "%SCRIPT_DIR%\dist\connection.properties" >nul
    )
)

echo ==========================================================
echo    SUCCESS! Build completed successfully.
echo    Distribution package created at: %SCRIPT_DIR%\dist
echo    To run: run.bat or dist\run.bat
echo ==========================================================
echo.
exit /b 0

:: ------------------------------------------------------------
:: Subroutine: install_jar <file> <groupId> <artifactId> <version>
:: ------------------------------------------------------------
:install_jar
set "JAR_PATH=%~1"
set "GID=%~2"
set "AID=%~3"
set "VER=%~4"

if exist "%SCRIPT_DIR%\%JAR_PATH%" (
    echo   Installing %AID% %VER%...
    call mvn install:install-file -Dfile="%SCRIPT_DIR%\%JAR_PATH%" -DgroupId="%GID%" -DartifactId="%AID%" -Dversion="%VER%" -Dpackaging=jar -DlocalRepositoryPath="%SCRIPT_DIR%\m2repo" -DcreateChecksum=true >nul
    call mvn install:install-file -Dfile="%SCRIPT_DIR%\%JAR_PATH%" -DgroupId="%GID%" -DartifactId="%AID%" -Dversion="%VER%" -Dpackaging=jar -DcreateChecksum=true >nul
) else (
    echo   WARNING: Dependency file missing: %JAR_PATH%
)
exit /b 0
