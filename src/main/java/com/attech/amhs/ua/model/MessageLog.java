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
    private String sender;               // Message sender
    private String subject;              // Message subject
    private String content;              // Message body/content
    private String priority;             // Message priority (LOW, NORMAL, HIGH, URGENT)
    private boolean success;             // Whether send succeeded
    private String errorMessage;         // Error message if send failed
    private String x400Payload;          // Full X.400 payload sent
    private boolean isReceived;          // true if this is a received message, false if sent
    
    // Report-related fields for DR/NDR/IPN tracking
    private String reportType;           // DR, NDR, IPN, or null for regular messages
    private String reportDetails;        // Detailed report information (delivery time, diagnostic codes, etc.)
    private String drRequestType;        // The DR request type set when sending (DR_DELIVERY_REPORT, DR_NO_REPORT, DR_NON_DELIVERY_REPORT)

    public MessageLog() {
        this.timestamp = System.currentTimeMillis();
        this.isReceived = false;
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

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
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
    
    public boolean isReceived() {
        return isReceived;
    }
    
    public void setIsReceived(boolean isReceived) {
        this.isReceived = isReceived;
    }

    // Report-related getters and setters
    
    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getReportDetails() {
        return reportDetails;
    }

    public void setReportDetails(String reportDetails) {
        this.reportDetails = reportDetails;
    }

    public String getDrRequestType() {
        return drRequestType;
    }

    public void setDrRequestType(String drRequestType) {
        this.drRequestType = drRequestType;
    }
}
