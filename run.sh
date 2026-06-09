#!/bin/bash
# ============================================================
# run.sh
# AMHS UA Test Tool - Linux / macOS / WSL
#
# Launches the pre-built JAR with proper JVM settings.
# Run install-and-build.sh first if you haven't built yet.
# ============================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" > /dev/null 2>&1 && pwd)"
cd "$SCRIPT_DIR"

echo "=========================================================="
echo "          AMHS UA Test Tool - Run Script"
echo "=========================================================="
echo

JAR_FILE="$SCRIPT_DIR/target/ua-test-tool-1.0.0-jar-with-dependencies.jar"
ISODE_LIB_DIR="$SCRIPT_DIR/lib"

# ============================================================
# SECTION 1 — CHECK JAVA
# ============================================================

if ! command -v java > /dev/null 2>&1; then
    echo "ERROR: Java is not installed or not available in PATH."
    echo "Please install Java 8 or higher."
    exit 1
fi

echo "Java version:"
java -version
echo

# ============================================================
# SECTION 2 — CHECK JAR EXISTS
# ============================================================

if [ ! -f "$JAR_FILE" ]; then
    echo "ERROR: Compiled JAR not found at:"
    echo "  $JAR_FILE"
    echo
    echo "Please run install-and-build.sh first to compile the application."
    exit 1
fi

# ============================================================
# SECTION 3 — LAUNCH APPLICATION
# ============================================================

echo "Starting AMHS UA Test Tool..."
echo "=========================================================="
echo

if [ -d "$ISODE_LIB_DIR" ]; then
    echo "Found Isode native library path: $ISODE_LIB_DIR"
    java -Disode.bindir="$ISODE_LIB_DIR" \
         -Djava.library.path="$ISODE_LIB_DIR" \
         -jar "$JAR_FILE"
else
    echo "WARNING: Isode native library directory not found: $ISODE_LIB_DIR"
    echo "Running with default JVM settings..."
    echo
    java -jar "$JAR_FILE"
fi

EXIT_CODE=$?
if [ $EXIT_CODE -ne 0 ]; then
    echo
    echo "Application exited with code $EXIT_CODE."
fi
