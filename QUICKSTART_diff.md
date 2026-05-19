--- QUICKSTART.md (原始)


+++ QUICKSTART.md (修改后)
# AMHS UA Test Tool - Quick Start Guide

## Running the Application

### Option 1: Complete Startup (Recommended for First Run)

This script handles everything: checks dependencies, installs libraries, builds, and launches the UI.

```bash
./start.sh
```

### Option 2: Manual Steps

If you prefer to run each step manually:

#### Step 1: Install Isode Libraries
```bash
./install-isode-libs.sh
```

#### Step 2: Build the Application
```bash
mvn clean package
```

#### Step 3: Launch the UI
```bash
./run.sh
```

Or directly:
```bash
java -jar target/ua-test-tool-1.0.0-jar-with-dependencies.jar
```

## Headless/Remote Operation

If running on a server without a display:

### Using Xvfb (Virtual Framebuffer)

```bash
# Install Xvfb if not already installed
apt-get install xvfb

# Start virtual display
Xvfb :99 -screen 0 1024x768x24 &
export DISPLAY=:99

# Run the application
./run.sh
```

### Using SSH X11 Forwarding

```bash
# Connect with X11 forwarding
ssh -X user@hostname

# Then run
./run.sh
```

## Using the UI

Once the UI is open:

1. **Connect to X.400 System**
   - Enter Presentation Address (e.g., `"3001"/Internet=nova.isode.net+3001`)
   - Enter User/O/R Address (e.g., `/CN=P7User1/OU=Sales/O=nova/PRMD=Isode/ADMD= /C=GB/`)
   - Enter Password
   - Select P7 (Message Store) or P3 (Channel)
   - Click **Connect**

2. **Send a Message**
   - Enter Recipient O/R Address
   - Enter Subject
   - Enter Content
   - Select Priority (optional)
   - Click **Send Message**

3. **Receive Messages**
   - Click **Receive Messages** to download messages
   - Click **Get Mailbox Summary** to list without downloading

## Troubleshooting

### "No X11 DISPLAY" Error
- Set DISPLAY variable: `export DISPLAY=:0`
- Or use Xvfb for headless operation (see above)

### Connection Failed
- Verify presentation address format
- Check network connectivity
- Ensure credentials are correct
- Check firewall settings (port 3001)

### Build Failed
- Run `./install-isode-libs.sh` first
- Ensure Java 8+ and Maven are installed
- Clean and rebuild: `mvn clean package`

## Files Overview

| File | Purpose |
|------|---------|
| `start.sh` | Complete startup script (recommended) |
| `install-isode-libs.sh` | Install Isode libraries to Maven repo |
| `run.sh` | Launch the UI application |
| `run-ui-headless.sh` | Launch UI with headless guidance |
| `pom.xml` | Maven build configuration |
| `lib/` | Contains all required JAR libraries |
| `src/` | Source code directory |

## Requirements

- **Java**: Version 1.8 or higher
- **Maven**: Version 3.6 or higher
- **Display**: X11 display (or Xvfb for headless)