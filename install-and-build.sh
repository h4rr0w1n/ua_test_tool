#!/bin/bash
# ============================================================
# install-and-build.sh  —  AMHS UA Test Tool
#
# Installs all local dependencies (Isode, ATTech UA, 3rd party JARs)
# into the project local repository (m2repo) and user's ~/.m2 repository,
# then compiles and packages the project into dist/
# ============================================================

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" > /dev/null 2>&1 && pwd)"
cd "$SCRIPT_DIR"

echo "=========================================================="
echo "   Installing Dependencies & Building AMHS UA Test Tool"
echo "=========================================================="
echo

# ---- 1. Check Maven ----
if ! command -v mvn > /dev/null 2>&1; then
    echo "ERROR: Maven (mvn) is not installed or not in PATH."
    echo "Please install Apache Maven and try again."
    exit 1
fi

# ---- 2. Check lib directory ----
if [ ! -d "$SCRIPT_DIR/lib" ]; then
    echo "ERROR: lib directory not found at $SCRIPT_DIR/lib"
    exit 1
fi

echo "[1/3] Installing local JARs into project repository (m2repo) and ~/.m2/repository..."
echo

install_jar() {
    JAR_PATH="$1"
    GID="$2"
    AID="$3"
    VER="$4"

    if [ -f "$SCRIPT_DIR/$JAR_PATH" ]; then
        echo "  Installing $AID $VER..."
        mvn install:install-file \
            -Dfile="$SCRIPT_DIR/$JAR_PATH" \
            -DgroupId="$GID" \
            -DartifactId="$AID" \
            -Dversion="$VER" \
            -Dpackaging=jar \
            -DlocalRepositoryPath="$SCRIPT_DIR/m2repo" \
            -DcreateChecksum=true > /dev/null
        mvn install:install-file \
            -Dfile="$SCRIPT_DIR/$JAR_PATH" \
            -DgroupId="$GID" \
            -DartifactId="$AID" \
            -Dversion="$VER" \
            -Dpackaging=jar \
            -DcreateChecksum=true > /dev/null
    else
        echo "  WARNING: Dependency file missing: $JAR_PATH"
    fi
}

install_jar "lib/isode-x400-1.0.0.jar" "com.isode.x400" "isode-x400" "1.0.0"
install_jar "lib/isode-lib-1.0.0.jar" "com.isode" "isode-lib" "1.0.0"
install_jar "lib/isode-asn-1.0.0.jar" "com.isode" "isode-asn" "1.0.0"
install_jar "lib/isode-crypto-1.0.0.jar" "com.isode" "isode-crypto" "1.0.0"
install_jar "lib/isode-dsapi-1.0.0.jar" "com.isode" "isode-dsapi" "1.0.0"
install_jar "lib/isode-dsapigui-1.0.0.jar" "com.isode" "isode-dsapigui" "1.0.0"
install_jar "lib/isode-emmash-1.0.0.jar" "com.isode" "isode-emmash" "1.0.0"
install_jar "lib/isode-hlxja-1.0.0.jar" "com.isode" "isode-hlxja" "1.0.0"
install_jar "lib/isode-mvc-1.0.0.jar" "com.isode" "isode-mvc" "1.0.0"
install_jar "lib/isode-nettrace-1.0.0.jar" "com.isode" "isode-nettrace" "1.0.0"
install_jar "lib/isode-rbac-1.0.0.jar" "com.isode" "isode-rbac" "1.0.0"
install_jar "lib/isode-ca-1.0.0.jar" "com.isode" "isode-ca" "1.0.0"
install_jar "lib/jswrapper-1.0.0.jar" "com.isode" "jswrapper" "1.0.0"

install_jar "lib/com.attech.amhs.ua.db-1.0.0.jar" "com.attech.amhs.ua" "com.attech.amhs.ua.db" "1.0.0"
install_jar "lib/com.attech.amhs.ua.common-1.0.0.jar" "com.attech.amhs.ua" "com.attech.amhs.ua.common" "1.0.0"

install_jar "lib/javax.persistence-api-2.2.jar" "javax.persistence" "javax.persistence-api" "2.2"
install_jar "lib/hibernate-core-4.3.11.Final.jar" "org.hibernate" "hibernate-core" "4.3.11.Final"
install_jar "lib/slf4j-api-2.0.7.jar" "org.slf4j" "slf4j-api" "2.0.7"
install_jar "lib/log4j-1.2.17.jar" "log4j" "log4j" "1.2.17"
install_jar "lib/commons-lang-2.6.jar" "commons-lang" "commons-lang" "2.6"
install_jar "lib/bcprov-jdk15on-1.47.jar" "org.bouncycastle" "bcprov-jdk15on" "1.47"
install_jar "lib/bcpkix-jdk15on-1.47.jar" "org.bouncycastle" "bcpkix-jdk15on" "1.47"
install_jar "lib/jna-3.4.0.jar" "net.java.dev.jna" "jna" "3.4.0"
install_jar "lib/poi-ooxml-3.17.jar" "org.apache.poi" "poi-ooxml" "3.17"
install_jar "lib/poi-3.17.jar" "org.apache.poi" "poi" "3.17"

echo
echo "[2/3] Building application with Maven..."
echo

mvn clean package
if [ $? -ne 0 ]; then
    echo
    echo "ERROR: Maven build failed."
    exit 1
fi

echo
echo "[3/3] Assembling standalone distribution package (dist/)..."
echo

mkdir -p "$SCRIPT_DIR/dist/lib"

cp "$SCRIPT_DIR/target/ua-test-tool-1.0.0.jar" "$SCRIPT_DIR/ua-test-tool.jar"
cp "$SCRIPT_DIR/target/ua-test-tool-1.0.0.jar" "$SCRIPT_DIR/dist/ua-test-tool.jar"

cp -r "$SCRIPT_DIR/lib/"* "$SCRIPT_DIR/dist/lib/"

if [ -f "$SCRIPT_DIR/connection.properties" ] && [ ! -f "$SCRIPT_DIR/dist/connection.properties" ]; then
    cp "$SCRIPT_DIR/connection.properties" "$SCRIPT_DIR/dist/connection.properties"
fi

echo "=========================================================="
echo "   SUCCESS! Build completed successfully."
echo "   Distribution package created at: $SCRIPT_DIR/dist"
echo "   To run: ./dist/run.sh"
echo "=========================================================="
echo
