# AMHS UA Test Tool

A Java-based AMHS X.400 test tool with a Swing GUI.

## Prerequisites

- Java 8 or higher
- Maven
- Isode X.400 libraries (JAR files and native libraries)

## Install

### Install Isode libraries

On Windows:

```bat
install-isode-libs.bat
```

On Linux/macOS:

```bash
./install-isode-libs.sh
```

The script expects your Isode JAR files to be available in the `lib/` directory and installs them into your local Maven repository.

## Build and Run

Build the project:

```bash
mvn clean package
```

Run the application:

```bash
java -jar target/ua-test-tool-1.0.0-jar-with-dependencies.jar
```

On Windows, you can also use the provided batch scripts:

```bat
build.bat
run.bat
```

> Note: This repository does not include `run.sh`.

## Project Structure

```
ua_test_tool/
├── build.bat
├── install-isode-libs.bat
├── install-isode-libs.sh
├── lib/                      # Optional directory for Isode JAR files
├── pom.xml                   # Maven build configuration
├── README.md                 # Main project guide
├── DOCKER_GUI_USAGE.md       # Docker GUI usage instructions
├── DOCKER_README.md          # Docker build and runtime guide
├── run.bat                   # Run the tool on Windows
├── src/                      # Java source files
└── target/                   # Build artifacts (removed during cleanup)
```

## Configuration

The tool uses `connection.properties` for X.400 connection settings.

Common values:
- `presentationAddress`
- `userAddress`
- `password`
- `connectionType` (P3 or P7)

## Troubleshooting

### Missing native libraries
If you see `UnsatisfiedLinkError` for missing DLLs or shared libraries, ensure the required Isode native files are available in `lib/` and on the Java library path.

### Build fails with missing dependencies
If Maven cannot resolve Isode dependencies, make sure the required JAR files are present in `lib/` and run the install script again.

### Connection issues
If the X.400 connection fails:
1. Verify `connection.properties`
2. Confirm the X.400 server is reachable
3. Check credentials and connection type
4. Confirm network access to the server

## Docker

For Docker usage, see `DOCKER_GUI_USAGE.md`.

## License

[Add your license information here]
