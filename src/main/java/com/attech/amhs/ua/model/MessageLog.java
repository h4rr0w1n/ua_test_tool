package com.attech.amhs.ua.model;

import java.io.Serializable;

/**
 * Represents a logged AMHS message send event
 */
public class MessageLog implements Serializable {
    private static final long serialVersionUID = 1L;

    private String testCaseId;           // Associated test case (e.g., CTSW001)
    private String testSubcaseId;        // Associated test subcase (e.g., CTSW001.1)
    private long timestamp;              // When message was sent
    private String recipient;            // Message recipient
    private String subject;              // Message subject
    private String content;              // Message body/content
    private String priority;             // Message priority (LOW, NORMAL, HIGH, URGENT)
    private boolean success;             // Whether send succeeded
    private String errorMessage;         // Error message if send failed
    private String x400Payload;          // Full X.400 payload sent

    public MessageLog() {
        this.timestamp = System.currentTimeMillis();
    }

    public MessageLog(String testCaseId, String testSubcaseId) {
        this();
        this.testCaseId = testCaseId;
        this.testSubcaseId = testSubcaseId;
    }

    // Getters and Setters
    public String getTestCaseId() {
        return testCaseId;
    }

    public void setTestCaseId(String testCaseId) {
        this.testCaseId = testCaseId;
    }

    public String getTestSubcaseId() {
        return testSubcaseId;
    }

    public void setTestSubcaseId(String testSubcaseId) {
        this.testSubcaseId = testSubcaseId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getX400Payload() {
        return x400Payload;
    }

    public void setX400Payload(String x400Payload) {
        this.x400Payload = x400Payload;
    }
}
