package com.attech.amhs.ua.ui.components;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Bottom-of-UI panel that combines test case / subcase descriptions and expectations
 * with the action log strip.
 *
 * <p>It replaces the previous {@code ActionLogsPanel}. Test documentation is shown
 * by calling {@link #displayTestCaseDescription} or {@link #displaySubcaseDescription},
 * which REPLACES the current text. Send / receive / general action events are
 * appended on top of whatever description is currently displayed via
 * {@link #logSendMessage}, {@link #logReceiveMessage} and {@link #logAction}.</p>
 */
public class DescriptionPanel extends JPanel {

    private JTextArea txtContent;
    private JScrollPane scrollPane;
    private SimpleDateFormat dateFormat;

    public DescriptionPanel() {
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        initUI();
    }

    private void initUI() {
        setBorder(new TitledBorder("Test Case / Subcase Description, Expectations & Action Logs"));
        setLayout(new BorderLayout());

        txtContent = new JTextArea();
        txtContent.setEditable(false);
        txtContent.setFont(new Font("Monospaced", Font.PLAIN, 11));
        txtContent.setLineWrap(true);
        txtContent.setWrapStyleWord(true);

        scrollPane = new JScrollPane(txtContent);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        setPreferredSize(new Dimension(0, 200));
    }

    // ── Description display ───────────────────────────────────────────────

    /**
     * Display test case description with expectations (replaces current text).
     */
    public void displayTestCaseDescription(String testCaseId, String name,
                                           String description, String testCriteria,
                                           String reference) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== TEST CASE: ").append(testCaseId).append(" ===\n\n");
        sb.append("Name: ").append(name).append("\n\n");
        if (description != null && !description.isEmpty()) {
            sb.append("Description:\n").append(description).append("\n\n");
        }
        if (testCriteria != null && !testCriteria.isEmpty()) {
            sb.append("Test Criteria (Expectation):\n").append(testCriteria).append("\n\n");
        }
        if (reference != null && !reference.isEmpty()) {
            sb.append("Reference: ").append(reference).append("\n");
        }
        txtContent.setText(sb.toString());
    }

    /**
     * Display subcase description with expectations (replaces current text).
     */
    public void displaySubcaseDescription(String subcaseId, String name,
                                          String description, String expectation) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== SUBCASE: ").append(subcaseId).append(" ===\n\n");
        sb.append("Name: ").append(name).append("\n\n");
        if (description != null && !description.isEmpty()) {
            sb.append("Description:\n").append(description).append("\n\n");
        }
        if (expectation != null && !expectation.isEmpty()) {
            sb.append("Expected Result:\n").append(expectation).append("\n");
        } else {
            sb.append("Expected Result: Message shall be sent/received according to the test case criteria.\n");
        }
        txtContent.setText(sb.toString());
    }

    public void clear() {
        txtContent.setText("");
    }

    // ── Action log methods (migrated from ActionLogsPanel) ────────────────

    /**
     * Log a general action message.
     */
    public void logAction(String action) {
        String timestamp = dateFormat.format(new Date());
        appendLog("[" + timestamp + "] " + action);
    }

    /**
     * Log a connection event.
     */
    public void logConnection(String address, String userOrAddress, String type, boolean success) {
        String timestamp = dateFormat.format(new Date());
        String status = success ? "SUCCESS" : "FAILED";
        appendLog("[" + timestamp + "] CONNECTION_" + status + ": type=" + type +
                  ", address=" + address + ", user=" + userOrAddress);
    }

    /**
     * Log a send message operation with full payload integrated.
     */
    public void logSendMessage(String testCaseId, String subcaseId, String recipient,
                               String subject, String content, String priority,
                               boolean success, String errorMessage, String x400Payload) {
        String timestamp = dateFormat.format(new Date());
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(timestamp).append("] SEND_MESSAGE_").append(success ? "SUCCESS" : "FAILED").append("\n");
        sb.append("  TestCase: ").append(testCaseId).append(", Subcase: ").append(subcaseId).append("\n");
        sb.append("  Recipient: ").append(recipient).append("\n");
        sb.append("  Subject: ").append(subject).append("\n");
        sb.append("  Priority: ").append(priority).append("\n");
        sb.append("  Content: ").append(content).append("\n");
        if (x400Payload != null && !x400Payload.isEmpty()) {
            sb.append("  Payload Details:\n");
            String[] payloadLines = x400Payload.split("\n");
            for (String line : payloadLines) {
                sb.append("    ").append(line).append("\n");
            }
        }
        if (errorMessage != null && !errorMessage.isEmpty()) {
            sb.append("  Error: ").append(errorMessage).append("\n");
        }
        appendLog(sb.toString());
    }

    /**
     * Log a receive message operation - logs messages as soon as they arrive.
     */
    public void logReceiveMessage(String sender, String subject, String content,
                                  String testCaseId, String subcaseId) {
        String timestamp = dateFormat.format(new Date());
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(timestamp).append("] MESSAGE_RECEIVED\n");
        sb.append("  From: ").append(sender).append("\n");
        sb.append("  Subject: ").append(subject).append("\n");
        if (testCaseId != null) {
            sb.append("  TestCase: ").append(testCaseId);
            if (subcaseId != null) {
                sb.append(" / Subcase: ").append(subcaseId);
            }
            sb.append("\n");
        }
        sb.append("  Content: ").append(content != null ? content : "(no content)").append("\n");
        appendLog(sb.toString());
    }

    /**
     * Log a session control event.
     */
    public void logSessionControl(String action, String details) {
        String timestamp = dateFormat.format(new Date());
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(timestamp).append("] SESSION_").append(action).append("\n");
        if (details != null && !details.isEmpty()) {
            sb.append("  ").append(details).append("\n");
        }
        appendLog(sb.toString());
    }

    /**
     * Log an operation error.
     */
    public void logError(String operation, String errorMessage, String stackTrace) {
        String timestamp = dateFormat.format(new Date());
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(timestamp).append("] ERROR_").append(operation).append("\n");
        sb.append("  Message: ").append(errorMessage).append("\n");
        if (stackTrace != null && !stackTrace.isEmpty()) {
            sb.append("  Stack Trace:\n");
            String[] traceLines = stackTrace.split("\n");
            for (String line : traceLines) {
                sb.append("    ").append(line).append("\n");
            }
        }
        appendLog(sb.toString());
    }

    /**
     * Clear all logs and description text.
     */
    public void clearLogs() {
        txtContent.setText("");
    }

    /**
     * Get all text currently displayed in the panel.
     */
    public String getLogs() {
        return txtContent.getText();
    }

    // ── Internals ─────────────────────────────────────────────────────────

    /**
     * Append text to the panel and auto-scroll to bottom.
     */
    private void appendLog(String text) {
        txtContent.append(text);
        if (!text.endsWith("\n")) {
            txtContent.append("\n");
        }
        txtContent.append("---\n");

        SwingUtilities.invokeLater(() -> {
            txtContent.setCaretPosition(txtContent.getDocument().getLength());
        });
    }
}
