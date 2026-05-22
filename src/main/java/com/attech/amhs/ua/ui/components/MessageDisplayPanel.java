package com.attech.amhs.ua.ui.components;

import com.attech.amhs.ua.model.MessageLog;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Panel for displaying sent and received messages filtered by User/O/R Address
 */
public class MessageDisplayPanel extends JPanel {
    
    private JTextArea txtReceivedMessages;
    private JTextArea txtSentMessages;
    private JScrollPane scrollReceivedPane;
    private JScrollPane scrollSentPane;
    private JSplitPane splitPane;
    private SimpleDateFormat dateFormat;
    private List<MessageLog> allMessages;
    private String currentUserAddress;
    
    public MessageDisplayPanel() {
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.allMessages = new ArrayList<>();
        this.currentUserAddress = "";
        initUI();
    }
    
    private void initUI() {
        setLayout(new BorderLayout());
        setBorder(new TitledBorder("Messages (Received / Sent)"));
        
        // Received messages panel (upper)
        JPanel receivedPanel = createReceivedMessagesPanel();
        
        // Sent messages panel (lower)
        JPanel sentPanel = createSentMessagesPanel();
        
        // Split pane with received on top, sent on bottom
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, receivedPanel, sentPanel);
        splitPane.setDividerLocation(0.5);
        splitPane.setResizeWeight(0.5);
        splitPane.setContinuousLayout(true);
        
        add(splitPane, BorderLayout.CENTER);
        
        setPreferredSize(new Dimension(300, 250));
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
     * @param isReceived true if received message, false if sent
     */
    public void addMessage(MessageLog message, boolean isReceived) {
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
        
        for (MessageLog message : allMessages) {
            // For received messages, we check if the message is from a sender matching the filter
            // For sent messages, we check if the recipient matches the filter
            boolean isMatching = currentUserAddress.isEmpty() || 
                                (message.getRecipient() != null && message.getRecipient().contains(currentUserAddress));
            
            if (isMatching) {
                String formatted = formatMessage(message);
                
                // Simple heuristic: if it has a recipient, treat as sent; otherwise received
                // In a real scenario, you'd have a message direction field
                if (message.getRecipient() != null && !message.getRecipient().isEmpty()) {
                    sentBuilder.append(formatted).append("\n---\n");
                } else {
                    receivedBuilder.append(formatted).append("\n---\n");
                }
            }
        }
        
        txtReceivedMessages.setText(receivedBuilder.toString());
        txtSentMessages.setText(sentBuilder.toString());
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
        
        // Content (abbreviated if too long)
        if (message.getContent() != null && !message.getContent().isEmpty()) {
            String content = message.getContent();
            if (content.length() > 100) {
                content = content.substring(0, 100) + "...";
            }
            sb.append("Content: ").append(content).append("\n");
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
        if (address == null || address.length() <= 50) {
            return address;
        }
        return address.substring(0, 47) + "...";
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
