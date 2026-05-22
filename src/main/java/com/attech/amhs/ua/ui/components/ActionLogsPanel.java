package com.attech.amhs.ua.ui.components;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Panel for displaying action logs during testing with full message details
 */
public class ActionLogsPanel extends JPanel {
    
    private JTextArea txtLogs;
    private JScrollPane scrollPane;
    private SimpleDateFormat dateFormat;
    
    public ActionLogsPanel() {
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        initUI();
    }
    
    private void initUI() {
        setBorder(new TitledBorder("Action Logs"));
        setLayout(new BorderLayout());
        
        txtLogs = new JTextArea();
        txtLogs.setEditable(false);
        txtLogs.setFont(new Font("Monospaced", Font.PLAIN, 10));
        txtLogs.setLineWrap(true);
        txtLogs.setWrapStyleWord(true);
        
        scrollPane = new JScrollPane(txtLogs);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);
        
        setPreferredSize(new Dimension(0, 150));
    }
    
    /**
     * Log a general action message
     */
    public void logAction(String action) {
        String timestamp = dateFormat.format(new Date());
        appendLog("[" + timestamp + "] " + action);
    }
    
    /**
     * Log a connection event
     */
    public void logConnection(String address, String userOrAddress, String type, boolean success) {
        String timestamp = dateFormat.format(new Date());
        String status = success ? "SUCCESS" : "FAILED";
        appendLog("[" + timestamp + "] CONNECTION_" + status + ": type=" + type + 
                 ", address=" + address + ", user=" + userOrAddress);
    }
    
    /**
     * Log a send message operation with full payload
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
            sb.append("  X.400 Payload:\n");
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
     * Log a receive message operation with full message details
     */
    public void logReceiveMessages(String operationSummary, int messageCount, String details) {
        String timestamp = dateFormat.format(new Date());
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(timestamp).append("] RECEIVE_MESSAGES\n");
        sb.append("  Messages Retrieved: ").append(messageCount).append("\n");
        sb.append("  Summary: ").append(operationSummary).append("\n");
        if (details != null && !details.isEmpty()) {
            sb.append("  Details:\n");
            String[] detailLines = details.split("\n");
            for (String line : detailLines) {
                sb.append("    ").append(line).append("\n");
            }
        }
        appendLog(sb.toString());
    }
    
    /**
     * Log a session control event
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
     * Log an operation error
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
     * Clear all logs
     */
    public void clearLogs() {
        txtLogs.setText("");
    }
    
    /**
     * Get all logs as text
     */
    public String getLogs() {
        return txtLogs.getText();
    }
    
    /**
     * Append text to logs and auto-scroll to bottom
     */
    private void appendLog(String text) {
        txtLogs.append(text);
        if (!text.endsWith("\n")) {
            txtLogs.append("\n");
        }
        txtLogs.append("---\n");
        
        // Auto-scroll to bottom
        SwingUtilities.invokeLater(() -> {
            txtLogs.setCaretPosition(txtLogs.getDocument().getLength());
        });
    }
}
