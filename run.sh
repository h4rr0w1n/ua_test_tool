#!/bin/bash
# ============================================================
# run.sh  —  AMHS UA Test Tool
#
# Works in two modes automatically:
#   DIST mode  : run from a dist/ folder (ua-test-tool.jar beside this script)
#   DEV mode   : run from the project root (uses target/ JAR)
#
# Target machine needs ONLY Java 8+  — no Maven, no .m2
# ============================================================

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" > /dev/null 2>&1 && pwd)"
cd "$SCRIPT_DIR"

echo "=========================================================="
echo "          AMHS UA Test Tool"
echo "=========================================================="
echo

# ---- Locate the JAR (dist mode first, then dev mode) -------
JAR_FILE="$SCRIPT_DIR/ua-test-tool.jar"
if [ ! -f "$JAR_FILE" ]; then
    JAR_FILE="$SCRIPT_DIR/target/ua-test-tool-1.0.0-jar-with-dependencies.jar"
fi

# Native lib dir lives beside this script in dist/lib/,
# or in the project root lib/ for dev mode.
ISODE_LIB_DIR="$SCRIPT_DIR/lib"

# ============================================================
# CHECK JAVA
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
# CHECK JAR
# ============================================================

if [ ! -f "$JAR_FILE" ]; then
    echo "ERROR: Compiled JAR not found."
    echo "Expected at: $JAR_FILE"
    echo
    echo "Please run install-and-build.sh first."
    exit 1
fi

# ============================================================
# LAUNCH
# ============================================================

echo "Starting AMHS UA Test Tool..."
echo "JAR : $JAR_FILE"
if [ -d "$ISODE_LIB_DIR" ]; then
    echo "LIBS: $ISODE_LIB_DIR"
fi
echo "=========================================================="
echo

if [ -d "$ISODE_LIB_DIR" ]; then
    java -Disode.bindir="$ISODE_LIB_DIR" \
         -Djava.library.path="$ISODE_LIB_DIR" \
         -jar "$JAR_FILE"
else
    java -jar "$JAR_FILE"
fi

EXIT_CODE=$?
if [ $EXIT_CODE -ne 0 ]; then
    echo
    echo "Application exited with error code $EXIT_CODE."
fi
