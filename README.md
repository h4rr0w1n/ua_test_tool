# AMHS UA Test Tool

A Java-based AMHS X.400 test tool equipped with a Swing GUI. This tool acts as an AMHS User Agent, allowing you to connect to an X.400 Message Store (P7) or Channel (P3) to send, receive, and validate AMHS test messages.

## Prerequisites

**On the Target/Deployment Machine:**
- Java 8 or higher
- Apache Maven
- Isode X.400 Native Libraries & JAR files (must be placed in the `libs/` directory)

---

## 1. Installation & Running

The application's installation flow is fully automated via Maven. As long as the correct versions of Java and Maven are installed and the required libraries are placed in the `libs/` directory, the application will automatically install the local dependencies, compile, package, and run.

### Running the Application

To run the tool, simply execute the startup script from the project root. If the project hasn't been built yet, the script will automatically invoke Maven to compile and build it before launching.

**On Windows:**
```bat
run.bat
```

**On Linux/macOS:**
```bash
chmod +x run.sh
./run.sh
```

### What happens in the background?
1. Maven will run the `initialize` phase to automatically install all required `com.isode.*` and `com.attech.*` JARs from the `libs/` directory into your local Maven repository.
2. Maven will compile the source code and build a self-contained "fat JAR" in the `target/` directory.
3. The `run` script will automatically load the Isode native libraries (`.dll` or `.so`) from the `libs/` directory and start the application.

---

## 2. Configuration

The tool connects to your X.400 Message Store via parameters that can be loaded and saved inside the GUI. The settings are saved locally to a `connection.properties` file in the root directory.

**Standard AMHS Parameters Required:**
- `presentationAddress`: e.g., `"3001"/Internet=192.168.22.186+3001`
- `userOrAddress`: Your O/R address, e.g., `/CN=VVTSMHSA/OU=VVTS/O=VVTS/PRMD=VIETNAM/ADMD=ICAO/C=XX/`
- `password`: Account password
- `connectionType`: P7 (Message Store) or P3 (Channel)

---

## 3. Troubleshooting

### Connection Failed / UnsatisfiedLinkError
If you see an error mentioning `UnsatisfiedLinkError` or "Native library loading error", the Isode X.400 native libraries (`.dll` or `.so`) are missing or incompatible. 
- Ensure the native files (e.g., `pthreadvc2.dll`, `CJavaInterface.dll`) are inside the `libs/` folder.
- Ensure your Java architecture (32-bit vs 64-bit) matches the architecture of the native libraries.

### Build Fails
- Ensure that all required `.jar` files are correctly located in the `libs/` directory before running the scripts.
- Make sure that `mvn` is accessible in your system's `PATH`.

### Connection Issues (Timeouts or Bind Errors)
1. Verify the `presentationAddress` format.
2. Confirm the X.400 server is reachable and running over the network.
3. Ensure you have selected the correct Connection Type (P7 vs P3) that your server is expecting.
