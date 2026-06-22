#!/bin/bash
# ============================================================
# run.sh  -  AMHS UA Test Tool
#
# This script will automatically compile the project using Maven
# if the target JAR does not exist, and then launch it.
# ============================================================

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" > /dev/null 2>&1 && pwd)"
cd "$SCRIPT_DIR"

JAR_FILE="$SCRIPT_DIR/target/ua-test-tool-1.0.0-jar-with-dependencies.jar"
ISODE_LIB_DIR="$SCRIPT_DIR/lib"

if [ ! -f "$JAR_FILE" ]; then
    echo "=========================================================="
    echo "Building the project with Maven..."
    echo "=========================================================="
    mvn clean package -DskipTests
    if [ $? -ne 0 ]; then
        echo
        echo "ERROR: Maven build failed!"
        exit 1
    fi
fi

echo
echo "=========================================================="
echo "Starting AMHS UA Test Tool..."
echo "=========================================================="
echo "JAR : $JAR_FILE"
echo "LIBS: $ISODE_LIB_DIR"
echo "=========================================================="
echo

java -Disode.bindir="$ISODE_LIB_DIR" \
     -Djava.library.path="$ISODE_LIB_DIR" \
     -jar "$JAR_FILE"

EXIT_CODE=$?
if [ $EXIT_CODE -ne 0 ]; then
    echo
    echo "Application exited with error code $EXIT_CODE."
fi
