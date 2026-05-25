@echo off
setlocal enabledelayedexpansion
set "JAVA_HOME=C:\Program Files\Java\jdk1.8.0_111"
set "PATH=%JAVA_HOME%\bin;%PATH%"
echo JAVA_HOME: %JAVA_HOME%
echo.
cd /d "%~dp0"
mvn compile
echo.
echo Exit code: %ERRORLEVEL%
endlocal
