# AMHS X.400 Connection Error - Fix Summary

## Problem
The application failed to connect to the X.400 system with the following error:

```
Unable to load library C:\Users\maste\OneDrive\Desktop\ua_test_tool\lib\pthreadvc2.dll
java.lang.UnsatisfiedLinkError: LibraryResolver loaded...
```

## Root Cause
The Isode X.400 Java library requires **native Windows DLL files** to function. These files are missing from your installation:
- `pthreadvc2.dll` (POSIX Threads library)
- `CJavaInterface.dll` (Isode Java interface library)

## Solution Overview

### What has been done:
✓ Created enhanced error handling in the code (AMHSMessageService.java)
✓ Updated UI to display helpful error messages (AMHSMessageUI.java)
✓ Created automated setup script: `setup-native-libs.ps1`
✓ Created batch script: `setup-native-libs.bat`
✓ Added comprehensive troubleshooting guide: `NATIVE_LIBRARIES_TROUBLESHOOTING.md`
✓ Updated README.md with native library troubleshooting section
✓ Created necessary directories: `lib/` and `lib/amd64/`

### What you need to do:

**STEP 1: Find the DLL files**
- Locate your Isode X.400 SDK installation
- The DLL files should be in:
  ```
  C:\Program Files\Isode\lib\amd64\
  (or C:\Program Files (x86)\Isode\lib\amd64\)
  ```

**STEP 2: Copy the DLL files**

Run the PowerShell setup script:
```powershell
powershell -ExecutionPolicy Bypass -File "setup-native-libs.ps1"
```

Then copy the files from your Isode SDK to the setup directories.

Or manually copy using PowerShell:
```powershell
Copy-Item "C:\Program Files\Isode\lib\amd64\pthreadvc2.dll" -Destination "lib\amd64\"
Copy-Item "C:\Program Files\Isode\lib\amd64\CJavaInterface.dll" -Destination "lib\amd64\"
```

Or manually copy using Windows Explorer:
1. Open `C:\Program Files\Isode\lib\amd64\` (or your installation path)
2. Copy `pthreadvc2.dll` and `CJavaInterface.dll`
3. Paste them into: `C:\Users\maste\OneDrive\Desktop\ua_test_tool\lib\amd64\`

**STEP 3: Verify the installation**

Run the verification script:
```powershell
.\setup-native-libs.ps1 -Verify
```

You should see:
```
[OK] Found: pthreadvc2.dll (xxx KB)
[OK] Found: CJavaInterface.dll (xxx KB)
```

**STEP 4: Rebuild the project**

If you have Maven installed:
```bash
mvn clean package
```

**STEP 5: Run the application**

```bash
run.bat
```

Or directly:
```powershell
java -jar target\ua-test-tool-1.0.0-jar-with-dependencies.jar
```

## Directory Structure After Fix

```
ua_test_tool/
├── lib/
│   └── amd64/
│       ├── pthreadvc2.dll          <-- Copy here
│       └── CJavaInterface.dll      <-- Copy here
├── src/
├── target/
├── setup-native-libs.ps1            (New - Setup script)
├── setup-native-libs.bat            (New - Batch wrapper)
├── NATIVE_LIBRARIES_TROUBLESHOOTING.md (New - Detailed guide)
└── README.md                        (Updated)
```

## Testing the Connection

After completing the above steps:

1. **Run the application**: `run.bat`
2. **Enter your X.400 connection details** in the UI:
   - Presentation Address: (from connection.properties)
   - User/O/R Address: (from connection.properties)
   - Password: (from connection.properties)
   - Connection Type: P7 or P3 (based on your setup)
3. **Click "Connect"**

The connection should now succeed, and you'll see:
```
Status: Connected
Successfully connected to X.400 system
```

## Troubleshooting

### If you still get the DLL error:

1. **Verify the files exist**:
   ```powershell
   .\setup-native-libs.ps1 -Verify
   ```

2. **Check Java architecture matches**:
   - Run: `java -version`
   - Look for "64-Bit Server VM" (use `lib/amd64/`)
   - Or "32-Bit Server VM" (use `lib/`)

3. **Re-download the DLL files**:
   - Delete the existing DLL files
   - Copy fresh files from your Isode SDK
   - Ensure they are not corrupted

4. **Check antivirus**:
   - Temporarily disable antivirus and try again
   - Add `lib/amd64/` to antivirus whitelist

### If connection times out after DLL files are loaded:

See: `NATIVE_LIBRARIES_TROUBLESHOOTING.md` - Section "Issue: Connection timed out after successful library loading"

## Code Changes Made

### AMHSMessageService.java
- Added detailed error handling for `UnsatisfiedLinkError`
- Added debug logging for library path and working directory
- Improved error messages to guide users on fixing DLL issues

### AMHSMessageUI.java
- Enhanced connection error handling
- Added specific detection for native library errors
- Displays helpful instructions in the UI when DLL errors occur

### New Files Created
- `setup-native-libs.ps1` - PowerShell setup and verification script
- `setup-native-libs.bat` - Batch wrapper for Windows
- `NATIVE_LIBRARIES_TROUBLESHOOTING.md` - Comprehensive troubleshooting guide

### Updated Files
- `README.md` - Added troubleshooting section for native libraries

## Support Resources

1. **Quick Setup**: `setup-native-libs.ps1` or `setup-native-libs.bat`
2. **Detailed Guide**: `NATIVE_LIBRARIES_TROUBLESHOOTING.md`
3. **General Info**: `README.md`
4. **Configuration**: `connection.properties`

## Contact

If you continue to have issues after following these steps:
- Verify your Isode X.400 SDK installation
- Contact Isode support if the SDK files are missing or corrupted
- Check that the X.400 server is running and accessible

---

**Last Updated**: May 20, 2026
**Java Version**: OpenJDK 1.8.0_322
**Status**: Ready for DLL installation
