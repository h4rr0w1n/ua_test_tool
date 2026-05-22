package com.attech.amhs.ua.service;

import com.attech.amhs.ua.model.TestCase;
import com.attech.amhs.ua.model.TestSubcase;
import java.io.*;
import java.util.*;
import java.util.Properties;

/**
 * Loader for test case configuration from properties files
 * Loads default configurations for CTSW001-CTSW020 from /resources/testcases/
 */
public class TestCaseConfigLoader {

    private static final String TESTCASES_DIR = "testcases/";
    private static final String PROPERTIES_EXTENSION = ".properties";

    /**
     * Load all test cases from properties files
     * 
     * @return List of TestCase objects with defaults loaded from properties files
     */
    public static List<TestCase> loadAllTestCases() {
        List<TestCase> testCases = new ArrayList<>();
        
        // Load CTSW001 to CTSW020
        for (int i = 1; i <= 20; i++) {
            String caseId = String.format("CTSW%03d", i);
            TestCase testCase = loadTestCase(caseId);
            if (testCase != null) {
                testCases.add(testCase);
            }
        }
        
        return testCases;
    }

    /**
     * Load a single test case from its properties file
     * 
     * @param caseId Test case ID (e.g., "CTSW001")
     * @return TestCase object or null if not found
     */
    public static TestCase loadTestCase(String caseId) {
        String resourceName = TESTCASES_DIR + caseId + PROPERTIES_EXTENSION;
        InputStream inputStream = TestCaseConfigLoader.class.getClassLoader().getResourceAsStream(resourceName);
        
        if (inputStream == null) {
            // Try alternative path
            inputStream = TestCaseConfigLoader.class.getResourceAsStream("/" + resourceName);
        }
        
        if (inputStream == null) {
            System.err.println("Warning: Configuration file not found: " + resourceName);
            return null;
        }
        
        Properties props = new Properties();
        try {
            props.load(inputStream);
            inputStream.close();
        } catch (IOException e) {
            System.err.println("Error loading properties for " + caseId + ": " + e.getMessage());
            return null;
        }
        
        // Extract test case metadata
        String name = props.getProperty("name", caseId);
        String description = props.getProperty("description", "Test case " + caseId);
        
        TestCase testCase = new TestCase();
        testCase.setId(caseId);
        testCase.setName(name);
        testCase.setDescription(description);
        
        // Load subcases
        int subcaseIndex = 1;
        while (true) {
            String subcasePrefix = "subcase." + subcaseIndex + ".";
            String subcaseId = props.getProperty(subcasePrefix + "id");
            
            if (subcaseId == null) {
                // No more subcases
                break;
            }
            
            TestSubcase subcase = new TestSubcase();
            subcase.setId(subcaseId);
            subcase.setName(props.getProperty(subcasePrefix + "name", "Subcase " + subcaseIndex));
            subcase.setDescription(props.getProperty(subcasePrefix + "description", ""));
            
            // Load AMHS defaults for this subcase
            loadAmhsDefaults(props, subcasePrefix, subcase);
            
            testCase.addSubcase(subcase);
            subcaseIndex++;
        }
        
        return testCase;
    }

    /**
     * Load AMHS default values for a subcase from properties
     * 
     * @param props Properties object
     * @param prefix Property prefix (e.g., "subcase.1.")
     * @param subcase TestSubcase to populate
     */
    private static void loadAmhsDefaults(Properties props, String prefix, TestSubcase subcase) {
        // Common AMHS fields
        String[] amhsFields = {
            "recipient", "subject", "priority", "content",
            "filing-time", "precedence", "authorization-time",
            "body-part-type", "charset-reg-number", "charset-repertoire",
            "originator-reference", "optional-heading-info",
            "responsibility", "notify-control-position"
        };
        
        for (String field : amhsFields) {
            String value = props.getProperty(prefix + "amhs." + field);
            if (value != null) {
                subcase.setAmhsDefault(field, value);
            }
        }
        
        // Also check for direct properties (backward compatibility)
        String recipient = props.getProperty(prefix + "recipient");
        if (recipient != null && !recipient.isEmpty()) {
            subcase.setAmhsDefault("recipient", recipient);
        }
        
        String subject = props.getProperty(prefix + "subject");
        if (subject != null && !subject.isEmpty()) {
            subcase.setAmhsDefault("subject", subject);
        }
        
        String priority = props.getProperty(prefix + "priority");
        if (priority != null && !priority.isEmpty()) {
            subcase.setAmhsDefault("priority", priority);
        }
        
        String content = props.getProperty(prefix + "content");
        if (content != null && !content.isEmpty()) {
            subcase.setAmhsDefault("content", content);
        }
    }

    /**
     * Get list of available test case IDs
     * 
     * @return List of test case IDs (CTSW001, CTSW002, etc.)
     */
    public static List<String> getAvailableTestCaseIds() {
        List<String> ids = new ArrayList<>();
        
        for (int i = 1; i <= 20; i++) {
            String caseId = String.format("CTSW%03d", i);
            String resourceName = TESTCASES_DIR + caseId + PROPERTIES_EXTENSION;
            InputStream inputStream = TestCaseConfigLoader.class.getClassLoader().getResourceAsStream(resourceName);
            
            if (inputStream != null) {
                ids.add(caseId);
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // Ignore close errors
                }
            }
        }
        
        return ids;
    }

    /**
     * Check if a test case configuration exists
     * 
     * @param caseId Test case ID
     * @return true if configuration exists, false otherwise
     */
    public static boolean hasConfiguration(String caseId) {
        String resourceName = TESTCASES_DIR + caseId + PROPERTIES_EXTENSION;
        InputStream inputStream = TestCaseConfigLoader.class.getClassLoader().getResourceAsStream(resourceName);
        
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                // Ignore close errors
            }
            return true;
        }
        
        return false;
    }

    /**
     * Reload a specific test case (useful for runtime updates)
     * 
     * @param caseId Test case ID to reload
     * @return Updated TestCase or null if not found
     */
    public static TestCase reloadTestCase(String caseId) {
        return loadTestCase(caseId);
    }
}
