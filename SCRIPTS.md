# AMHS UA Test Tool - Scripts Documentation

This document describes the build and run scripts for the AMHS UA Test Tool.

## Quick Start

1. **First time setup or after code changes:**
   ```bash
   build.bat
   ```

2. **To run the application:**
   ```bash
   run.bat
   ```

---

## Scripts Overview

### build.bat - Clean Compilation Script

**Purpose:** Performs a clean compilation and packaging of the tool. Use this after making changes to the codebase.

**Features:**
- Auto-detects Java installation (JDK 8+)
- Auto-detects Maven installation
- Performs `mvn clean package` to ensure a fresh build
- Validates all dependencies are available
- Provides clear error messages if build fails

**When to use:**
- After updating code files
- After pulling changes from version control
- When dependencies are updated
- To ensure a completely fresh build

**Example output:**
```
==================================================
       AMHS UA Test Tool - Build Script                
==================================================

Auto-detected JAVA_HOME from PATH: C:\Program Files\Java\jdk1.8.0_111
...
Starting clean build process...
...
Build completed successfully!

Next step: Run the tool using run.bat
```

---

### run.bat - Execution Script

**Purpose:** Launches the pre-compiled AMHS UA Test Tool with proper JVM settings and native library paths.

**Features:**
- Auto-detects Java and required environment
- Validates the compiled JAR exists
- Configures native library paths for Isode/ATTech libraries
- Sets appropriate JVM parameters
- Provides fallback if native libraries not found

**When to use:**
- To start the application after successful build
- Repeatedly without rebuilding (much faster than build.bat)

**Prerequisites:**
- Must run `build.bat` at least once first
- Requires JAR file at: `target/ua-test-tool-1.0.0-jar-with-dependencies.jar`

**Example output:**
```
==================================================
            AMHS UA Test Tool - Run Script                
==================================================

Using existing JAVA_HOME: C:\Program Files\Java\jdk1.8.0_111
...
Starting AMHS UA Test Tool...
==================================================

Found Isode native library path: "lib"
[Application UI launches...]
```

---

## Workflow

### Scenario 1: Initial Setup
```bash
# First time - build the tool
build.bat

# Then run the tool
run.bat
```

### Scenario 2: Making Code Changes
```bash
# Edit source code in src/main/java/...

# Rebuild with changes
build.bat

# Run the updated tool
run.bat
```

### Scenario 3: Testing Multiple Times
```bash
# First, build once
build.bat

# Then run multiple times without rebuilding
run.bat
run.bat  # Second time - no rebuild needed
run.bat  # Third time - still no rebuild
```

### Scenario 4: Dependency Issues
```bash
# If running into issues, perform a clean rebuild
build.bat

# If build fails, check:
# 1. Java is installed (run 'java -version')
# 2. Maven is installed (run 'mvn -v')
# 3. Dependencies are available in lib/ directory
```

---

## Environment Detection

Both scripts automatically detect and configure the required tools:

### Java Detection (in order of preference)
1. Uses existing JAVA_HOME environment variable if set
2. Searches PATH for `java.exe`
3. Checks common installation directories:
   - `C:\Program Files\Java`
   - `C:\Program Files (x86)\Java`
   - `C:\Program Files\Eclipse Adoptium`
   - `C:\Program Files\Amazon Corretto`
   - `C:\Program Files\Azul`
   - Others...
4. Checks Windows Registry for JDK installation

### Maven Detection (in order of preference)
1. Searches PATH for `mvn` command
2. Uses M2_HOME environment variable if set
3. Uses MAVEN_HOME environment variable if set
4. Checks common installation directories
5. Checks NetBeans bundled Maven
6. Checks Windows Registry

---

## Troubleshooting

### Error: "Java is not installed"
- Install Java 8 or higher
- Ensure `java.exe` is in your PATH
- Or set JAVA_HOME environment variable

### Error: "Maven is not installed"
- Install Apache Maven from https://maven.apache.org/download.cgi
- Or use Chocolatey: `choco install maven`
- Or use Scoop: `scoop install maven`
- Or set M2_HOME/MAVEN_HOME environment variable

### Error: "Compiled JAR not found" (when running run.bat)
- First run `build.bat` to create the JAR
- Check that build completed successfully

### Error: "Maven build failed"
- Check for compilation errors in output
- Ensure all dependencies are available
- If you have Isode/ATTech libraries, run `install-libs.bat`
- Try running `build.bat` again

### Warning: "Isode native library path not found"
- This is usually not critical
- But if needed, place native libraries in `lib/` directory
- Application will run with default JVM settings

---

## File Structure

```
ua_test_tool/
├── build.bat           ← Use this to compile
├── run.bat             ← Use this to run
├── pom.xml             ← Maven build configuration
├── src/                ← Source code
│   └── main/java/...
├── lib/                ← Native libraries (Isode/ATTech)
├── target/             ← Compiled output
│   └── ua-test-tool-1.0.0-jar-with-dependencies.jar
└── README.md           ← Project documentation
```

---

## Performance Tips

- **First build:** Takes 30-60 seconds (downloads dependencies)
- **Subsequent builds:** Takes 10-20 seconds
- **Running tool:** Starts immediately (no build overhead)

**Recommendation:** Run `build.bat` once after code changes, then use `run.bat` multiple times during testing.

---

## Advanced Usage

### Force Complete Rebuild
```bash
build.bat
# Deletes target/ and rebuilds from scratch
```

### Manual Maven Commands
```bash
# If you want more control, use Maven directly:
mvn clean package          # Full build with tests
mvn clean compile          # Compile only
mvn clean package -DskipTests  # Build without running tests
java -jar target/ua-test-tool-1.0.0-jar-with-dependencies.jar  # Run JAR directly
```

---

## Support

For issues or questions:
1. Check the error messages in the script output
2. Review this documentation
3. Check the main README.md for project documentation
4. Verify Java and Maven are properly installed

---

*Last Updated: 2026-05-27*
*Version: 1.0.0*
