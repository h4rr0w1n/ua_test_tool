# Windows PowerShell installation script for Maven libraries
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "AMHS UA Test Tool - Windows Library Installation" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan

$libDir = "$PSScriptRoot\lib"

if (!(Get-Command mvn -ErrorAction SilentlyContinue)) {
    Write-Error "Maven (mvn) is not installed or not in your PATH. Please install Maven first."
    exit 1
}

function Install-Jar($file, $groupId, $artifactId, $version) {
    if (Test-Path $file) {
        Write-Host "Installing: $artifactId-$version.jar" -ForegroundColor Green
        mvn install:install-file -Dfile=$file -DgroupId=$groupId -DartifactId=$artifactId -Dversion=$version -Dpackaging=jar -q
    } else {
        Write-Host "Warning: File not found - $(Split-Path $file -Leaf)" -ForegroundColor Yellow
    }
}

# Install Isode X.400 libraries
Install-Jar "$libDir\isode-x400.jar" "com.isode.x400" "isode-x400" "1.0.0"
Install-Jar "$libDir\isode-lib.jar" "com.isode" "isode-lib" "1.0.0"
Install-Jar "$libDir\isode-asn.jar" "com.isode" "isode-asn" "1.0.0"
Install-Jar "$libDir\isode-crypto.jar" "com.isode" "isode-crypto" "1.0.0"
Install-Jar "$libDir\isode-dsapi.jar" "com.isode" "isode-dsapi" "1.0.0"
Install-Jar "$libDir\isode-dsapigui.jar" "com.isode" "isode-dsapigui" "1.0.0"
Install-Jar "$libDir\isode-emmash.jar" "com.isode" "isode-emmash" "1.0.0"
Install-Jar "$libDir\isode-hlxja.jar" "com.isode" "isode-hlxja" "1.0.0"
Install-Jar "$libDir\isode-mvc.jar" "com.isode" "isode-mvc" "1.0.0"
Install-Jar "$libDir\isode-nettrace.jar" "com.isode" "isode-nettrace" "1.0.0"
Install-Jar "$libDir\isode-rbac.jar" "com.isode" "isode-rbac" "1.0.0"
Install-Jar "$libDir\isode-ca.jar" "com.isode" "isode-ca" "1.0.0"
Install-Jar "$libDir\jswrapper.jar" "com.isode" "jswrapper" "1.0.0"
Install-Jar "$libDir\jna.jar" "net.java.dev.jna" "jna" "3.4.0"

# Install Bouncy Castle
Install-Jar "$libDir\bcprov-jdk15on-147.jar" "org.bouncycastle" "bcprov-jdk15on" "1.47"
Install-Jar "$libDir\bcpkix-jdk15on-147.jar" "org.bouncycastle" "bcpkix-jdk15on" "1.47"

# Install other dependencies
Install-Jar "$libDir\slf4j-api-2.0.7.jar" "org.slf4j" "slf4j-api" "2.0.7"
Install-Jar "$libDir\log4j-1.2.17.jar" "log4j" "log4j" "1.2.17"
Install-Jar "$libDir\commons-lang-2.6.jar" "commons-lang" "commons-lang" "2.6"
Install-Jar "$libDir\commons-codec-1.4.jar" "commons-codec" "commons-codec" "1.4"
Install-Jar "$libDir\commons-io-1.4.jar" "commons-io" "commons-io" "1.4"
Install-Jar "$libDir\activation.jar" "javax.activation" "activation" "1.1"

# Install ATTech UA database and common libraries
Install-Jar "$libDir\com.attech.amhs.ua.db.jar" "com.attech.amhs.ua" "com.attech.amhs.ua.db" "1.0.0"
Install-Jar "$libDir\com.attech.amhs.ua.common.jar" "com.attech.amhs.ua" "com.attech.amhs.ua.common" "1.0.0"

Write-Host "Installation Complete!" -ForegroundColor Green
