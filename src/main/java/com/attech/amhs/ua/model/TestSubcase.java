package com.attech.amhs.ua.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a subcase within a test case (e.g., CTSW001.1)
 */
public class TestSubcase implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;                          // e.g., "CTSW001.1"
    private String name;                        // Descriptive name
    private String description;                 // Test description
    private Map<String, String> amhsDefaults;  // Default X.400 message fields
    private String result;                      // "PASS" or "FAIL" or null if not tested
    private String comment;                     // Test result comment
    private boolean marked;                     // Whether this subcase has been marked
    
    public TestSubcase() {
        this.amhsDefaults = new HashMap<>();
        this.marked = false;
    }
    
    public TestSubcase(String id, String name) {
        this();
        this.id = id;
        this.name = name;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, String> getAmhsDefaults() {
        return amhsDefaults;
    }

    public void setAmhsDefaults(Map<String, String> amhsDefaults) {
        this.amhsDefaults = amhsDefaults;
    }

    public void setAmhsDefault(String key, String value) {
        this.amhsDefaults.put(key, value);
    }

    public String getAmhsDefault(String key) {
        return this.amhsDefaults.get(key);
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isMarked() {
        return marked;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
    }

    @Override
    public String toString() {
        return id + " - " + name;
    }
}
