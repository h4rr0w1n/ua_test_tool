# AMHS UA Test Tool

A Java-based tool for testing AMHS X.400 message sending and receiving with a Swing-based UI.

## Prerequisites

- **Java 8 or higher** - Required for running the application
- **Maven** - Required for building the project
- **Isode X.400 Libraries** - Required JAR files (if you have access to them)

## Quick Start

### Running the UI

The easiest way to run the application is using the provided run script:

```bash
./run.sh
```

This script will:
1. Check for Maven and Java installation
2. Build the project if needed (creating the JAR with dependencies)
3. Launch the UI application

### Installation (Optional - If you have Isode libraries)

If you have the Isode X.400 library JAR files, run the installation script first:

```bash
./install-isode-libs.sh
```

This script will:
1. Create a `lib/` directory if it doesn't exist
2. Install all required JAR files to your local Maven repository
3. Provide guidance on which JAR files are needed

After installation, you can build and run:

```bash
mvn clean package
./run.sh
```

## Manual Build and Run

If you prefer to build and run manually:

```bash
# Build the project
mvn clean package

# Run the UI
java -jar target/ua-test-tool-1.0.0-jar-with-dependencies.jar
```

Or use the convenience script:

```bash
./run.sh
```

## Project Structure

```
ua_test_tool/
├── install-isode-libs.sh    # Script to install Isode libraries to local Maven repo
├── run.sh                    # Script to build and run the UI
├── pom.xml                   # Maven build configuration
├── README.md                 # This file
├── lib/                      # Directory for Isode JAR files (create if needed)
└── src/
    └── main/
        └── java/
            └── com/
                └── attech/
                    └── amhs/
                        └── ua/
                            ├── ui/           # UI components (Swing)
                            ├── service/      # Business logic
                            ├── isode/        # Isode integration
                            └── common/       # Common utilities
```

## Features

- **Connection Configuration**: Configure P7 (Message Store) or P3 (Channel) connections
- **Send Messages**: Send X.400 messages with customizable priority
- **Receive Messages**: Retrieve messages from the mailbox
- **Mailbox Summary**: View summary of messages in the mailbox
- **Real-time Status**: Connection status and operation output display

## Configuration

The UI provides fields for:
- **Presentation Address**: X.400 presentation address
- **User/O/R Address**: User or OR address for authentication
- **Password**: Authentication password
- **Connection Type**: P7 (Message Store) or P3 (Channel)

## Troubleshooting

### Build fails with missing dependencies
If the build fails due to missing Isode dependencies:
1. Ensure you have the required JAR files
2. Copy them to the `lib/` directory
3. Run `./install-isode-libs.sh`
4. Try building again with `mvn clean package`

### No JAR files in lib directory
The `install-isode-libs.sh` script will create the `lib/` directory if it doesn't exist and list all required JAR files. Copy your JAR files there and run the script again.

### Java version issues
Ensure you have Java 8 or higher installed:
```bash
java -version
```

### Maven not found
Install Maven from https://maven.apache.org/download.cgi and ensure it's in your PATH.

## License

[Add your license information here]