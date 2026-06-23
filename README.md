# AMHS UA Test Tool

A Java-based AMHS X.400 test tool equipped with a Swing GUI. This tool acts as an AMHS User Agent, allowing you to connect to an X.400 Message Store (P7) or Channel (P3) to send, receive, and validate AMHS test messages.

## Prerequisites

**On the Build Machine:**
- Java 8 or higher
- Apache Maven
- Isode X.400 Native Libraries & JAR files (must be placed in the `lib/` directory)

**On the Target/Deployment Machine:**
- Java 8 or higher
- *No Maven or local `.m2` repository is required!*

---

## 1. Building the Application

To build the tool, you only need to run the unified build script. This script automatically detects your Java and Maven installations, installs the custom Isode JARs from the `lib/` directory into your local Maven repository, and compiles the application.

**On Windows:**
```bat
install-and-build.bat
```

**On Linux/macOS:**
```bash
chmod +x install-and-build.sh
./install-and-build.sh
```

### Build Output (`dist` folder)
After a successful build, a `dist/` directory will be created in the project root. This directory contains a self-contained, deployable package:
- `ua-test-tool.jar` (A "fat JAR" containing all Java dependencies)
- `lib/` (Containing only your native Isode `.dll` or `.so` libraries)
- `run.bat` & `run.sh` (Standalone launch scripts)

The project root `lib/` directory is used during the build process to install JAR dependencies and collect native platform libraries. Only the native libraries are copied into `dist/lib/` for runtime.

---

## 2. Running the Application

Because the build creates a fully self-contained `dist/` folder, you do not need Maven, source code, or the `.m2` repository to run the tool.

### To Deploy to Another Machine:
1. Copy the entire `dist/` folder to the target machine.
2. Ensure the target machine has **Java 8 or higher** installed.
3. Run the launch script from inside the `dist/` folder.

**On Windows:**
```bat
dist\run.bat
```

**On Linux/macOS:**
```bash
cd dist
chmod +x run.sh
./run.sh
```

> Note: The preferred runtime path is always the self-contained `dist/` package. The root-level `run.bat` and `run.sh` are wrappers that forward execution into `dist/` when available.

---

## 3. Configuration

The tool connects to your X.400 Message Store via parameters that can be loaded and saved inside the GUI. The settings are saved locally to a `connection.properties` file in the `dist/` directory.

**Standard AMHS Parameters Required:**
- `presentationAddress`: e.g., `"3001"/Internet=192.168.22.186+3001`
- `userOrAddress`: Your O/R address, e.g., `/CN=VVTSMHSA/OU=VVTS/O=VVTS/PRMD=VIETNAM/ADMD=ICAO/C=XX/`
- `password`: Account password
- `connectionType`: P7 (Message Store) or P3 (Channel)

---

## 4. Troubleshooting

### Connection Failed / UnsatisfiedLinkError
If you see an error mentioning `UnsatisfiedLinkError` or "Native library loading error", the Isode X.400 native libraries (`.dll` or `.so`) are missing or incompatible. 
- Ensure the native files (e.g., `pthreadvc2.dll`, `CJavaInterface.dll`) are inside the `dist/lib/` folder.
- Ensure your Java architecture (32-bit vs 64-bit) matches the architecture of the native libraries.

### Build Fails
- If `install-and-build` fails because it cannot find Isode dependencies, verify that your provided `.jar` files are correctly located in the root `lib/` directory before running the script.

### Connection Issues (Timeouts or Bind Errors)
1. Verify the `presentationAddress` format.
2. Confirm the X.400 server is reachable and running over the network.
3. Ensure you have selected the correct Connection Type (P7 vs P3) that your server is expecting.
