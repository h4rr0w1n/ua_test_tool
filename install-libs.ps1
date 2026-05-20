# Windows PowerShell installation script for Maven libraries
# This script installs the required external JAR files to the local Maven repository.

$ErrorActionPreference = "Stop"

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "         AMHS UA Test Tool - Windows Library Installer    " -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host ""

$libDir = "$PSScriptRoot\lib"

# Auto-detect JAVA_HOME if not set
if ([string]::IsNullOrEmpty($env:JAVA_HOME)) {
    $javaPath = Get-Command java -ErrorAction SilentlyContinue
    if ($javaPath) {
        $binDir = Split-Path $javaPath.Source -Parent
        $grandParent = Split-Path $binDir -Parent
        if (Test-Path (Join-Path $grandParent "bin\java.exe")) {
            $env:JAVA_HOME = $grandParent
            Write-Host "Auto-detected JAVA_HOME: $env:JAVA_HOME" -ForegroundColor Yellow
        }
    }
    if ([string]::IsNullOrEmpty($env:JAVA_HOME)) {
        $ojdkDirs = Get-ChildItem "C:\Program Files (x86)\ojdkbuild" -ErrorAction SilentlyContinue
        foreach ($dir in $ojdkDirs) {
            if ($dir.Name -like "java-1.8.0*") {
                $env:JAVA_HOME = $dir.FullName
                $env:PATH += ";$($env:JAVA_HOME)\bin"
                Write-Host "Auto-detected JAVA_HOME from ojdkbuild: $env:JAVA_HOME" -ForegroundColor Yellow
                break
            }
        }
    }
}

# Auto-detect Maven if not in path
if (!(Get-Command mvn -ErrorAction SilentlyContinue)) {
    $netBeansMvn = "C:\Program Files (x86)\NetBeans 8.2\java\maven\bin"
    if (Test-Path (Join-Path $netBeansMvn "mvn.bat")) {
        $env:PATH += ";$netBeansMvn"
        Write-Host "Auto-detected Maven from NetBeans: $netBeansMvn" -ForegroundColor Yellow
    }
}

# Verify Maven is available
if (!(Get-Command mvn -ErrorAction SilentlyContinue)) {
    Write-Host "ERROR: Maven (mvn) is not installed or not available in your PATH." -ForegroundColor Red
    Write-Host "Please install Apache Maven first: https://maven.apache.org/download.cgi" -ForegroundColor Yellow
    Exit 1
}

# Verify Java is available
if (!(Get-Command java -ErrorAction SilentlyContinue)) {
    Write-Host "ERROR: Java is not installed or not available in your PATH." -ForegroundColor Red
    Write-Host "Please install Java 8 or higher." -ForegroundColor Yellow
    Exit 1
}

# Create lib directory if it doesn't exist
if (!(Test-Path $libDir)) {
    Write-Host "Creating lib directory at: $libDir" -ForegroundColor Yellow
    New-Item -ItemType Directory -Force -Path $libDir | Out-Null
    Write-Host ""
    Write-Host "NOTE: The lib directory was created but is empty." -ForegroundColor Magenta
    Write-Host "Please copy the required JAR files and dependencies to: $libDir" -ForegroundColor Magenta
    Write-Host ""
    Write-Host "Required JAR files:" -ForegroundColor Cyan
    Write-Host "  - isode-x400.jar"
    Write-Host "  - isode-lib.jar"
    Write-Host "  - isode-asn.jar"
    Write-Host "  - isode-crypto.jar"
    Write-Host "  - isode-dsapi.jar"
    Write-Host "  - isode-dsapigui.jar"
    Write-Host "  - isode-emmash.jar"
    Write-Host "  - isode-hlxja.jar"
    Write-Host "  - isode-mvc.jar"
    Write-Host "  - isode-nettrace.jar"
    Write-Host "  - isode-rbac.jar"
    Write-Host "  - isode-ca.jar"
    Write-Host "  - jswrapper.jar"
    Write-Host "  - jna.jar"
    Write-Host "  - bcprov-jdk15on-147.jar"
    Write-Host "  - bcpkix-jdk15on-147.jar"
    Write-Host "  - slf4j-api-2.0.7.jar"
    Write-Host "  - log4j-1.2.17.jar"
    Write-Host "  - commons-lang-2.6.jar"
    Write-Host "  - commons-codec-1.4.jar"
    Write-Host "  - commons-io-1.4.jar"
    Write-Host "  - activation.jar"
    Write-Host "  - com.attech.amhs.ua.db.jar"
    Write-Host "  - com.attech.amhs.ua.common.jar"
    Write-Host ""
    Write-Host "After copying the required JAR files, run this script again." -ForegroundColor Yellow
    Exit 0
}

