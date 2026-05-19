package com.attech.amhs.ua.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a testing session with timing and message logs
 */
public class TestSession implements Serializable {
    private static final long serialVersionUID = 1L;

    private long sessionStartTime;           // Session start time (milliseconds)
    private long sessionEndTime;             // Session end time (milliseconds)
    private List<MessageLog> messageLogs;    // Log of sent messages
    private List<TestCase> testCases;        // All test cases in this session

    public TestSession() {
        this.messageLogs = new ArrayList<>();
        this.testCases = new ArrayList<>();
        this.sessionStartTime = 0;
        this.sessionEndTime = 0;
    }

    public void startSession() {
        this.sessionStartTime = System.currentTimeMillis();
    }

    public void endSession() {
        this.sessionEndTime = System.currentTimeMillis();
    }

    public long getSessionDuration() {
        if (sessionEndTime == 0) {
            return 0;
        }
        return sessionEndTime - sessionStartTime;
    }

    public long getSessionStartTime() {
        return sessionStartTime;
    }

    public void setSessionStartTime(long sessionStartTime) {
        this.sessionStartTime = sessionStartTime;
    }

    public long getSessionEndTime() {
        return sessionEndTime;
    }

    public void setSessionEndTime(long sessionEndTime) {
        this.sessionEndTime = sessionEndTime;
    }

    public List<MessageLog> getMessageLogs() {
        return messageLogs;
    }

    public void setMessageLogs(List<MessageLog> messageLogs) {
        this.messageLogs = messageLogs;
    }

    public void addMessageLog(MessageLog log) {
        this.messageLogs.add(log);
    }

    public List<TestCase> getTestCases() {
        return testCases;
    }

    public void setTestCases(List<TestCase> testCases) {
        this.testCases = testCases;
    }

    public void addTestCase(TestCase testCase) {
        this.testCases.add(testCase);
    }
}
