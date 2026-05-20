# AMHS X.400 Connection Error - Troubleshooting Guide

## Problem Description

When trying to connect to the X.400 system, you encounter an error like:

```
Loaded configuration from connection.properties
Initiating connection to X.400 system...
Connection failed: LibraryResolver loaded at [timestamp]
loadIsodeLibrary invoked to load "CJavaInterface" (mapped to "CJavaInterface.dll")
Derived bindir from isode.bindir=C:\Users\...\ua_test_tool\lib
Unable to load library C:\Users\...\ua_test_tool\lib\pthreadvc2.dll
java.lang.UnsatisfiedLinkError: ...
```

This indicates that the Isode X.400 native libraries (DLL files) are missing.

## Root Cause

The Isode X.400 Java API requires native libraries to function. The error occurs because:

1. **Missing DLL Files**: The required Windows DLL files are not present in the `lib/` or `lib/amd64/` directory
2. **Library Path Not Set**: The Java runtime cannot find the native libraries
3. **Incomplete Installation**: The Isode X.400 SDK was not properly installed or the DLL files were not copied

## Required Files

The following native libraries are required:

### For 64-bit Java (Recommended):
```
lib/amd64/pthreadvc2.dll          (POSIX Threads library)
lib/amd64/CJavaInterface.dll      (Isode Java interface library)
```

### For 32-bit Java (Alternative):
```
lib/pthreadvc2.dll                (POSIX Threads library)
lib/CJavaInterface.dll            (Isode Java interface library)
```

## Solution Steps

### Step 1: Locate the Isode X.400 SDK

The DLL files come from your Isode X.400 SDK installation. They are typically located in:

```
C:\Program Files\Isode\lib\amd64\        (for 64-bit)
C:\Program Files\Isode\lib\              (for 32-bit)
C:\Program Files (x86)\Isode\lib\amd64\  (alternative location)
C:\Program Files (x86)\Isode\lib\        (alternative location)
```

If you cannot find the Isode SDK:
- Check your Downloads folder for the Isode installation package
- Contact Isode support to obtain the X.400 SDK
- Check your organization's shared network drives

### Step 2: Create the Native Library Directories

```bash
mkdir lib
mkdir lib\amd64
```

Or simply run the setup script:
```bash
setup-native-libs.bat
```

### Step 3: Copy the DLL Files

**For 64-bit Java (Recommended):**
Copy both DLL files from your Isode installation to `lib/amd64/`:
```
Copy: C:\Program Files\Isode\lib\amd64\pthreadvc2.dll
To:   C:\Users\maste\OneDrive\Desktop\ua_test_tool\lib\amd64\

Copy: C:\Program Files\Isode\lib\amd64\CJavaInterface.dll
To:   C:\Users\maste\OneDrive\Desktop\ua_test_tool\lib\amd64\
```

**For 32-bit Java:**
Copy to `lib/` instead:
```
Copy: C:\Program Files\Isode\lib\pthreadvc2.dll
To:   C:\Users\maste\OneDrive\Desktop\ua_test_tool\lib\

Copy: C:\Program Files\Isode\lib\CJavaInterface.dll
To:   C:\Users\maste\OneDrive\Desktop\ua_test_tool\lib\
```

### Step 4: Verify the Installation

After copying the DLL files, verify they are present:

```bash
# Check if files exist (Windows)
dir lib\amd64\*.dll
```

You should see output like:
```
Volume in drive C is [Your Drive]
Directory of C:\...\ua_test_tool\lib\amd64

05/20/2026  10:00 AM        1,234,567  pthreadvc2.dll
05/20/2026  10:00 AM        2,345,678  CJavaInterface.dll
```

### Step 5: Rebuild and Test

After placing the DLL files:

1. **Rebuild the project:**
   ```bash
   mvn clean package
   ```

2. **Run the application:**
   ```bash
   run.bat
   ```

3. **Try connecting again** using the UI

## Additional Troubleshooting

### Issue: "Cannot find pthreadvc2.dll" or "Cannot find CJavaInterface.dll"

**Check:**
- Verify the file names are exactly as shown (case-sensitive)
- Ensure you're using the correct architecture (32-bit vs 64-bit)
- Check that the files were completely copied (not partially)

**Solution:**
1. Delete the partial/incorrect files
2. Copy the correct DLL files from the Isode SDK again
3. Verify file sizes match the original files in the Isode installation

### Issue: "java.lang.UnsatisfiedLinkError: Can't load library"

This means the DLL file cannot be loaded even though it exists. This can happen if:

**Causes:**
- Wrong architecture (32-bit DLL on 64-bit Java or vice versa)
- Missing dependencies of the DLL (other required libraries)
- File corruption during copy
- Antivirus software blocking the DLL

**Solutions:**
1. Verify Java architecture matches DLL architecture:
   ```bash
   java -version
   ```
   Look for "64-Bit Server VM" or "32-Bit Server VM"

2. Use the correct directory:
   - 64-bit Java: Use `lib/amd64/`
   - 32-bit Java: Use `lib/`

3. Re-download and copy the DLL files from the Isode SDK

4. Check if antivirus software is blocking the files:
   - Temporarily disable antivirus and try again
   - Add the `lib/amd64/` directory to antivirus whitelist

### Issue: "Connection timed out" after successful library loading

If the DLL files load successfully but the connection times out:

**Check:**
1. Verify the X.400 server address in `connection.properties`
2. Ensure the X.400 server is running and accessible
3. Check network connectivity to the server
4. Verify firewall rules allow connection to the server port
5. Confirm username/password are correct

**Example connection.properties:**
```properties
presentationAddress="3001"/URI+0000+URL+itot\://192.168.22.186\:3001
userOrAddress=/CN\=VVTSOPTC/OU\=VVTS/O\=VVTS/PRMD\=VIETNAM/ADMD\=ICAO/C\=XX/
password=123456
connectionType=P7
```

Test connectivity:
```bash
ping 192.168.22.186
```

## Environment Information

### Your System Details:
- **Operating System**: Windows
- **Java Version**: OpenJDK 1.8.0_322
- **Project Location**: C:\Users\maste\OneDrive\Desktop\ua_test_tool
- **Config File**: connection.properties

### Required Configuration:
- **Maven**: Should be installed (for `mvn clean package`)
- **Native Libraries**: pthreadvc2.dll, CJavaInterface.dll
- **X.400 Server**: Must be running and accessible

## Advanced Configuration

### Setting Java Library Path (Alternative Method)

If you want to keep DLL files in a different location, you can set the Java library path:

```bash
java -Djava.library.path=C:\path\to\dlls -jar ua-test-tool-1.0.0-jar-with-dependencies.jar
```

Or in `run.bat`:
```batch
java -Djava.library.path=lib\amd64 -jar target\ua-test-tool-1.0.0-jar-with-dependencies.jar
```

## Getting Help

If you still cannot resolve the issue:

1. **Check the logs** in the UI output window for specific error details
2. **Review connection.properties** for correct server configuration
3. **Contact Isode support** if you don't have the SDK
4. **Contact your administrator** if connecting to a corporate X.400 system
5. **Check README.md** for additional build and installation instructions

## File Checklist

Before reporting an issue, verify:
- [ ] Java is installed (Java 8+): `java -version`
- [ ] Maven is installed: `mvn -version`
- [ ] `lib/amd64/pthreadvc2.dll` exists
- [ ] `lib/amd64/CJavaInterface.dll` exists
- [ ] `connection.properties` has correct settings
- [ ] X.400 server is running and accessible
- [ ] Project was rebuilt after copying DLL files: `mvn clean package`

