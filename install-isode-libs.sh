#!/bin/bash

# Installation script for AMHS UA Test Tool
# This script installs the required Isode libraries to the local Maven repository

set -e  # Exit on error

echo "=========================================="
echo "AMHS UA Test Tool - Library Installation"
echo "=========================================="

# Get the directory of this script
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
LIB_DIR="$SCRIPT_DIR/lib"

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "ERROR: Maven is not installed or not in PATH"
    echo "Please install Maven first (https://maven.apache.org/download.cgi)"
    exit 1
fi

# Check if Java is available
if ! command -v java &> /dev/null; then
    echo "ERROR: Java is not installed or not in PATH"
    echo "Please install Java 8 or higher"
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | head -n 1)
echo "Java version: $JAVA_VERSION"
echo ""

# Create lib directory if it doesn't exist
if [ ! -d "$LIB_DIR" ]; then
    echo "Creating lib directory at $LIB_DIR"
    mkdir -p "$LIB_DIR"
    echo ""
    echo "NOTE: The lib directory was created but is empty."
    echo "Please copy your Isode JAR files and other dependencies to: $LIB_DIR"
    echo ""
    echo "Required JAR files:"
    echo "  - isode-x400.jar"
    echo "  - isode-lib.jar"
    echo "  - isode-asn.jar"
    echo "  - isode-crypto.jar"
    echo "  - isode-dsapi.jar"
    echo "  - isode-dsapigui.jar"
    echo "  - isode-emmash.jar"
    echo "  - isode-hlxja.jar"
    echo "  - isode-mvc.jar"
    echo "  - isode-nettrace.jar"
    echo "  - isode-rbac.jar"
    echo "  - isode-ca.jar"
    echo "  - jswrapper.jar"
    echo "  - jna.jar"
    echo "  - bcprov-jdk15on-147.jar"
    echo "  - bcpkix-jdk15on-147.jar"
    echo "  - slf4j-api-2.0.7.jar"
    echo "  - log4j-1.2.17.jar"
    echo "  - commons-lang-2.6.jar"
    echo "  - commons-codec-1.4.jar"
    echo "  - commons-io-1.4.jar"
    echo "  - activation.jar"
    echo ""
    echo "After copying the JAR files, run this script again."
    exit 0
fi

echo "Installing libraries from: $LIB_DIR"
echo ""

# Count how many jars we have
JAR_COUNT=$(find "$LIB_DIR" -maxdepth 1 -name "*.jar" 2>/dev/null | wc -l)
if [ "$JAR_COUNT" -eq 0 ]; then
    echo "WARNING: No JAR files found in $LIB_DIR"
    echo "Please copy the required JAR files to this directory."
    echo ""
    echo "Skipping installation. Run this script again after adding JAR files."
    exit 0
fi

echo "Found $JAR_COUNT JAR file(s) in lib directory"
echo ""

# Function to install a jar file
install_jar() {
    local jar_file=$1
    local group_id=$2
    local artifact_id=$3
    local version=$4
    
    if [ -f "$jar_file" ]; then
        echo "Installing: $artifact_id-$version.jar"
        if mvn install:install-file \
            -Dfile="$jar_file" \
            -DgroupId="$group_id" \
            -DartifactId="$artifact_id" \
            -Dversion="$version" \
            -Dpackaging=jar \
            -q 2>&1; then
            echo "  ✓ Success"
        else
            echo "  ✗ Failed"
            return 1
        fi
    else
        echo "  ⚠ File not found: $(basename "$jar_file")"
        return 0  # Don't fail if optional file is missing
    fi
}

# Track installation status
INSTALL_FAILED=0

# Install Isode libraries
echo "Installing Isode X.400 libraries..."
install_jar "$LIB_DIR/isode-x400.jar" "com.isode.x400" "isode-x400" "1.0.0" || INSTALL_FAILED=1
install_jar "$LIB_DIR/isode-lib.jar" "com.isode" "isode-lib" "1.0.0" || INSTALL_FAILED=1
install_jar "$LIB_DIR/isode-asn.jar" "com.isode" "isode-asn" "1.0.0" || INSTALL_FAILED=1
install_jar "$LIB_DIR/isode-crypto.jar" "com.isode" "isode-crypto" "1.0.0" || INSTALL_FAILED=1
install_jar "$LIB_DIR/isode-dsapi.jar" "com.isode" "isode-dsapi" "1.0.0" || INSTALL_FAILED=1
install_jar "$LIB_DIR/isode-dsapigui.jar" "com.isode" "isode-dsapigui" "1.0.0" || INSTALL_FAILED=1
install_jar "$LIB_DIR/isode-emmash.jar" "com.isode" "isode-emmash" "1.0.0" || INSTALL_FAILED=1
install_jar "$LIB_DIR/isode-hlxja.jar" "com.isode" "isode-hlxja" "1.0.0" || INSTALL_FAILED=1
install_jar "$LIB_DIR/isode-mvc.jar" "com.isode" "isode-mvc" "1.0.0" || INSTALL_FAILED=1
install_jar "$LIB_DIR/isode-nettrace.jar" "com.isode" "isode-nettrace" "1.0.0" || INSTALL_FAILED=1
install_jar "$LIB_DIR/isode-rbac.jar" "com.isode" "isode-rbac" "1.0.0" || INSTALL_FAILED=1
install_jar "$LIB_DIR/isode-ca.jar" "com.isode" "isode-ca" "1.0.0" || INSTALL_FAILED=1
install_jar "$LIB_DIR/jswrapper.jar" "com.isode" "jswrapper" "1.0.0" || INSTALL_FAILED=1
install_jar "$LIB_DIR/jna.jar" "net.java.dev.jna" "jna" "3.4.0" || INSTALL_FAILED=1

# Install Bouncy Castle
echo ""
echo "Installing Bouncy Castle libraries..."
install_jar "$LIB_DIR/bcprov-jdk15on-147.jar" "org.bouncycastle" "bcprov-jdk15on" "1.47" || INSTALL_FAILED=1
install_jar "$LIB_DIR/bcpkix-jdk15on-147.jar" "org.bouncycastle" "bcpkix-jdk15on" "1.47" || INSTALL_FAILED=1

# Install other dependencies
echo ""
echo "Installing other dependencies..."
install_jar "$LIB_DIR/slf4j-api-2.0.7.jar" "org.slf4j" "slf4j-api" "2.0.7" || INSTALL_FAILED=1
install_jar "$LIB_DIR/log4j-1.2.17.jar" "log4j" "log4j" "1.2.17" || INSTALL_FAILED=1
install_jar "$LIB_DIR/commons-lang-2.6.jar" "commons-lang" "commons-lang" "2.6" || INSTALL_FAILED=1
install_jar "$LIB_DIR/commons-codec-1.4.jar" "commons-codec" "commons-codec" "1.4" || INSTALL_FAILED=1
install_jar "$LIB_DIR/commons-io-1.4.jar" "commons-io" "commons-io" "1.4" || INSTALL_FAILED=1
install_jar "$LIB_DIR/activation.jar" "javax.activation" "activation" "1.1" || INSTALL_FAILED=1

echo ""
echo "=========================================="
if [ $INSTALL_FAILED -eq 0 ]; then
    echo "Installation complete!"
else
    echo "Installation completed with some warnings."
    echo "Some JAR files were not found in the lib directory."
fi
echo "=========================================="
echo ""
echo "Next steps:"
echo "  1. Build the project: mvn clean package"
echo "  2. Run the UI: ./run.sh"
echo ""
echo "Or simply run: ./run.sh"
echo "(This will automatically build and run if needed)"
echo ""
