package com.attech.amhs.ua.service;

import com.attech.amhs.ua.model.MessageLog;
import com.attech.amhs.ua.model.TestSession;

/**
 * Service for recording test session events (timing, messages)
 */
public class TestSessionRecorder {

    private TestSession testSession;
    private long timerStartTime;
    private boolean timerRunning;

    public TestSessionRecorder() {
        this.testSession = new TestSession();
        this.timerStartTime = 0;
        this.timerRunning = false;
    }

    /**
     * Start the overall test session
     */
    public void startSession() {
        testSession.startSession();
    }

    /**
     * End the overall test session
     */
    public void endSession() {
        testSession.endSession();
    }

    /**
     * Start the session timer
     */
    public void startTimer() {
        if (!timerRunning) {
            timerStartTime = System.currentTimeMillis();
            timerRunning = true;
        }
    }

    /**
     * Stop the session timer and record the elapsed time
     * 
     * @return Elapsed time in milliseconds, or 0 if timer not running
     */
    public long stopTimer() {
        if (timerRunning) {
            long elapsed = System.currentTimeMillis() - timerStartTime;
            timerRunning = false;
            return elapsed;
        }
        return 0;
    }

    /**
     * Get current timer elapsed time (without stopping it)
     * 
     * @return Elapsed time in milliseconds, or 0 if timer not running
     */
    public long getElapsedTime() {
        if (timerRunning) {
            return System.currentTimeMillis() - timerStartTime;
        }
        return 0;
    }

    /**
     * Check if timer is currently running
     * 
     * @return true if timer is running
     */
    public boolean isTimerRunning() {
        return timerRunning;
    }

    /**
     * Reset the timer
     */
    public void resetTimer() {
        timerStartTime = 0;
        timerRunning = false;
    }

    /**
     * Log a sent message
     * 
     * @param testCaseId Test case ID
     * @param testSubcaseId Test subcase ID
     * @param recipient Message recipient
     * @param subject Message subject
     * @param priority Message priority
     * @param success Whether send was successful
     * @param errorMessage Error message if applicable
     */
    public void logMessage(String testCaseId, String testSubcaseId, String recipient,
                          String subject, String priority, boolean success, String errorMessage) {
        MessageLog log = new MessageLog(testCaseId, testSubcaseId);
        log.setRecipient(recipient);
        log.setSubject(subject);
        log.setPriority(priority);
        log.setSuccess(success);
        log.setErrorMessage(errorMessage);
        testSession.addMessageLog(log);
    }

    /**
     * Log a sent message with content
     * 
     * @param testCaseId Test case ID
     * @param testSubcaseId Test subcase ID
     * @param recipient Message recipient
     * @param subject Message subject
     * @param content Message content
     * @param priority Message priority
     * @param success Whether send was successful
     * @param errorMessage Error message if applicable
     * @param x400Payload Full X.400 payload
     */
    public void logMessage(String testCaseId, String testSubcaseId, String recipient,
                          String subject, String content, String priority,
                          boolean success, String errorMessage, String x400Payload) {
        MessageLog log = new MessageLog(testCaseId, testSubcaseId);
        log.setRecipient(recipient);
        log.setSubject(subject);
        log.setContent(content);
        log.setPriority(priority);
        log.setSuccess(success);
        log.setErrorMessage(errorMessage);
        log.setX400Payload(x400Payload);
        testSession.addMessageLog(log);
    }

    /**
     * Get the test session
     * 
     * @return Current TestSession
     */
    public TestSession getTestSession() {
        return testSession;
    }

    /**
     * Create a new test session (clear previous data)
     */
    public void createNewSession() {
        this.testSession = new TestSession();
        resetTimer();
    }

    /**
     * Get message log count
     * 
     * @return Number of logged messages
     */
    public int getMessageLogCount() {
        return testSession.getMessageLogs().size();
    }

    /**
     * Get session duration in milliseconds
     * 
     * @return Session duration, or 0 if not ended
     */
    public long getSessionDuration() {
        return testSession.getSessionDuration();
    }

    /**
     * Get session start time
     * 
     * @return Start time in milliseconds since epoch
     */
    public long getSessionStartTime() {
        return testSession.getSessionStartTime();
    }

    /**
     * Get session end time
     * 
     * @return End time in milliseconds since epoch, or 0 if not ended
     */
    public long getSessionEndTime() {
        return testSession.getSessionEndTime();
    }

    /**
     * Format elapsed time for display
     * 
     * @param milliseconds Time in milliseconds
     * @return Formatted string (HH:MM:SS)
     */
    public static String formatElapsedTime(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        
        seconds = seconds % 60;
        minutes = minutes % 60;
        
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
