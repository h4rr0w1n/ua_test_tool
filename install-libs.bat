@echo off
REM Install Isode and ATTech libraries into Maven local repository
REM This script installs the JAR files from the lib directory into ~/.m2/repository

setlocal enabledelayedexpansion

REM Set up Java and Maven environment
set "JAVA_HOME=C:\Program Files\Java\jdk1.8.0_111"
set "PATH=!JAVA_HOME!\bin;!PATH!"

echo =========================================================
echo     Installing Isode and ATTech Libraries
echo =========================================================
echo.

REM Check if Maven is available
where mvn >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo ERROR: Maven is not available in PATH
    echo Please install Maven or ensure it is in your PATH
    pause
    exit /b 1
)

cd /d "%~dp0"

REM Install Isode X.400 libraries
echo Installing Isode X.400 libraries...
mvn install:install-file -Dfile=lib\isode-x400.jar -DgroupId=com.isode.x400 -DartifactId=isode-x400 -Dversion=1.0.0 -Dpackaging=jar
mvn install:install-file -Dfile=lib\isode-lib.jar -DgroupId=com.isode -DartifactId=isode-lib -Dversion=1.0.0 -Dpackaging=jar
mvn install:install-file -Dfile=lib\isode-asn.jar -DgroupId=com.isode -DartifactId=isode-asn -Dversion=1.0.0 -Dpackaging=jar
mvn install:install-file -Dfile=lib\isode-crypto.jar -DgroupId=com.isode -DartifactId=isode-crypto -Dversion=1.0.0 -Dpackaging=jar
mvn install:install-file -Dfile=lib\isode-dsapi.jar -DgroupId=com.isode -DartifactId=isode-dsapi -Dversion=1.0.0 -Dpackaging=jar
mvn install:install-file -Dfile=lib\isode-dsapigui.jar -DgroupId=com.isode -DartifactId=isode-dsapigui -Dversion=1.0.0 -Dpackaging=jar
mvn install:install-file -Dfile=lib\isode-emmash.jar -DgroupId=com.isode -DartifactId=isode-emmash -Dversion=1.0.0 -Dpackaging=jar
mvn install:install-file -Dfile=lib\isode-hlxja.jar -DgroupId=com.isode -DartifactId=isode-hlxja -Dversion=1.0.0 -Dpackaging=jar
mvn install:install-file -Dfile=lib\isode-mvc.jar -DgroupId=com.isode -DartifactId=isode-mvc -Dversion=1.0.0 -Dpackaging=jar
mvn install:install-file -Dfile=lib\isode-nettrace.jar -DgroupId=com.isode -DartifactId=isode-nettrace -Dversion=1.0.0 -Dpackaging=jar
mvn install:install-file -Dfile=lib\isode-rbac.jar -DgroupId=com.isode -DartifactId=isode-rbac -Dversion=1.0.0 -Dpackaging=jar
mvn install:install-file -Dfile=lib\isode-ca.jar -DgroupId=com.isode -DartifactId=isode-ca -Dversion=1.0.0 -Dpackaging=jar
mvn install:install-file -Dfile=lib\jswrapper.jar -DgroupId=com.isode -DartifactId=jswrapper -Dversion=1.0.0 -Dpackaging=jar

echo Installing ATTech UA libraries...
mvn install:install-file -Dfile=lib\com.attech.amhs.ua.db.jar -DgroupId=com.attech.amhs.ua -DartifactId=com.attech.amhs.ua.db -Dversion=1.0.0 -Dpackaging=jar
mvn install:install-file -Dfile=lib\com.attech.amhs.ua.common.jar -DgroupId=com.attech.amhs.ua -DartifactId=com.attech.amhs.ua.common -Dversion=1.0.0 -Dpackaging=jar

echo.
echo =========================================================
echo     Library installation complete!
echo =========================================================
echo.

endlocal
