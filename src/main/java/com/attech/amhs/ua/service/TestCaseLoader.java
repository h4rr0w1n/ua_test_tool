package com.attech.amhs.ua.service;

import com.attech.amhs.ua.model.TestCase;
import com.attech.amhs.ua.model.TestSubcase;
import java.util.ArrayList;
import java.util.List;

/**
 * Loader for initializing test cases from ICAO EUR Doc 047 Appendix A
 * CTSW001 - CTSW020 test cases
 */
public class TestCaseLoader {

    /**
     * Load default test cases template (CTSW001-CTSW020)
     * Users can customize defaults in the returned list
     * 
     * @return List of test cases with empty defaults (to be populated by user)
     */
    public static List<TestCase> loadDefaultTestCases() {
        List<TestCase> testCases = new ArrayList<>();
        
        // CTSW001 - CTSW020
        for (int i = 1; i <= 20; i++) {
            String caseId = String.format("CTSW%03d", i);
            TestCase testCase = new TestCase();
            testCase.setId(caseId);
            testCase.setName("Test Case " + caseId);
            testCase.setDescription("AMHS X.400 message test case from ICAO EUR Doc 047 Appendix A");
            
            // Add default 1-2 subcases per case
            TestSubcase subcase1 = new TestSubcase();
            subcase1.setId(caseId + ".1");
            subcase1.setName(caseId + " - Subcase 1");
            subcase1.setDescription("Default subcase for " + caseId);
            // Defaults are empty initially - user fills them in via UI
            testCase.addSubcase(subcase1);
            
            // Add second subcase for some cases
            if (i % 2 == 1) {  // Odd numbered cases get second subcase
                TestSubcase subcase2 = new TestSubcase();
                subcase2.setId(caseId + ".2");
                subcase2.setName(caseId + " - Subcase 2");
                subcase2.setDescription("Alternative subcase for " + caseId);
                testCase.addSubcase(subcase2);
            }
            
            testCases.add(testCase);
        }
        
        return testCases;
    }

    /**
     * Load test cases with SAMPLE defaults (for testing/demo)
     * Replace with actual defaults from ICAO EUR Doc 047 when available
     * 
     * @return List of test cases with sample AMHS defaults
     */
    public static List<TestCase> loadTestCasesWithSampleDefaults() {
        List<TestCase> testCases = loadDefaultTestCases();
        
        // Set sample defaults for demonstration
        // In production, these would come from ICAO EUR Doc 047 Appendix A specifications
        for (TestCase testCase : testCases) {
            for (TestSubcase subcase : testCase.getSubcases()) {
                // Sample X.400 defaults (customize per actual test case requirements)
                subcase.setAmhsDefault("recipient", "/CN=TestRecipient/OU=Test/O=TestOrg/PRMD=TestPRMD/ADMD=/C=US/");
                subcase.setAmhsDefault("subject", "Test Message: " + subcase.getId());
                subcase.setAmhsDefault("priority", "NORMAL");
                subcase.setAmhsDefault("content", "This is a test message for " + subcase.getId() + 
                                                 ". Replace with actual test content from ICAO EUR Doc 047.");
            }
        }
        
        return testCases;
    }

    /**
     * Create a single test case programmatically
     * 
     * @param caseId Test case ID (e.g., "CTSW001")
     * @param name Test case name
     * @param subcaseCount Number of subcases to create
     * @return TestCase object
     */
    public static TestCase createTestCase(String caseId, String name, int subcaseCount) {
        TestCase testCase = new TestCase();
        testCase.setId(caseId);
        testCase.setName(name);
        testCase.setDescription("Test case " + caseId);
        
        for (int i = 1; i <= subcaseCount; i++) {
            TestSubcase subcase = new TestSubcase();
            subcase.setId(caseId + "." + i);
            subcase.setName(caseId + " - Subcase " + i);
            subcase.setDescription("Subcase " + i + " for " + caseId);
            testCase.addSubcase(subcase);
        }
        
        return testCase;
    }

    /**
     * Create a test case with predefined AMHS defaults
     * 
     * @param caseId Test case ID
     * @param name Test case name
     * @param recipient Default recipient
     * @param subject Default subject
     * @param priority Default priority
     * @param content Default content
     * @return TestCase with subcase having AMHS defaults
     */
    public static TestCase createTestCaseWithDefaults(String caseId, String name,
                                                       String recipient, String subject,
                                                       String priority, String content) {
        TestCase testCase = createTestCase(caseId, name, 1);
        TestSubcase subcase = testCase.getSubcases().get(0);
        
        subcase.setAmhsDefault("recipient", recipient);
        subcase.setAmhsDefault("subject", subject);
        subcase.setAmhsDefault("priority", priority);
        subcase.setAmhsDefault("content", content);
        
        return testCase;
    }
}
