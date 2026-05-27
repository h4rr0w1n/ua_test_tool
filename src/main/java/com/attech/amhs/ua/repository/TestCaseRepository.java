package com.attech.amhs.ua.repository;

import com.attech.amhs.ua.model.TestCase;
import com.attech.amhs.ua.model.TestSubcase;
import java.io.*;
import java.util.*;

/**
 * Repository for managing test cases and their results
 */
public class TestCaseRepository {

    private Map<String, TestCase> testCases;
    private String dataFilePath;

    public TestCaseRepository() {
        this.testCases = new LinkedHashMap<>();
        this.dataFilePath = System.getProperty("user.home") + File.separator + 
                           ".amhs_ua_test" + File.separator + "testcases.dat";
    }

    /**
     * Initialize repository with test cases
     * 
     * @param cases List of test cases to load
     */
    public void initializeWithTestCases(List<TestCase> cases) {
        this.testCases.clear();
        for (TestCase testCase : cases) {
            this.testCases.put(testCase.getId(), testCase);
        }
    }

    /**
     * Add a test case to the repository
     * 
     * @param testCase TestCase to add
     */
    public void addTestCase(TestCase testCase) {
        this.testCases.put(testCase.getId(), testCase);
    }

    /**
     * Get a test case by ID
     * 
     * @param id Test case ID (e.g., "CTSW001")
     * @return TestCase or null if not found
     */
    public TestCase getTestCaseById(String id) {
        return this.testCases.get(id);
    }

    /**
     * Get a subcase by full ID path
     * 
     * @param caseId Test case ID
     * @param subcaseId Subcase ID within the case
     * @return TestSubcase or null if not found
     */
    public TestSubcase getSubcaseById(String caseId, String subcaseId) {
        TestCase testCase = this.testCases.get(caseId);
        if (testCase == null) return null;
        
        for (TestSubcase subcase : testCase.getSubcases()) {
            if (subcase.getId().equals(subcaseId)) {
                return subcase;
            }
        }
        return null;
    }

    /**
     * Get all test cases
     * 
     * @return Collection of all test cases
     */
    public Collection<TestCase> getAllTestCases() {
        return new ArrayList<>(this.testCases.values());
    }

    /**
     * Get test cases as ordered list
     * 
     * @return List of test cases in order
     */
    public List<TestCase> getTestCasesList() {
        return new ArrayList<>(this.testCases.values());
    }

    /**
     * Update subcase result
     * 
     * @param caseId Test case ID
     * @param subcaseId Subcase ID
     * @param result "PASS" or "FAIL"
     * @param comment Result comment
     */
    public void markSubcase(String caseId, String subcaseId, String result, String comment) {
        TestSubcase subcase = this.getSubcaseById(caseId, subcaseId);
        if (subcase != null) {
            // Record start time if not already set
            if (subcase.getStartTime() == 0) {
                subcase.setStartTime(System.currentTimeMillis());
            }
            
            subcase.setResult(result);
            subcase.setComment(comment);
            subcase.setMarked(true);
            
            // Record end time
            subcase.setEndTime(System.currentTimeMillis());
        }
    }

    /**
     * Update case result
     * 
     * @param caseId Test case ID
     * @param result "PASS" or "FAIL"
     * @param comment Result comment
     */
    public void markTestCase(String caseId, String result, String comment) {
        TestCase testCase = this.testCases.get(caseId);
        if (testCase != null) {
            testCase.setResult(result);
            testCase.setComment(comment);
            testCase.setMarked(true);
        }
    }

    /**
     * Check if subcase has been marked
     * 
     * @param caseId Test case ID
     * @param subcaseId Subcase ID
     * @return true if marked, false otherwise
     */
    public boolean isSubcaseMarked(String caseId, String subcaseId) {
        TestSubcase subcase = this.getSubcaseById(caseId, subcaseId);
        return subcase != null && subcase.isMarked();
    }

    /**
     * Get mark count for a subcase
     * 
     * @param caseId Test case ID
     * @param subcaseId Subcase ID
     * @return 0 or 1 (once marked, always marked)
     */
    public int getSubcaseMarkCount(String caseId, String subcaseId) {
        return this.isSubcaseMarked(caseId, subcaseId) ? 1 : 0;
    }

    /**
     * Save repository to file
     */
    public void save() {
        try {
            File file = new File(this.dataFilePath);
            file.getParentFile().mkdirs();
            
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(this.testCases);
            }
        } catch (IOException e) {
            System.err.println("Failed to save test cases: " + e.getMessage());
        }
    }

    /**
     * Load repository from file
     */
    @SuppressWarnings("unchecked")
    public void load() {
        try {
            File file = new File(this.dataFilePath);
            if (!file.exists()) {
                return;
            }
            
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                this.testCases = (Map<String, TestCase>) ois.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to load test cases: " + e.getMessage());
        }
    }

    /**
     * Clear all test cases
     */
    public void clear() {
        this.testCases.clear();
    }

    /**
     * Get test case count
     * 
     * @return Number of test cases
     */
    public int getTestCaseCount() {
        return this.testCases.size();
    }

    /**
     * Get total subcase count
     * 
     * @return Total number of subcases across all cases
     */
    public int getSubcaseCount() {
        int count = 0;
        for (TestCase testCase : this.testCases.values()) {
            count += testCase.getSubcases().size();
        }
        return count;
    }

    /**
     * Get marked subcase count
     * 
     * @return Number of marked subcases
     */
    public int getMarkedSubcaseCount() {
        int count = 0;
        for (TestCase testCase : this.testCases.values()) {
            for (TestSubcase subcase : testCase.getSubcases()) {
                if (subcase.isMarked()) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Get passed subcase count
     * 
     * @return Number of passed subcases
     */
    public int getPassedSubcaseCount() {
        int count = 0;
        for (TestCase testCase : this.testCases.values()) {
            for (TestSubcase subcase : testCase.getSubcases()) {
                if (subcase.isMarked() && "PASS".equals(subcase.getResult())) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Get failed subcase count
     * 
     * @return Number of failed subcases
     */
    public int getFailedSubcaseCount() {
        int count = 0;
        for (TestCase testCase : this.testCases.values()) {
            for (TestSubcase subcase : testCase.getSubcases()) {
                if (subcase.isMarked() && "FAIL".equals(subcase.getResult())) {
                    count++;
                }
            }
        }
        return count;
    }
}
