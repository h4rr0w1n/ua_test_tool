#!/bin/bash

# Run script for AMHS UA Test Tool
# This script builds and runs the UI application

echo "=========================================="
echo "AMHS UA Test Tool - Runner"
echo "=========================================="

# Get the directory of this script
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

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

# Check if the jar-with-dependencies exists, if not build it
JAR_FILE="$SCRIPT_DIR/target/ua-test-tool-1.0.0-jar-with-dependencies.jar"

if [ ! -f "$JAR_FILE" ]; then
    echo ""
    echo "Building the application..."
    echo ""
    
    # Check if lib directory exists for Isode libraries
    if [ ! -d "$SCRIPT_DIR/lib" ]; then
        echo "WARNING: lib directory not found."
        echo "The Isode libraries may not be installed."
        echo "Please run ./install-isode-libs.sh first if you have the Isode JAR files."
        echo ""
        echo "Attempting to build anyway (may fail if Isode dependencies are required)..."
        echo ""
    fi
    
    mvn clean package -q
    BUILD_STATUS=$?
    
    if [ $BUILD_STATUS -ne 0 ]; then
        echo ""
        echo "ERROR: Build failed!"
        echo "Make sure all required dependencies are installed."
        echo "If you have Isode libraries, run: ./install-isode-libs.sh"
        exit 1
    fi
    
    echo ""
    echo "Build successful!"
fi

echo ""
echo "Starting AMHS UA Test Tool UI..."
echo "=========================================="
echo ""

# Run the application
if [ -z "$ISODE_LIB_DIR" ]; then
    ISODE_LIB_DIR="$SCRIPT_DIR/lib"
fi

if [ -d "$ISODE_LIB_DIR" ]; then
    echo "Found Isode native library path: $ISODE_LIB_DIR"
    java -Djava.library.path="$ISODE_LIB_DIR" -jar "$JAR_FILE"
else
    echo "WARNING: Isode native library path ($ISODE_LIB_DIR) not found."
    echo "Please ensure the Isode native libraries are placed in the project's lib directory."
    echo "Running with default JVM settings..."
    java -jar "$JAR_FILE"
fi

