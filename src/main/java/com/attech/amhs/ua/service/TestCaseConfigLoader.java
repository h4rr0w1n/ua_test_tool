package com.attech.amhs.ua.service;

import com.attech.amhs.ua.model.TestCase;
import com.attech.amhs.ua.model.TestSubcase;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Helper for loading per-case default configuration from *.properties files located in
 * {@code src/main/resources/testcases}. Each file is named {@code CTSWXXX.properties}.
 * The caller can retrieve any property by key (e.g., "recipient", "subject", "priority",
 * "content", "latestDeliveryTime", "payloadSize", "payloadType").
 */
public class TestCaseConfigLoader {

    private static final String PROPERTIES_PATH = "/testcases/"; // inside classpath

    /**
     * Load all test cases with defaults.
     * Uses TestCaseLoader to get the structure and then injects properties.
     */
    public static List<TestCase> loadAllTestCases() {
        List<TestCase> testCases = TestCaseLoader.loadDefaultTestCases();
        for (TestCase tc : testCases) {
            Map<String, String> props = loadDefaults(tc.getId());
            if (!props.isEmpty()) {
                System.out.println("=== Loading " + tc.getId() + " ===");
                for (TestSubcase subcase : tc.getSubcases()) {
                    String prefix = "subcase." + getSubcaseIndex(subcase.getId()) + ".amhs.";
                    System.out.println("  Subcase " + subcase.getId() + ", prefix: " + prefix);
                    Map<String, String> subcaseDefaults = new HashMap<>();
                    for (Map.Entry<String, String> entry : props.entrySet()) {
                        if (entry.getKey().startsWith(prefix)) {
                            String key = entry.getKey().substring(prefix.length());
                            subcaseDefaults.put(key, entry.getValue());
                            System.out.println("    [" + key + "] = " + entry.getValue());
                        }
                    }
                    if (!subcaseDefaults.isEmpty()) {
                        for (Map.Entry<String, String> entry : subcaseDefaults.entrySet()) {
                            subcase.setAmhsDefault(entry.getKey(), entry.getValue());
                        }
                    }
                }
            }
        }
        return testCases;
    }

    private static String getSubcaseIndex(String subcaseId) {
        int lastDot = subcaseId.lastIndexOf('.');
        if (lastDot != -1 && lastDot < subcaseId.length() - 1) {
            return subcaseId.substring(lastDot + 1);
        }
        return "1";
    }

    /**
     * Load all properties for a given case identifier.
     *
     * @param caseId e.g. "CTSW004"
     * @return map of key -> value, empty if the file cannot be read.
     */
    public static Map<String, String> loadDefaults(String caseId) {
        String fileName = PROPERTIES_PATH + caseId + ".properties";
        Properties props = new Properties();
        try (InputStream in = TestCaseConfigLoader.class.getResourceAsStream(fileName)) {
            if (in != null) {
                props.load(in);
            } else {
                System.err.println("[WARN] Property file not found: " + fileName);
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to load properties for " + caseId + ": " + e.getMessage());
        }
        Map<String, String> map = new HashMap<>();
        for (String name : props.stringPropertyNames()) {
            map.put(name, props.getProperty(name).trim());
        }
        return map;
    }

    /**
     * Convenience wrapper to fetch a single property value.
     */
    public static String get(String caseId, String key) {
        return loadDefaults(caseId).get(key);
    }

    public static void main(String[] args) {
        System.out.println("=== Testing TestCaseConfigLoader ===");
        List<TestCase> testCases = loadAllTestCases();
        for (TestCase tc : testCases) {
            if ("CTSW016".equals(tc.getId())) {
                System.out.println("\n=== " + tc.getId() + " subcases ===");
                for (TestSubcase subcase : tc.getSubcases()) {
                    System.out.println("  " + subcase.getId());
                    System.out.println("    amhs defaults: " + subcase.getAmhsDefaults());
                }
            }
        }
    }
}
