#!/bin/bash
# ============================================================
# install-and-build.sh
# AMHS UA Test Tool - Linux / macOS / WSL  (build machine only)
#
# Step 1 : Install Isode + ATTech JARs into local Maven repo
# Step 2 : Run "mvn clean package" to compile and package
# Step 3 : Assemble a self-contained dist/ folder that can be
#           copied as-is to any target machine (Java only needed)
#
# AFTER THIS COMPLETES:
#   - Copy the dist/ folder to the target machine
#   - On target (Windows):        run dist\run.bat
#     On target (Linux/macOS):    chmod +x dist/run.sh && ./dist/run.sh
# ============================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" > /dev/null 2>&1 && pwd)"
cd "$SCRIPT_DIR"

echo "=========================================================="
echo "    AMHS UA Test Tool - Install & Build Script"
echo "=========================================================="
echo

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
# SECTION 2 — CHECK MAVEN
# ============================================================

if ! command -v mvn > /dev/null 2>&1; then
    echo "ERROR: Maven is not installed or not available in PATH."
    echo "Install it via your package manager, e.g.:"
    echo "  Ubuntu/Debian : sudo apt install maven"
    echo "  macOS (brew)  : brew install maven"
    echo "  SDKMAN        : sdk install maven"
    exit 1
fi

echo "Maven version:"
mvn -version
echo

# ============================================================
# SECTION 3 — INSTALL LOCAL ISODE + ATTECH JARS INTO MAVEN REPO
# ============================================================

echo "----------------------------------------------------------"
echo " Step 1/3: Installing Isode + ATTech JARs into local Maven repo"
echo "----------------------------------------------------------"
echo

if [ ! -d "lib" ]; then
    echo "ERROR: The lib/ directory was not found in: $SCRIPT_DIR"
    echo "Place all Isode and ATTech JAR files under lib/ and re-run this script."
    exit 1
fi

install_jar() {
    local jar="$1"
    local group="$2"
    local artifact="$3"
    local version="$4"
    if [ ! -f "$jar" ]; then
        echo "[SKIP] JAR not found, skipping: $jar"
        return 0
    fi
    echo "Installing $jar ..."
    mvn install:install-file \
        -Dfile="$jar" \
        -DgroupId="$group" \
        -DartifactId="$artifact" \
        -Dversion="$version" \
        -Dpackaging=jar \
        -q
}

install_jar "lib/isode-x400.jar"                com.isode.x400     isode-x400            1.0.0
install_jar "lib/isode-lib.jar"                 com.isode          isode-lib             1.0.0
install_jar "lib/isode-asn.jar"                 com.isode          isode-asn             1.0.0
install_jar "lib/isode-crypto.jar"              com.isode          isode-crypto          1.0.0
install_jar "lib/isode-dsapi.jar"               com.isode          isode-dsapi           1.0.0
install_jar "lib/isode-dsapigui.jar"            com.isode          isode-dsapigui        1.0.0
install_jar "lib/isode-emmash.jar"              com.isode          isode-emmash          1.0.0
install_jar "lib/isode-hlxja.jar"               com.isode          isode-hlxja           1.0.0
install_jar "lib/isode-mvc.jar"                 com.isode          isode-mvc             1.0.0
install_jar "lib/isode-nettrace.jar"            com.isode          isode-nettrace        1.0.0
install_jar "lib/isode-rbac.jar"                com.isode          isode-rbac            1.0.0
install_jar "lib/isode-ca.jar"                  com.isode          isode-ca              1.0.0
install_jar "lib/jswrapper.jar"                 com.isode          jswrapper             1.0.0
install_jar "lib/com.attech.amhs.ua.db.jar"     com.attech.amhs.ua com.attech.amhs.ua.db     1.0.0
install_jar "lib/com.attech.amhs.ua.common.jar" com.attech.amhs.ua com.attech.amhs.ua.common 1.0.0

echo
echo "All local JARs installed."

# ============================================================
# SECTION 4 — MAVEN BUILD
# ============================================================

echo
echo "----------------------------------------------------------"
echo " Step 2/3: Building the tool with Maven (clean package)"
echo "----------------------------------------------------------"
echo

mvn clean package -DskipTests

# ============================================================
# SECTION 5 — ASSEMBLE dist/ (no Maven/m2 needed on target)
# ============================================================

echo
echo "----------------------------------------------------------"
echo " Step 3/3: Assembling self-contained dist/ folder"
echo "----------------------------------------------------------"
echo

DIST="$SCRIPT_DIR/dist"
FAT_JAR="$SCRIPT_DIR/target/ua-test-tool-1.0.0-jar-with-dependencies.jar"

if [ ! -f "$FAT_JAR" ]; then
    echo "ERROR: Fat JAR not found: $FAT_JAR"
    exit 1
fi

# Clean and recreate dist/
rm -rf "$DIST"
mkdir -p "$DIST/lib"

# Copy fat JAR
cp "$FAT_JAR" "$DIST/ua-test-tool.jar"
echo "Copied fat JAR to dist/"

# Copy native shared libraries (.dll / .so / .dylib) from lib/
for ext in dll so dylib; do
    for f in "$SCRIPT_DIR/lib/"*."$ext"; do
        [ -f "$f" ] || continue
        cp "$f" "$DIST/lib/"
        echo "Copied native lib: $(basename "$f")"
    done
done

# Copy connection.properties if present
if [ -f "$SCRIPT_DIR/connection.properties" ]; then
    cp "$SCRIPT_DIR/connection.properties" "$DIST/"
    echo "Copied connection.properties"
fi

# Copy the real root run scripts into dist
cp "$SCRIPT_DIR/run.bat" "$DIST/run.bat"
echo "Copied run.bat  ->  dist/run.bat"
cp "$SCRIPT_DIR/run.sh" "$DIST/run.sh"
echo "Copied run.sh   ->  dist/run.sh"
chmod +x "$DIST/run.sh"
echo "Created dist/run.sh"

echo
echo "=========================================================="
echo "  Install & Build completed successfully!"
echo "=========================================================="
echo
echo "  Self-contained package ready in:  dist/"
echo
echo "  To deploy to another machine:"
echo "    1. Copy the entire dist/ folder"
echo "    2. On target (Windows):     run dist\\run.bat"
echo "       On target (Linux/macOS): chmod +x dist/run.sh && ./dist/run.sh"
echo "    3. Target machine needs ONLY Java 8+  — no Maven, no .m2"
echo
