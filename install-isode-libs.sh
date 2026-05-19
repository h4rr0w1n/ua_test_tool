#!/bin/bash

# Installation script for AMHS UA Test Tool
# This script installs the required Isode libraries to the local Maven repository

echo "=========================================="
echo "AMHS UA Test Tool - Library Installation"
echo "=========================================="

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "ERROR: Maven is not installed or not in PATH"
    echo "Please install Maven first (https://maven.apache.org/download.cgi)"
    exit 1
fi

# Check if Java 8 is available
JAVA_VERSION=$(java -version 2>&1 | head -n 1)
echo "Java version: $JAVA_VERSION"

# Get the directory of this script
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
LIB_DIR="$SCRIPT_DIR/lib"

if [ ! -d "$LIB_DIR" ]; then
    echo "ERROR: lib directory not found at $LIB_DIR"
    exit 1
fi

echo ""
echo "Installing Isode libraries to local Maven repository..."
echo ""

# Function to install a jar file
install_jar() {
    local jar_file=$1
    local group_id=$2
    local artifact_id=$3
    local version=$4
    
    if [ -f "$jar_file" ]; then
        echo "Installing: $artifact_id-$version.jar"
        mvn install:install-file \
            -Dfile="$jar_file" \
            -DgroupId="$group_id" \
            -DartifactId="$artifact_id" \
            -Dversion="$version" \
            -Dpackaging=jar \
            -q
        if [ $? -eq 0 ]; then
            echo "  ✓ Success"
        else
            echo "  ✗ Failed"
        fi
    else
        echo "  ⚠ File not found: $jar_file"
    fi
}

# Install Isode libraries
echo "Installing Isode X.400 libraries..."
install_jar "$LIB_DIR/isode-x400.jar" "com.isode.x400" "isode-x400" "1.0.0"
install_jar "$LIB_DIR/isode-lib.jar" "com.isode" "isode-lib" "1.0.0"
install_jar "$LIB_DIR/isode-asn.jar" "com.isode" "isode-asn" "1.0.0"
install_jar "$LIB_DIR/isode-crypto.jar" "com.isode" "isode-crypto" "1.0.0"
install_jar "$LIB_DIR/isode-dsapi.jar" "com.isode" "isode-dsapi" "1.0.0"
install_jar "$LIB_DIR/isode-dsapigui.jar" "com.isode" "isode-dsapigui" "1.0.0"
install_jar "$LIB_DIR/isode-emmash.jar" "com.isode" "isode-emmash" "1.0.0"
install_jar "$LIB_DIR/isode-hlxja.jar" "com.isode" "isode-hlxja" "1.0.0"
install_jar "$LIB_DIR/isode-mvc.jar" "com.isode" "isode-mvc" "1.0.0"
install_jar "$LIB_DIR/isode-nettrace.jar" "com.isode" "isode-nettrace" "1.0.0"
install_jar "$LIB_DIR/isode-rbac.jar" "com.isode" "isode-rbac" "1.0.0"
install_jar "$LIB_DIR/isode-ca.jar" "com.isode" "isode-ca" "1.0.0"
install_jar "$LIB_DIR/jswrapper.jar" "com.isode" "jswrapper" "1.0.0"
install_jar "$LIB_DIR/jna.jar" "net.java.dev.jna" "jna" "3.4.0"

# Install Bouncy Castle
echo ""
echo "Installing Bouncy Castle libraries..."
install_jar "$LIB_DIR/bcprov-jdk15on-147.jar" "org.bouncycastle" "bcprov-jdk15on" "1.47"
install_jar "$LIB_DIR/bcpkix-jdk15on-147.jar" "org.bouncycastle" "bcpkix-jdk15on" "1.47"

# Install other dependencies
echo ""
echo "Installing other dependencies..."
install_jar "$LIB_DIR/slf4j-api-2.0.7.jar" "org.slf4j" "slf4j-api" "2.0.7"
install_jar "$LIB_DIR/log4j-1.2.17.jar" "log4j" "log4j" "1.2.17"
install_jar "$LIB_DIR/commons-lang-2.6.jar" "commons-lang" "commons-lang" "2.6"
install_jar "$LIB_DIR/commons-codec-1.4.jar" "commons-codec" "commons-codec" "1.4"
install_jar "$LIB_DIR/commons-io-1.4.jar" "commons-io" "commons-io" "1.4"
install_jar "$LIB_DIR/activation.jar" "javax.activation" "activation" "1.1"

echo ""
echo "=========================================="
echo "Installation complete!"
echo "=========================================="
echo ""
echo "You can now build the project with:"
echo "  mvn clean package"
echo ""
echo "To run the UI:"
echo "  java -jar target/ua-test-tool-1.0.0-jar-with-dependencies.jar"
echo ""
