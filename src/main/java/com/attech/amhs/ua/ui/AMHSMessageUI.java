/*
 * AMHS X.400 Message Tool - Main UI
 * A simple Swing-based UI for sending and receiving AMHS X.400 messages
 */
package com.attech.amhs.ua.ui;

import com.attech.amhs.ua.service.AMHSMessageService;
import com.isode.x400.highlevel.X400APIException;
import com.isode.x400.highlevel.X400Msg.X400_Priority;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Main UI for AMHS X.400 Message Tool
 */
public class AMHSMessageUI extends JFrame {
    
    private AMHSMessageService messageService;
    
    // Configuration fields
    private JTextField txtPresentationAddress;
    private JTextField txtUserOrAddress;
    private JPasswordField txtPassword;
    private JRadioButton radioP7;
    private JRadioButton radioP3;
    
    // Message fields
    private JTextField txtRecipient;
    private JTextField txtSubject;
    private JTextArea txtContent;
    private JComboBox<X400_Priority> comboPriority;
    
    // Status and output
    private JTextArea txtOutput;
    private JLabel lblConnectionStatus;
    
    public AMHSMessageUI() {
        messageService = new AMHSMessageService();
        initUI();
        loadConfigFromFile();
    }
    
    private void initUI() {
        setTitle("AMHS X.400 Message Tool");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create main panel with border
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Configuration Panel
        JPanel configPanel = createConfigurationPanel();
        mainPanel.add(configPanel, BorderLayout.NORTH);
        
        // Message Panel
        JPanel messagePanel = createMessagePanel();
        mainPanel.add(messagePanel, BorderLayout.CENTER);
        
        // Output Panel
        JPanel outputPanel = createOutputPanel();
        mainPanel.add(outputPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createConfigurationPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Connection Configuration"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Presentation Address
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Presentation Address:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        txtPresentationAddress = new JTextField("\"3001\"/Internet=nova.isode.net+3001", 30);
        panel.add(txtPresentationAddress, gbc);
        
        // User/Address
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        panel.add(new JLabel("User/O/R Address:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        txtUserOrAddress = new JTextField("/CN=P7User1/OU=Sales/O=nova/PRMD=Isode/ADMD= /C=GB/", 30);
        panel.add(txtUserOrAddress, gbc);
        
        // Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        panel.add(new JLabel("Password:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        txtPassword = new JPasswordField("secret", 30);
        panel.add(txtPassword, gbc);
        
        // Connection Type
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        ButtonGroup group = new ButtonGroup();
        radioP7 = new JRadioButton("P7 (Message Store)");
        radioP3 = new JRadioButton("P3 (Channel)");
        radioP7.setSelected(true);
        group.add(radioP7);
        group.add(radioP3);
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        radioPanel.add(radioP7);
        radioPanel.add(radioP3);
        panel.add(radioPanel, gbc);
        
        // Connect/Disconnect Button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weightx = 0;
        JButton btnConnect = new JButton("Connect");
        btnConnect.addActionListener(e -> connect());
        panel.add(btnConnect, gbc);
        
        gbc.gridx = 2;
        gbc.gridwidth = 2;
        JButton btnDisconnect = new JButton("Disconnect");
        btnDisconnect.addActionListener(e -> disconnect());
        panel.add(btnDisconnect, gbc);
        
        // Save/Load Config Buttons
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        JButton btnLoadConfig = new JButton("Load Config File");
        btnLoadConfig.addActionListener(e -> loadConfigFromFile());
        panel.add(btnLoadConfig, gbc);
        
        gbc.gridx = 2;
        gbc.gridwidth = 2;
        JButton btnSaveConfig = new JButton("Save Config File");
        btnSaveConfig.addActionListener(e -> saveConfigToFile());
        panel.add(btnSaveConfig, gbc);
        
        // Connection Status Label
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 4;
        lblConnectionStatus = new JLabel("Status: Disconnected");
        lblConnectionStatus.setForeground(Color.RED);
        panel.add(lblConnectionStatus, gbc);
        
        return panel;
    }
    
    private JPanel createMessagePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Message Operations"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Recipient
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Recipient:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        txtRecipient = new JTextField("/CN=P7User1/OU=Sales/O=nova/PRMD=Isode/ADMD= /C=GB/", 30);
        panel.add(txtRecipient, gbc);
        
        // Subject
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        panel.add(new JLabel("Subject:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        txtSubject = new JTextField("Test X.400 Message", 30);
        panel.add(txtSubject, gbc);
        
        // Priority
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        panel.add(new JLabel("Priority:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.5;
        comboPriority = new JComboBox<>(new X400_Priority[]{
            X400_Priority.NORMAL_PRIORITY,
            X400_Priority.LOW_PRIORITY,
            X400_Priority.HIGH_PRIORITY
        });
        panel.add(comboPriority, gbc);
        
        // Content Label
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Content:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        txtContent = new JTextArea(8, 30);
        txtContent.setText("This is a test message sent via AMHS X.400.");
        JScrollPane scrollPane = new JScrollPane(txtContent);
        panel.add(scrollPane, gbc);
        
        // Buttons
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        
        JButton btnSend = new JButton("Send Message");
        btnSend.addActionListener(e -> sendMessage());
        panel.add(btnSend, gbc);
        
        gbc.gridx = 1;
        JButton btnReceive = new JButton("Receive Messages");
        btnReceive.addActionListener(e -> receiveMessages());
        panel.add(btnReceive, gbc);
        
        gbc.gridx = 2;
        JButton btnRefresh = new JButton("Get Mailbox Summary");
        btnRefresh.addActionListener(e -> getMailboxSummary());
        panel.add(btnRefresh, gbc);
        
        gbc.gridx = 3;
        JButton btnClear = new JButton("Clear Output");
        btnClear.addActionListener(e -> txtOutput.setText(""));
        panel.add(btnClear, gbc);
        
        return panel;
    }
    
    private JPanel createOutputPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Output"));
        panel.setPreferredSize(new Dimension(0, 200));
        
        txtOutput = new JTextArea();
        txtOutput.setEditable(false);
        txtOutput.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(txtOutput);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void connect() {
        try {
            String presentationAddress = txtPresentationAddress.getText().trim();
            String userOrAddress = txtUserOrAddress.getText().trim();
            String password = new String(txtPassword.getPassword());
            
            if (radioP3.isSelected()) {
                messageService.configureP3(presentationAddress, userOrAddress, password);
            } else {
                messageService.configureP7(presentationAddress, userOrAddress, password);
            }
            
            messageService.connect();
            lblConnectionStatus.setText("Status: Connected");
            lblConnectionStatus.setForeground(Color.GREEN);
            appendOutput("Successfully connected to X.400 system\n");
            
        } catch (X400APIException e) {
            lblConnectionStatus.setText("Status: Connection Failed");
            lblConnectionStatus.setForeground(Color.RED);
            appendOutput("Connection failed: " + e.getMessage() + "\n");
            appendOutput("Error code: " + e.getNativeErrorCode() + "\n");
        }
    }
    
    private void disconnect() {
        messageService.disconnect();
        lblConnectionStatus.setText("Status: Disconnected");
        lblConnectionStatus.setForeground(Color.RED);
        appendOutput("Disconnected from X.400 system\n");
    }
    
    private void sendMessage() {
        if (!messageService.isConnected()) {
            appendOutput("Error: Not connected. Please connect first.\n");
            return;
        }
        
        try {
            String recipient = txtRecipient.getText().trim();
            String subject = txtSubject.getText().trim();
            String content = txtContent.getText().trim();
            X400_Priority priority = (X400_Priority) comboPriority.getSelectedItem();
            
            if (recipient.isEmpty() || subject.isEmpty() || content.isEmpty()) {
                appendOutput("Error: Recipient, subject, and content are required.\n");
                return;
            }
            
            appendOutput("Sending message...\n");
            String msgId = messageService.sendMessage(recipient, subject, content, priority);
            appendOutput("Message sent successfully!\n");
            appendOutput("Message ID: " + msgId + "\n");
            
        } catch (X400APIException e) {
            appendOutput("Failed to send message: " + e.getMessage() + "\n");
            appendOutput("Error code: " + e.getNativeErrorCode() + "\n");
        }
    }
    
    private void receiveMessages() {
        if (!messageService.isConnected()) {
            appendOutput("Error: Not connected. Please connect first.\n");
            return;
        }
        
        try {
            appendOutput("Receiving messages...\n");
            List<AMHSMessageService.MessageSummary> messages = messageService.receiveMessages(10);
            
            if (messages.isEmpty()) {
                appendOutput("No messages found.\n");
            } else {
                appendOutput("Received " + messages.size() + " message(s):\n");
                appendOutput("========================================\n");
                for (AMHSMessageService.MessageSummary msg : messages) {
                    appendOutput("From: " + msg.getSender() + "\n");
                    appendOutput("Subject: " + msg.getSubject() + "\n");
                    if (msg.getContent() != null && !msg.getContent().isEmpty()) {
                        appendOutput("Content: " + msg.getContent() + "\n");
                    }
                    appendOutput("----------------------------------------\n");
                }
            }
            
        } catch (X400APIException e) {
            appendOutput("Failed to receive messages: " + e.getMessage() + "\n");
            appendOutput("Error code: " + e.getNativeErrorCode() + "\n");
        }
    }
    
    private void getMailboxSummary() {
        if (!messageService.isConnected()) {
            appendOutput("Error: Not connected. Please connect first.\n");
            return;
        }
        
        try {
            appendOutput("Getting mailbox summary...\n");
            List<AMHSMessageService.MessageSummary> messages = messageService.getMailboxSummary();
            
            if (messages.isEmpty()) {
                appendOutput("Mailbox is empty.\n");
            } else {
                appendOutput("Mailbox contains " + messages.size() + " message(s):\n");
                appendOutput("========================================\n");
                for (AMHSMessageService.MessageSummary msg : messages) {
                    appendOutput("From: " + msg.getSender() + "\n");
                    appendOutput("Subject: " + msg.getSubject() + "\n");
                    appendOutput("Time: " + msg.getSubmissionTime() + "\n");
                    appendOutput("Size: " + msg.getContentLength() + " bytes\n");
                    appendOutput("----------------------------------------\n");
                }
            }
            
        } catch (X400APIException e) {
            appendOutput("Failed to get mailbox summary: " + e.getMessage() + "\n");
            appendOutput("Error code: " + e.getNativeErrorCode() + "\n");
        }
    }
    
    private static final String CONFIG_FILE = "connection.properties";

    private void loadConfigFromFile() {
        java.io.File file = new java.io.File(CONFIG_FILE);
        if (!file.exists()) {
            appendOutput("Configuration file (" + CONFIG_FILE + ") not found.\n");
            return;
        }
        
        java.util.Properties props = new java.util.Properties();
        try (java.io.FileInputStream fis = new java.io.FileInputStream(file)) {
            props.load(fis);
            txtPresentationAddress.setText(props.getProperty("presentationAddress", ""));
            txtUserOrAddress.setText(props.getProperty("userOrAddress", ""));
            txtPassword.setText(props.getProperty("password", ""));
            txtRecipient.setText(props.getProperty("recipient", ""));
            txtSubject.setText(props.getProperty("subject", ""));
            
            String connType = props.getProperty("connectionType", "P7");
            if ("P3".equalsIgnoreCase(connType)) {
                radioP3.setSelected(true);
            } else {
                radioP7.setSelected(true);
            }
            
            appendOutput("Loaded configuration from " + CONFIG_FILE + "\n");
        } catch (java.io.IOException e) {
            appendOutput("Failed to load configuration: " + e.getMessage() + "\n");
        }
    }

    private void saveConfigToFile() {
        java.util.Properties props = new java.util.Properties();
        props.setProperty("presentationAddress", txtPresentationAddress.getText().trim());
        props.setProperty("userOrAddress", txtUserOrAddress.getText().trim());
        props.setProperty("password", new String(txtPassword.getPassword()));
        props.setProperty("recipient", txtRecipient.getText().trim());
        props.setProperty("subject", txtSubject.getText().trim());
        props.setProperty("connectionType", radioP3.isSelected() ? "P3" : "P7");
        
        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(CONFIG_FILE)) {
            props.store(fos, "AMHS UA Test Tool Connection Settings");
            appendOutput("Saved configuration to " + CONFIG_FILE + "\n");
        } catch (java.io.IOException e) {
            appendOutput("Failed to save configuration: " + e.getMessage() + "\n");
        }
    }

    private void appendOutput(String text) {
        txtOutput.append(text);
        txtOutput.setCaretPosition(txtOutput.getDocument().getLength());
    }
    
    public static void main(String[] args) {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Use default look and feel
        }
        
        SwingUtilities.invokeLater(() -> {
            AMHSMessageUI ui = new AMHSMessageUI();
            ui.setVisible(true);
        });
    }
}
