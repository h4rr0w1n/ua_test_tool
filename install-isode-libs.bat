@echo off
setlocal enabledelayedexpansion

echo Installing Isode and ATTech local libraries to Maven local repository...

set "SCRIPT_DIR=%~dp0"
cd /d "%SCRIPT_DIR%"

if not exist "lib\" (
    echo ERROR: Required lib directory not found: "%SCRIPT_DIR%lib\"
    echo Place the Isode and ATTech JAR files in the project lib\ directory and rerun this script.
    pause
    exit /b 1
)

call :installJar "lib/isode-x400.jar" com.isode.x400 isode-x400 1.0.0
call :installJar "lib/isode-lib.jar" com.isode isode-lib 1.0.0
call :installJar "lib/isode-asn.jar" com.isode isode-asn 1.0.0
call :installJar "lib/isode-crypto.jar" com.isode isode-crypto 1.0.0
call :installJar "lib/isode-dsapi.jar" com.isode isode-dsapi 1.0.0
call :installJar "lib/isode-dsapigui.jar" com.isode isode-dsapigui 1.0.0
call :installJar "lib/isode-emmash.jar" com.isode isode-emmash 1.0.0
call :installJar "lib/isode-hlxja.jar" com.isode isode-hlxja 1.0.0
call :installJar "lib/isode-mvc.jar" com.isode isode-mvc 1.0.0
call :installJar "lib/isode-nettrace.jar" com.isode isode-nettrace 1.0.0
call :installJar "lib/isode-rbac.jar" com.isode isode-rbac 1.0.0
call :installJar "lib/isode-ca.jar" com.isode isode-ca 1.0.0
call :installJar "lib/jswrapper.jar" com.isode jswrapper 1.0.0
call :installJar "lib/com.attech.amhs.ua.db.jar" com.attech.amhs.ua com.attech.amhs.ua.db 1.0.0
call :installJar "lib/com.attech.amhs.ua.common.jar" com.attech.amhs.ua com.attech.amhs.ua.common 1.0.0

echo Installation complete.
endlocal
exit /b 0

:installJar
set "JAR_PATH=%~1"
set "GROUP_ID=%~2"
set "ARTIFACT_ID=%~3"
set "VERSION=%~4"

if not exist "%JAR_PATH%" (
    echo ERROR: Missing JAR: "%JAR_PATH%"
    exit /b 1
)

echo Installing %JAR_PATH%
call mvn install:install-file -Dfile="%JAR_PATH%" -DgroupId=%GROUP_ID% -DartifactId=%ARTIFACT_ID% -Dversion=%VERSION% -Dpackaging=jar
if errorlevel 1 (
    echo ERROR: Maven failed to install %JAR_PATH%
    exit /b 1
)
exit /b 0
