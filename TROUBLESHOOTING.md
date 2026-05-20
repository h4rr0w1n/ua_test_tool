# AMHS UA Test Tool - Troubleshooting Guide

## Connection Timeout Issues

If you see: `Connection timeout after 60 seconds - server not responding`

### Root Causes & Solutions

#### 1. **Wrong Presentation Address Format** (Most Common)
The presentation address must match the exact format the ISODE X.400 server expects.

**Correct formats:**
- `"3001"/URI+0000+URL+itot://192.168.22.186:3001`  (Standard ISODE format)
- `"3001""/Internet=192.168.22.186+3001`  (Internet gateway format)

**Verify:**
- Check the working UA's configuration in `com.attech.amhs.ua\config\account.xml` from the new_ua codebase
- Copy the exact server-address value

**File to edit:** `connection.properties` (line 7: `presentationAddress=`)

---

#### 2. **Server Not Running / Unreachable**

**Quick test:**
```cmd
ping 192.168.22.186
telnet 192.168.22.186 3001
```

If both fail:
- Server is down or IP is wrong
- Check firewall rules
- Verify network connectivity

**Solution:**
- Start the AMHS X.400 server
- Check if the correct IP/port is configured
- Update `connection.properties` with correct server address

---

#### 3. **P7 vs P3 Mismatch**

The server might be restricted to one protocol type per user.

**To test:**
1. In the UI, toggle between "P7 (Message Store)" and "P3 (Channel)" radio buttons
2. Click "Connect" and observe which one responds faster

**If P3 connects instantly** → Use P3 format:
```java
P3BindSession session = new P3BindSession(address, user, password);
```

**If P7 connects instantly** → Use P7 format (default):
```java
P7BindSession session = new P7BindSession(address, user, password, false);
```

---

#### 4. **Native Library Version Mismatch**

**Symptoms:**
- Hangs immediately after creating P7BindSession
- "UnsatisfiedLinkError" in logs

**Solution:**
- Verify DLL files in `lib/` or `lib/amd64/` exist:
  - `pthreadvc2.dll`
  - `CJavaInterface.dll`
- Ensure DLLs are 64-bit (match your Java installation)
- Replace with DLLs from the working UA's lib folder

**Verify installation:**
```bash
cd lib
dir *.dll
```

---

#### 5. **Firewall/Network Blocking**

**Symptoms:**
- Times out exactly at 60 seconds
- No error code from ISODE library

**Solutions:**
- Add exceptions for port 3001 in Windows Firewall
- Check corporate proxy/VPN settings
- Try `telnet 192.168.22.186 3001` to verify connectivity

---

## Configuration Reference

### connection.properties

| Setting | Example | Description |
|---------|---------|-------------|
| `presentationAddress` | `"3001"/URI+0000+URL+itot://192.168.22.186:3001` | Server address and port |
| `userOrAddress` | `/CN=VVTSOPTC/OU=VVTS/O=VVTS/PRMD=VIETNAM/ADMD=ICAO/C=XX/` | Your O/R address |
| `password` | `123456` | Mailbox password |
| `connectionType` | `P7` | Protocol: P7 (Message Store) or P3 (Channel) |
| `recipient` | `/CN=VVTSOPTC/...` | Default recipient O/R address |
| `subject` | `Test X.400 Message` | Default message subject |

### How to Find the Correct Address

1. Locate the working UA installation
2. Find: `com.attech.amhs.ua\config\account.xml`
3. Copy the `<server-address>` value
4. Paste into `connection.properties` line 7

Example working config:
```xml
<Account>
    <server-address>"3001"/URI+0000+URL+itot://10.64.1.102:3001</server-address>
    <mailbox>/CN=VVHNOPTB/OU=VVHN/O=VVTS/PRMD=VIETNAM/ADMD=ICAO/C=XX/</mailbox>
    <password>amhs</password>
    <connection-type>P7</connection-type>
</Account>
```

---

## Debugging Steps

1. **Enable debug output:**
   ```
   Look for these in the console:
   - "DEBUG: Original Presentation Address: ..."
   - "DEBUG: Attempting P7 bind with address: ..."
   - "DEBUG: P7BindSession created, calling bind()..."
   ```

2. **Check logs:**
   - Console output shows connection progress (dots: .)
   - After 60 seconds, detailed error message appears
   - Error suggests next troubleshooting steps

3. **Test with CLI:**
   ```cmd
   cd C:\Users\maste\OneDrive\Desktop\ua_test_tool
   java -Djava.library.path=lib -jar target\ua-test-tool-1.0.0-jar-with-dependencies.jar
   ```

4. **Verify DLL loading:**
   - If "UnsatisfiedLinkError" appears → DLLs not found
   - Check `lib/amd64/` folder exists and contains DLLs
   - Run: `install-libs.bat` if provided

---

## Recent Fixes (May 2026)

### Issue: session.bind() Hung Indefinitely
**Root Cause:** ISODE X.400 library blocks indefinitely when:
- Presentation address format is wrong
- Server doesn't understand the format
- Native library has compatibility issues

**Fixes Applied:**
1. ✅ Added 60-second timeout on bind() call
2. ✅ Runs bind() in separate thread (UI doesn't freeze)
3. ✅ Provides detailed diagnostics on timeout
4. ✅ Corrected presentation address format
5. ✅ Added 4th parameter to P7BindSession constructor

### Key Constructor Fix
```java
// WRONG (hangs indefinitely):
P7BindSession session = new P7BindSession(address, user, password);

// CORRECT (works with timeout):
P7BindSession session = new P7BindSession(address, user, password, false);
```

---

## Contact & Support

If you need help:
1. Check `connection.properties` line 7 (presentationAddress)
2. Compare with working UA's account.xml
3. Verify server is running and reachable
4. Check Windows Firewall rules for port 3001
5. Try toggling between P7 and P3 in the UI

---

Last Updated: May 20, 2026
