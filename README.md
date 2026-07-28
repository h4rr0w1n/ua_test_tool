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

## Building from Source on Any Machine

If you are compiling the source code on a new machine (or behind an offline/proxy network):

1. **Option A (Automated Script - Recommended)**:
   Run the included setup script. It automatically registers all local JAR dependencies (Isode X.400, ATTech UA, 3rd party libraries) into both `m2repo/` and your local `.m2` repository, then builds the application into `dist/`:
   - **On Windows:**
     ```bat
     install-and-build.bat
     ```
   - **On Linux/macOS:**
     ```bash
     chmod +x install-and-build.sh
     ./install-and-build.sh
     ```

2. **Option B (Direct Maven Command)**:
   Because `pom.xml` defines a project-local repository (`file://${project.basedir}/m2repo`), standard Maven commands will resolve all dependencies locally without needing internet or central repository access:
   ```bash
   mvn clean package
   ```

---

## 1. Running the Application

The tool is provided as a self-contained, pre-compiled package in the `dist/` directory. You do not need Maven, source code, or the `.m2` repository to run the tool. The included `run.bat` (and `run.sh`) script is responsible for setting up the environment and running the tool.

### To Deploy to Another Machine:
1. Copy the entire `dist/` folder to the target machine.
2. Ensure the target machine has **Java 8 or higher** installed.
3. Run the launch script from inside the `dist/` folder.

**On Windows:**
```bat
dist\run.bat
```
*(Or simply run `run.bat` in the project root, which will automatically forward to the `dist` directory)*

**On Linux/macOS:**
```bash
cd dist
chmod +x run.sh
./run.sh
```

---

## 2. Configuration

The tool connects to your X.400 Message Store via parameters that can be loaded and saved inside the GUI. The settings are saved locally to a `connection.properties` file in the `dist/` directory.

**Standard AMHS Parameters Required:**
- `presentationAddress`: e.g., `"3001"/Internet=192.168.22.186+3001`
- `userOrAddress`: Your O/R address, e.g., `/CN=VVTSMHSA/OU=VVTS/O=VVTS/PRMD=VIETNAM/ADMD=ICAO/C=XX/`
- `password`: Account password
- `connectionType`: P7 (Message Store) or P3 (Channel)

---

## 3. Using the Tool

### Sending Test Messages

1. **Load Test Cases**: Test cases are loaded automatically from the `src/main/resources/testcases/` directory on startup. You can browse them in the left-hand "Test Case Directory" panel.

2. **Load Defaults**: Select a specific message (subcase) in the tree and click "Load defaults" to populate the message fields with the predefined values for that subcase.

3. **Send a Single Message**:
   - After loading defaults (or manually filling in the fields), click "Send Message" to send a single message.
   - Alternatively, select a subcase and click "Send defaults" to load and send the defaults for that subcase in one action.

4. **Send All Subcases**:
   - Select a test case (not a subcase) and click "Send All Subcases" to send all messages defined under that test case.
   - The tool will iterate through each subcase, load its defaults, and send the message.

5. **Sent Messages Log**:
   - All successfully sent messages (whether sent via "Send Message", "Send defaults", or "Send All Subcases") are automatically logged in the "Sent Messages" tab on the right-hand panel.
   - This allows you to track what has been sent and correlate with received messages (DR/N/RN, etc.).

### Receiving Messages

- Click "Receive Messages" to poll for incoming messages.
- Any received messages (including delivery reports (DR), non-delivery reports (NDR), and receipt notifications (RN)) will appear in the "Received Messages" tab.
- The tool attempts to correlate received reports with original sent messages when possible.

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
