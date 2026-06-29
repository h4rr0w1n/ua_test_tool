#!/bin/bash
# ============================================================
# run.sh  —  AMHS UA Test Tool
#
# Recommended workflow:
#   1) Build once with install-and-build.sh
#   2) Run the self-contained dist/ package: ./dist/run.sh
#   3) If run from the project root, this wrapper forwards into dist/
#
# Target machine needs ONLY Java 8+  — no Maven, no .m2
# ============================================================

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" > /dev/null 2>&1 && pwd)"
if [ -f "$SCRIPT_DIR/ua-test-tool.jar" ]; then
    :
elif [ -f "$SCRIPT_DIR/dist/ua-test-tool.jar" ]; then
    SCRIPT_DIR="$SCRIPT_DIR/dist"
fi
cd "$SCRIPT_DIR"

echo "=========================================================="
echo "          AMHS UA Test Tool"
echo "=========================================================="
echo

# ---- Locate the JAR in dist/ or current directory -------
JAR_FILE="$SCRIPT_DIR/ua-test-tool.jar"

# Native lib dir lives beside this script in dist/lib/
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
    echo "WARNING: Compiled JAR not found."
    echo "Expected at: $JAR_FILE"
    echo "Attempting to build project automatically using Maven..."
    echo
    
    if command -v mvn > /dev/null 2>&1; then
        mvn clean package
        if [ $? -ne 0 ]; then
            echo "ERROR: Maven build failed. Please check errors."
            exit 1
        fi
        cp "$SCRIPT_DIR/target/ua-test-tool-1.0.0.jar" "$JAR_FILE"
        mkdir -p "$ISODE_LIB_DIR"
        cp -r "$SCRIPT_DIR/target/lib/"* "$ISODE_LIB_DIR/"
        echo "Build successful."
        echo
    else
        echo "ERROR: Maven is not installed."
        echo "Please install Maven or build manually."
        exit 1
    fi
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
