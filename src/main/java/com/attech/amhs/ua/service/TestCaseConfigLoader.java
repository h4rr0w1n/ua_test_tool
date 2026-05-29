package com.attech.amhs.ua.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Helper for loading per‑case default configuration from *.properties files located in
 * {@code src/main/resources/testcases}. Each file is named {@code CTSWXXX.properties}.
 * The caller can retrieve any property by key (e.g., "recipient", "subject", "priority",
 * "content", "latestDeliveryTime", "payloadSize", "payloadType").
 */
public class TestCaseConfigLoader {

    /**
     * Load all test cases with defaults. Currently returns an empty list, causing the UI
     * to fall back to the built‑in default test case definitions.
     * This method can be expanded to read all *.properties files and populate each
     * {@link com.attech.amhs.ua.model.TestCase} accordingly.
     */
    public static java.util.List<com.attech.amhs.ua.model.TestCase> loadAllTestCases() {
        return new java.util.ArrayList<>();
    }

    private static final String PROPERTIES_PATH = "/testcases/"; // inside classpath

    /**
     * Load all properties for a given case identifier.
     *
     * @param caseId e.g. "CTSW004"
     * @return map of key → value, empty if the file cannot be read.
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
            map.put(name, props.getProperty(name));
        }
        return map;
    }

    /**
     * Convenience wrapper to fetch a single property value.
     */
    public static String get(String caseId, String key) {
        return loadDefaults(caseId).get(key);
    }
}