# Check if there are any jar files in the lib directory
$jars = Get-ChildItem -Path $libDir -Filter *.jar -ErrorAction SilentlyContinue
if ($jars.Count -eq 0 -or $jars -eq $null) {
    Write-Host "WARNING: No JAR files found in: $libDir" -ForegroundColor Yellow
    Write-Host "Please copy the required JAR files listed above to this directory." -ForegroundColor Yellow
    Write-Host "Skipping installation. Run this script again after adding the JAR files." -ForegroundColor Yellow
    Exit 0
}

Write-Host "Found $($jars.Count) JAR file(s) in lib directory." -ForegroundColor Cyan
Write-Host "Installing libraries to local Maven repository..." -ForegroundColor Cyan
Write-Host ""

$successCount = 0
$warnCount = 0
$failCount = 0

function Install-Jar($fileName, $groupId, $artifactId, $version) {
    $filePath = Join-Path $libDir $fileName
    if (Test-Path $filePath) {
        Write-Host "Installing: $fileName -> ${groupId}:${artifactId}:${version}" -ForegroundColor Cyan
        
        # Run mvn install:install-file
        $processInfo = Start-Process mvn -ArgumentList "install:install-file -Dfile=`"$filePath`" -DgroupId=$groupId -DartifactId=$artifactId -Dversion=$version -Dpackaging=jar -q" -NoNewWindow -PassThru -Wait
        
        if ($processInfo.ExitCode -eq 0) {
            Write-Host "  [SUCCESS] Installed successfully" -ForegroundColor Green
            $script:successCount++
        } else {
            Write-Host "  [FAILED] Maven installation failed with exit code $($processInfo.ExitCode)" -ForegroundColor Red
            $script:failCount++
        }
    } else {
        Write-Host "  [SKIPPED] Optional file not found: $fileName" -ForegroundColor Yellow
        $script:warnCount++
    }
}

# Install Isode X.400 libraries
Install-Jar "isode-x400.jar" "com.isode.x400" "isode-x400" "1.0.0"
Install-Jar "isode-lib.jar" "com.isode" "isode-lib" "1.0.0"
Install-Jar "isode-asn.jar" "com.isode" "isode-asn" "1.0.0"
Install-Jar "isode-crypto.jar" "com.isode" "isode-crypto" "1.0.0"
Install-Jar "isode-dsapi.jar" "com.isode" "isode-dsapi" "1.0.0"
Install-Jar "isode-dsapigui.jar" "com.isode" "isode-dsapigui" "1.0.0"
Install-Jar "isode-emmash.jar" "com.isode" "isode-emmash" "1.0.0"
Install-Jar "isode-hlxja.jar" "com.isode" "isode-hlxja" "1.0.0"
Install-Jar "isode-mvc.jar" "com.isode" "isode-mvc" "1.0.0"
Install-Jar "isode-nettrace.jar" "com.isode" "isode-nettrace" "1.0.0"
Install-Jar "isode-rbac.jar" "com.isode" "isode-rbac" "1.0.0"
Install-Jar "isode-ca.jar" "com.isode" "isode-ca" "1.0.0"
Install-Jar "jswrapper.jar" "com.isode" "jswrapper" "1.0.0"
Install-Jar "jna.jar" "net.java.dev.jna" "jna" "3.4.0"

# Install Bouncy Castle
Install-Jar "bcprov-jdk15on-147.jar" "org.bouncycastle" "bcprov-jdk15on" "1.47"
Install-Jar "bcpkix-jdk15on-147.jar" "org.bouncycastle" "bcpkix-jdk15on" "1.47"

# Install other dependencies
Install-Jar "slf4j-api-2.0.7.jar" "org.slf4j" "slf4j-api" "2.0.7"
Install-Jar "log4j-1.2.17.jar" "log4j" "log4j" "1.2.17"
Install-Jar "commons-lang-2.6.jar" "commons-lang" "commons-lang" "2.6"
Install-Jar "commons-codec-1.4.jar" "commons-codec" "commons-codec" "1.4"
Install-Jar "commons-io-1.4.jar" "commons-io" "commons-io" "1.4"
Install-Jar "activation.jar" "javax.activation" "activation" "1.1"

# Install ATTech UA database and common libraries
Install-Jar "com.attech.amhs.ua.db.jar" "com.attech.amhs.ua" "com.attech.amhs.ua.db" "1.0.0"
Install-Jar "com.attech.amhs.ua.common.jar" "com.attech.amhs.ua" "com.attech.amhs.ua.common" "1.0.0"

Write-Host ""
Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "                  Installation Summary" -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "  Successfully Installed : $successCount" -ForegroundColor Green
Write-Host "  Skipped (Not Found)    : $warnCount" -ForegroundColor Yellow
Write-Host "  Failed to Install      : $failCount" -ForegroundColor Red
Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host ""

if ($failCount -gt 0) {
    Write-Host "Some libraries failed to install. Please check the logs above." -ForegroundColor Red
    Exit 1
} else {
    Write-Host "Library installation is complete!" -ForegroundColor Green
    Write-Host "You can now run: run.bat to build and launch the application." -ForegroundColor Yellow
    Exit 0
}
