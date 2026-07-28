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

for %%B in (
    "%USERPROFILE%\.antigravity-ide\extensions"
    "%USERPROFILE%\.antigravity\extensions"
    "%USERPROFILE%\.vscode\extensions"
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
        for /d %%d in ("%%~B\redhat.java-*\jre\*" "%%~B\jdk*" "%%~B\jre*" "%%~B\java-*" "%%~B\openjdk*") do (
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
:: NATIVE LIBRARY SETUP
:: ============================================================

if exist "%ISODE_LIB_DIR%" (
    echo.
    echo Setting up native library environment...

    :: ── 1. Add Isode system bin and lib\ to the Windows process PATH
    if exist "C:\Program Files\Isode\bin" (
        set "PATH=C:\Program Files\Isode\bin;%ISODE_LIB_DIR%;%PATH%"
        echo   Detected Isode installation at C:\Program Files\Isode\bin - added to PATH.
    ) else (
        set "PATH=%ISODE_LIB_DIR%;%PATH%"
        echo   Added lib\ to process PATH for transitive DLL resolution.
    )

    :: ── 2. Create the lib-name alias CJavaInterface.dll next to libCJavaInterface.dll
    if exist "%ISODE_LIB_DIR%\libCJavaInterface.dll" (
        if not exist "%ISODE_LIB_DIR%\CJavaInterface.dll" (
            copy /Y "%ISODE_LIB_DIR%\libCJavaInterface.dll" "%ISODE_LIB_DIR%\CJavaInterface.dll" >nul
            echo   Created CJavaInterface.dll - alias for libCJavaInterface.dll
        )
    )
    if exist "%ISODE_LIB_DIR%\libCJavaMTInterface.dll" (
        if not exist "%ISODE_LIB_DIR%\CJavaMTInterface.dll" (
            copy /Y "%ISODE_LIB_DIR%\libCJavaMTInterface.dll" "%ISODE_LIB_DIR%\CJavaMTInterface.dll" >nul
            echo   Created CJavaMTInterface.dll - alias for libCJavaMTInterface.dll
        )
    )

    :: ── 3. Create/update %USERPROFILE%\isode\userlibs.txt with complete topological load order
    if not exist "%USERPROFILE%\isode" mkdir "%USERPROFILE%\isode"
    (
        echo # Auto-generated by run.bat — Isode DLL load order
        echo pthreadVC2.dll
        echo msvcr100.dll
        echo msvcp100.dll
        echo isode_libeay32.dll
        echo isode_ssleay32.dll
        echo capi.dll
        echo libsasl.dll
        echo libisode.dll
        echo libicrypto.dll
        echo libismime.dll
        echo libx509x400.dll
        echo libpp.dll
        echo libibase.dll
        echo libisodejavalib.dll
        echo libx400common.dll
        echo libx400ms.dll
        echo libx400mt.dll
        echo libCJavaInterface.dll
        echo libCJavaMTInterface.dll
        echo CJavaInterface.dll
        echo CJavaMTInterface.dll
    ) > "%USERPROFILE%\isode\userlibs.txt"
    echo   Wrote %USERPROFILE%\isode\userlibs.txt

    :: ── 4. Check for missing DLLs and warn the user.
    set "MISSING_DLLS="
    if not exist "%ISODE_LIB_DIR%\MSVCR100.dll" set "MISSING_DLLS=!MISSING_DLLS! MSVCR100.dll"
    if not exist "%ISODE_LIB_DIR%\libibase.dll" set "MISSING_DLLS=!MISSING_DLLS! libibase.dll"
    if not exist "%ISODE_LIB_DIR%\libisodejavalib.dll" set "MISSING_DLLS=!MISSING_DLLS! libisodejavalib.dll"
    if not exist "%ISODE_LIB_DIR%\libx400common.dll" set "MISSING_DLLS=!MISSING_DLLS! libx400common.dll"

    if not "!MISSING_DLLS!"=="" (
        echo.
        echo   WARNING: The following DLLs are MISSING from %ISODE_LIB_DIR%:
        echo     !MISSING_DLLS!
        echo.
        echo   These DLLs are part of the Isode SDK - M14 or M-Switch.
        echo   Please copy them from your Isode installation into:
        echo     %ISODE_LIB_DIR%\
        echo.
        echo   Isode installations typically have these files in:
        echo     C:\Isode\bin\   or   %%ISODE_ROOT%%\bin\
        echo.
        echo   The application will attempt to start but native X.400
        echo   connectivity will fail until these files are provided.
    )
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
