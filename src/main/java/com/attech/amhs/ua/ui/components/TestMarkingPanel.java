package com.attech.amhs.ua.ui.components;

import com.attech.amhs.ua.model.MessageLog;
import com.attech.amhs.ua.model.TestCase;
import com.attech.amhs.ua.model.TestSubcase;
import com.attech.amhs.ua.repository.TestCaseRepository;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * UI Panel for displaying all sent and received messages connected to the User/O/R Address.
 * Upper half shows received messages, lower half shows sent messages.
 */
public class TestMarkingPanel extends JPanel {
    
    private TestCaseRepository repository;
    private List<MessageLog> allMessages;
    private String currentUserAddress;
    private SimpleDateFormat dateFormat;
    
    // Received messages (upper half)
    private JTextArea txtReceivedMessages;
    private JScrollPane scrollReceivedPane;
    
    // Sent messages (lower half)
    private JTextArea txtSentMessages;
    private JScrollPane scrollSentPane;
    
    // Main split pane dividing received/sent
    private JSplitPane mainSplitPane;
    
    public TestMarkingPanel(TestCaseRepository repository) {
        this.repository = repository;
        this.allMessages = new ArrayList<>();
        this.currentUserAddress = "";
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        initUI();
    }
    
    private void initUI() {
        setLayout(new BorderLayout());
        setBorder(new TitledBorder("Messages - User/O/R Address Filtered"));
        
        // Received messages panel (upper half)
        JPanel receivedPanel = createReceivedMessagesPanel();
        
        // Sent messages panel (lower half)
        JPanel sentPanel = createSentMessagesPanel();
        
        // Split pane with received on top, sent on bottom
        mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, receivedPanel, sentPanel);
        mainSplitPane.setDividerLocation(0.5);
        mainSplitPane.setResizeWeight(0.5);
        mainSplitPane.setContinuousLayout(true);
        
        add(mainSplitPane, BorderLayout.CENTER);
        
        setPreferredSize(new Dimension(400, 300));
    }
    
    private JPanel createReceivedMessagesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Received Messages"));
        
        txtReceivedMessages = new JTextArea();
        txtReceivedMessages.setEditable(false);
        txtReceivedMessages.setFont(new Font("Monospaced", Font.PLAIN, 10));
        txtReceivedMessages.setLineWrap(true);
        txtReceivedMessages.setWrapStyleWord(true);
        
        scrollReceivedPane = new JScrollPane(txtReceivedMessages);
        scrollReceivedPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollReceivedPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        panel.add(scrollReceivedPane, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createSentMessagesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Sent Messages"));
        
        txtSentMessages = new JTextArea();
        txtSentMessages.setEditable(false);
        txtSentMessages.setFont(new Font("Monospaced", Font.PLAIN, 10));
        txtSentMessages.setLineWrap(true);
        txtSentMessages.setWrapStyleWord(true);
        
        scrollSentPane = new JScrollPane(txtSentMessages);
        scrollSentPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollSentPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        panel.add(scrollSentPane, BorderLayout.CENTER);
        return panel;
    }
    
    /**
     * Add a message to the display
     * @param message The message log entry
     * @param isReceived true if received message, false if sent
     */
    public void addMessage(MessageLog message, boolean isReceived) {
        message.setIsReceived(isReceived);
        allMessages.add(message);
        refreshDisplay();
    }
    
    /**
     * Set the current User/O/R Address filter
     */
    public void setUserAddress(String userAddress) {
        this.currentUserAddress = userAddress != null ? userAddress.trim() : "";
        refreshDisplay();
    }
    
    /**
     * Refresh the display based on current filter
     */
    private void refreshDisplay() {
        StringBuilder receivedBuilder = new StringBuilder();
        StringBuilder sentBuilder = new StringBuilder();
        
        int receivedCount = 0;
        int sentCount = 0;
        boolean limitActive = SettingsDialog.isLimitMessages();
        int limit = SettingsDialog.getMessageLimit();
        
        for (int i = allMessages.size() - 1; i >= 0; i--) {
            MessageLog message = allMessages.get(i);
            // Filter by User/O/R Address - check if message is related to current address
            boolean isMatching = currentUserAddress.isEmpty() || 
                                messageContainsAddress(message, currentUserAddress);
            
            if (isMatching) {
                String formatted = formatMessage(message);
                
                // Use the isReceived flag to determine which panel to show the message in
                if (message.isReceived()) {
                    if (!limitActive || receivedCount < limit) {
                        // Received message - show in received panel
                        receivedBuilder.append(formatted).append("\n---\n");
                        receivedCount++;
                    }
                } else {
                    if (!limitActive || sentCount < limit) {
                        // Sent message - show in sent panel
                        sentBuilder.append(formatted).append("\n---\n");
                        sentCount++;
                    }
                }
                
                if (limitActive && receivedCount >= limit && sentCount >= limit) {
                    break;
                }
            }
        }
        
        txtReceivedMessages.setText(receivedBuilder.toString());
        txtSentMessages.setText(sentBuilder.toString());
        
        // Auto-scroll
        SwingUtilities.invokeLater(() -> {
            if (SettingsDialog.isAutoScrollMessages()) {
                txtReceivedMessages.setCaretPosition(0);
                txtSentMessages.setCaretPosition(0);
            }
        });
    }
    
    /**
     * Check if a message contains the given address
     */
    private boolean messageContainsAddress(MessageLog message, String address) {
        if (message.getRecipient() != null && message.getRecipient().contains(address)) {
            return true;
        }
        // Could also check sender field if available in MessageLog
        return false;
    }
    
    private String formatMessage(MessageLog message) {
        StringBuilder sb = new StringBuilder();
        
        // Timestamp
        String timestamp = dateFormat.format(new Date(message.getTimestamp()));
        sb.append("[").append(timestamp).append("]\n");
        
        // Test Case and Subcase
        if (message.getTestCaseId() != null) {
            sb.append("Case: ").append(message.getTestCaseId());
            if (message.getTestSubcaseId() != null) {
                sb.append(" / ").append(message.getTestSubcaseId());
            }
            sb.append("\n");
        }
        
        // Recipient/From
        if (message.getRecipient() != null) {
            sb.append("Recipient: ").append(abbreviateAddress(message.getRecipient())).append("\n");
        }
        
        // Subject
        if (message.getSubject() != null) {
            sb.append("Subject: ").append(message.getSubject()).append("\n");
        }
        
        // Priority
        if (message.getPriority() != null) {
            sb.append("Priority: ").append(message.getPriority()).append("\n");
        }
        
        // Content - show full content since AMHS messages are typically short
        if (message.getContent() != null && !message.getContent().isEmpty()) {
            sb.append("Content:\n").append(message.getContent()).append("\n");
        }
        
        // Status
        sb.append("Status: ").append(message.isSuccess() ? "SUCCESS" : "FAILED");
        if (message.getErrorMessage() != null && !message.getErrorMessage().isEmpty()) {
            sb.append(" - ").append(message.getErrorMessage());
        }
        sb.append("\n");
        
        return sb.toString();
    }
    
    /**
     * Abbreviate a long address for display
     */
    private String abbreviateAddress(String address) {
        if (address == null || address.length() <= 80) {
            return address;
        }
        return address.substring(0, 77) + "...";
    }
    
    /**
     * Clear all messages
     */
    public void clearMessages() {
        allMessages.clear();
        txtReceivedMessages.setText("");
        txtSentMessages.setText("");
    }
    
    /**
     * Get all messages
     */
    public List<MessageLog> getAllMessages() {
        return new ArrayList<>(allMessages);
    }
}
