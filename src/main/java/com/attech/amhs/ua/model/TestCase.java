package com.attech.amhs.ua.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a test case (e.g., CTSW001) with multiple subcases
 */
public class TestCase implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;                      // e.g., "CTSW001"
    private String name;                    // Descriptive name
    private String description;             // Test case description
    private List<TestSubcase> subcases;     // List of subcases
    private String result;                  // "PASS" or "FAIL" or null if not tested
    private String comment;                 // Test result comment
    private boolean marked;                 // Whether this case has been marked

    public TestCase() {
        this.subcases = new ArrayList<>();
        this.marked = false;
    }

    public TestCase(String id, String name) {
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

    public List<TestSubcase> getSubcases() {
        return subcases;
    }

    public void setSubcases(List<TestSubcase> subcases) {
        this.subcases = subcases;
    }

    public void addSubcase(TestSubcase subcase) {
        this.subcases.add(subcase);
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
        return id + " - " + name + " (" + subcases.size() + " subcases)";
    }
}
