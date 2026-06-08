#!/bin/bash
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"
cd "$SCRIPT_DIR"

echo "Installing Isode and ATTech local libraries to Maven local repository..."

if ! command -v mvn >/dev/null 2>&1; then
    echo "ERROR: Maven is not installed or not available in PATH."
    exit 1
fi

if [ ! -d "lib" ]; then
    echo "ERROR: Required lib directory not found: $SCRIPT_DIR/lib"
    echo "Place the Isode and ATTech JAR files in the project lib/ directory and rerun this script."
    exit 1
fi

install_jar() {
    local jar="$1"
    local group="$2"
    local artifact="$3"
    local version="$4"

    if [ ! -f "$jar" ]; then
        echo "ERROR: Required JAR not found: $jar"
        exit 1
    fi

    echo "Installing $jar"
    mvn install:install-file -Dfile="$jar" -DgroupId="$group" -DartifactId="$artifact" -Dversion="$version" -Dpackaging=jar
}

install_jar "lib/isode-x400.jar" com.isode.x400 isode-x400 1.0.0
install_jar "lib/isode-lib.jar" com.isode isode-lib 1.0.0
install_jar "lib/isode-asn.jar" com.isode isode-asn 1.0.0
install_jar "lib/isode-crypto.jar" com.isode isode-crypto 1.0.0
install_jar "lib/isode-dsapi.jar" com.isode isode-dsapi 1.0.0
install_jar "lib/isode-dsapigui.jar" com.isode isode-dsapigui 1.0.0
install_jar "lib/isode-emmash.jar" com.isode isode-emmash 1.0.0
install_jar "lib/isode-hlxja.jar" com.isode isode-hlxja 1.0.0
install_jar "lib/isode-mvc.jar" com.isode isode-mvc 1.0.0
install_jar "lib/isode-nettrace.jar" com.isode isode-nettrace 1.0.0
install_jar "lib/isode-rbac.jar" com.isode isode-rbac 1.0.0
install_jar "lib/isode-ca.jar" com.isode isode-ca 1.0.0
install_jar "lib/jswrapper.jar" com.isode jswrapper 1.0.0
install_jar "lib/com.attech.amhs.ua.db.jar" com.attech.amhs.ua com.attech.amhs.ua.db 1.0.0
install_jar "lib/com.attech.amhs.ua.common.jar" com.attech.amhs.ua com.attech.amhs.ua.common 1.0.0

echo "Installation complete."
