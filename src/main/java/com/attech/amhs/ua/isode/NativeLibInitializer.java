package com.attech.amhs.ua.isode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Self-contained Native Library Initializer for Isode X.400 DLLs.
 * Loads DLLs directly from src/main/resources/lib (classpath) or local lib folder.
 * Uses JNA SetDllDirectoryW to ensure Windows PE Loader resolves all transitive DLL dependencies cleanly.
 */
public class NativeLibInitializer {

    private static final Logger logger = LoggerFactory.getLogger(NativeLibInitializer.class);
    private static boolean initialized = false;

    private static final String[] DLL_LOAD_ORDER = {
        "pthreadVC2.dll",
        "msvcr100.dll",
        "msvcp100.dll",
        "isode_libeay32.dll",
        "isode_ssleay32.dll",
        "capi.dll",
        "libsasl.dll",
        "libisode.dll",
        "libicrypto.dll",
        "libismime.dll",
        "libx509x400.dll",
        "libpp.dll",
        "libibase.dll",
        "libisodejavalib.dll",
        "libx400common.dll",
        "libx400ms.dll",
        "libx400mt.dll",
        "libCJavaInterface.dll",
        "libCJavaMTInterface.dll",
        "CJavaInterface.dll",
        "CJavaMTInterface.dll"
    };

    // JNA interface to Kernel32 SetDllDirectoryW
    private interface Kernel32Library extends com.sun.jna.Library {
        Kernel32Library INSTANCE = (Kernel32Library) com.sun.jna.Native.loadLibrary("kernel32", Kernel32Library.class);
        boolean SetDllDirectoryW(String lpPathName);
    }

    public static synchronized void initialize() {
        if (initialized) {
            return;
        }

        try {
            File nativeDir = findOrExtractNativeDir();
            if (nativeDir == null || !nativeDir.exists()) {
                logger.error("Could not locate or extract native libraries directory.");
                return;
            }

            String absPath = nativeDir.getAbsolutePath();
            logger.info("Initializing Isode native libraries from: {}", absPath);

            // 1. Set isode.bindir system property for Isode LibraryResolver
            System.setProperty("isode.bindir", absPath);

            // 2. Update java.library.path and reset ClassLoader sys_paths
            updateJavaLibraryPath(absPath);

            // 3. Set Windows process DLL search directory via JNA SetDllDirectoryW
            try {
                if (System.getProperty("os.name", "").toLowerCase().contains("win")) {
                    Kernel32Library.INSTANCE.SetDllDirectoryW(absPath);
                    logger.debug("SetDllDirectoryW set to: {}", absPath);
                }
            } catch (Throwable t) {
                logger.warn("Could not call SetDllDirectoryW via JNA: {}", t.getMessage());
            }

            // 4. Update user.home/isode/userlibs.txt for LibraryResolver
            updateUserLibsFile();

            // 5. Pre-load all native libraries in topological dependency order
            for (String dllName : DLL_LOAD_ORDER) {
                File dllFile = new File(nativeDir, dllName);
                if (dllFile.exists()) {
                    try {
                        System.load(dllFile.getAbsolutePath());
                        logger.debug("Pre-loaded native library: {}", dllName);
                    } catch (Throwable t) {
                        logger.trace("Pre-load attempt for {} note: {}", dllName, t.getMessage());
                    }
                }
            }

            initialized = true;
            logger.info("Isode native libraries successfully initialized.");
        } catch (Exception e) {
            logger.error("Error during native library initialization", e);
        }
    }

    private static File findOrExtractNativeDir() {
        // Priority 1: Check project root lib directory
        File rootLib = new File("lib");
        if (rootLib.exists() && rootLib.isDirectory() && hasRequiredDlls(rootLib)) {
            return rootLib.getAbsoluteFile();
        }

        // Priority 2: Check target/classes/lib or build directory
        File targetLib = new File("target/classes/lib");
        if (targetLib.exists() && targetLib.isDirectory() && hasRequiredDlls(targetLib)) {
            return targetLib.getAbsoluteFile();
        }

        // Priority 3: Extract from classpath resources (/lib/*.dll) into user home
        File extractedDir = extractResourcesToUserHome();
        if (extractedDir != null && hasRequiredDlls(extractedDir)) {
            return extractedDir;
        }

        return rootLib.getAbsoluteFile();
    }

    private static boolean hasRequiredDlls(File dir) {
        return new File(dir, "libCJavaInterface.dll").exists() || new File(dir, "CJavaInterface.dll").exists();
    }

    private static File extractResourcesToUserHome() {
        try {
            File userHomeDir = new File(System.getProperty("user.home"), ".amhs_ua_tool/lib");
            if (!userHomeDir.exists()) {
                userHomeDir.mkdirs();
            }

            for (String dllName : DLL_LOAD_ORDER) {
                InputStream is = NativeLibInitializer.class.getResourceAsStream("/lib/" + dllName);
                if (is != null) {
                    File targetFile = new File(userHomeDir, dllName);
                    if (!targetFile.exists() || targetFile.length() == 0) {
                        try (OutputStream os = new FileOutputStream(targetFile)) {
                            byte[] buffer = new byte[8192];
                            int bytesRead;
                            while ((bytesRead = is.read(buffer)) != -1) {
                                os.write(buffer, 0, bytesRead);
                            }
                        }
                    }
                    is.close();
                }
            }
            return userHomeDir;
        } catch (Exception e) {
            logger.warn("Could not extract embedded DLL resources: {}", e.getMessage());
            return null;
        }
    }

    private static void updateJavaLibraryPath(String path) {
        String currentPath = System.getProperty("java.library.path");
        if (currentPath == null || !currentPath.contains(path)) {
            String newPath = (currentPath == null) ? path : path + File.pathSeparator + currentPath;
            System.setProperty("java.library.path", newPath);
            try {
                java.lang.reflect.Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
                fieldSysPath.setAccessible(true);
                fieldSysPath.set(null, null);
            } catch (Exception ignored) {
            }
        }
    }

    private static void updateUserLibsFile() {
        try {
            File isodeUserDir = new File(System.getProperty("user.home"), "isode");
            if (!isodeUserDir.exists()) {
                isodeUserDir.mkdirs();
            }
            File userLibsFile = new File(isodeUserDir, "userlibs.txt");
            StringBuilder sb = new StringBuilder("# Auto-generated Isode DLL load order\n");
            for (String dll : DLL_LOAD_ORDER) {
                sb.append(dll).append("\n");
            }
            try (FileOutputStream fos = new FileOutputStream(userLibsFile)) {
                fos.write(sb.toString().getBytes("UTF-8"));
            }
        } catch (Exception ignored) {
        }
    }
}
