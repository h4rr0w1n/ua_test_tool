/*
 * AMHS X.400 Message Tool - Main UI
 * A simple Swing-based UI for sending and receiving AMHS X.400 messages
 */
package com.attech.amhs.ua.ui;

import com.attech.amhs.ua.model.TestCase;
import com.attech.amhs.ua.model.TestSubcase;
import com.attech.amhs.ua.repository.TestCaseRepository;
import com.attech.amhs.ua.service.AMHSMessageService;
import com.attech.amhs.ua.service.TestCaseLoader;
import com.attech.amhs.ua.service.TestSessionRecorder;
import com.attech.amhs.ua.ui.components.*;
import com.isode.x400.highlevel.X400APIException;
import com.isode.x400.highlevel.X400Msg.X400_Priority;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Main UI for AMHS X.400 Message Tool with Test Case Management
 */
public class AMHSMessageUI extends JFrame {
    
    private AMHSMessageService messageService;
    private TestCaseRepository repository;
    private TestSessionRecorder recorder;
    
    // UI Components
    private TestCaseSelectorPanel selectorPanel;
    private TestControlPanel controlPanel;
    private TestMarkingPanel markingPanel;
    private ActionLogsPanel actionLogsPanel;
    private MessageDisplayPanel messageDisplayPanel;
    
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
    private JLabel lblConnectionStatus;
    
    public AMHSMessageUI() {
        messageService = new AMHSMessageService();
        repository = new TestCaseRepository();
        recorder = new TestSessionRecorder();
        
        // Initialize test cases
        initializeTestCases();
        
        initUI();
        loadConfigFromFile();
    }
    
    private void initializeTestCases() {
        List<TestCase> testCases = TestCaseLoader.loadDefaultTestCases();
        repository.initializeWithTestCases(testCases);
    }
    
    private void initUI() {
        setTitle("AMHS X.400 Message Tool - Test Case Manager");
        setSize(1400, 950);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create main panel with border
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        // LEFT PANEL - Test Case Selection and Session Control
        JPanel leftPanel = createLeftPanel();
        
        // CENTER PANEL - Configuration and Message Operations
        JPanel centerPanel = createCenterPanel();
        
        // RIGHT PANEL - Message Display
        messageDisplayPanel = new MessageDisplayPanel();
        
        // BOTTOM PANEL - Test Marking (Subcase/Case Marking)
        markingPanel = new TestMarkingPanel(repository);
        JScrollPane markingScrollPane = new JScrollPane(markingPanel);
        markingScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        markingScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        // Create main horizontal split: left panel and center-right area
        JSplitPane mainHorizontalSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, centerPanel);
        mainHorizontalSplit.setDividerLocation(280);
        mainHorizontalSplit.setResizeWeight(0.0);
        mainHorizontalSplit.setContinuousLayout(true);
        
        // Create split for center-right: center and right message display
        JSplitPane centerRightSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, centerPanel, messageDisplayPanel);
        centerRightSplit.setDividerLocation(600);
        centerRightSplit.setResizeWeight(0.7);
        centerRightSplit.setContinuousLayout(true);
        
        // Fix the main panel layout
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(centerRightSplit, BorderLayout.CENTER);
        mainPanel.add(markingScrollPane, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        
        // Test Case Selector (top, resizable)
        selectorPanel = new TestCaseSelectorPanel(repository);
        selectorPanel.addDefaultsLoadedListener("main", this::handleLoadDefaultsForSubcase);
        selectorPanel.addCopyDefaultsListener("main", this::handleCopyDefaults);
        selectorPanel.addSendDefaultsListener("main", this::handleSendDefaults);
        JScrollPane selectorScroll = new JScrollPane(selectorPanel);
        selectorScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        selectorScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        // Test Control Panel and Action Logs (middle/bottom, split)
        JPanel controlLogPanel = new JPanel(new BorderLayout(5, 5));
        
        // Test Control Panel
        controlPanel = new TestControlPanel(repository, recorder);
        JScrollPane controlScroll = new JScrollPane(controlPanel);
        controlScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        // Action Logs Panel
        actionLogsPanel = new ActionLogsPanel();
        
        JSplitPane controlLogSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, controlScroll, actionLogsPanel);
        controlLogSplit.setDividerLocation(0.4);
        controlLogSplit.setResizeWeight(0.4);
        controlLogSplit.setContinuousLayout(true);
        
        controlLogPanel.add(controlLogSplit, BorderLayout.CENTER);
        
        // Main left panel split: selector on top, control+logs on bottom
        JSplitPane leftSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, selectorScroll, controlLogPanel);
        leftSplit.setDividerLocation(0.35);
        leftSplit.setResizeWeight(0.35);
        leftSplit.setContinuousLayout(true);
        
        panel.add(leftSplit, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        
        // Configuration Panel (top, resizable)
        JPanel configPanel = createConfigurationPanel();
        JScrollPane configScroll = new JScrollPane(configPanel);
        configScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        configScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        // Message Panel (middle, resizable)
        JPanel messagePanel = createMessagePanel();
        JScrollPane messageScroll = new JScrollPane(messagePanel);
        messageScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        messageScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        // Split configuration and message
        JSplitPane centerSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, configScroll, messageScroll);
        centerSplit.setDividerLocation(0.3);
        centerSplit.setResizeWeight(0.3);
        centerSplit.setContinuousLayout(true);
        
        panel.add(centerSplit, BorderLayout.CENTER);
        
        return panel;
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
        JButton btnClear = new JButton("Clear Logs");
        btnClear.addActionListener(e -> {
            if (actionLogsPanel != null) {
                actionLogsPanel.clearLogs();
            }
        });
        panel.add(btnClear, gbc);
        
        return panel;
    }
    
    private void connect() {
        lblConnectionStatus.setText("Status: Connecting...");
        lblConnectionStatus.setForeground(Color.BLUE);
        appendOutput("Initiating connection to X.400 system...\n");
        
        String presentationAddress = txtPresentationAddress.getText().trim();
        String userOrAddress = txtUserOrAddress.getText().trim();
        String password = new String(txtPassword.getPassword());
        
        if (radioP3.isSelected()) {
            messageService.configureP3(presentationAddress, userOrAddress, password);
        } else {
            messageService.configureP7(presentationAddress, userOrAddress, password);
        }
        
        // Set a longer timeout for more reliable connections (90 seconds)
        messageService.setConnectTimeout(90);
        
        new Thread(() -> {
            try {
                messageService.connect();
                SwingUtilities.invokeLater(() -> {
                    lblConnectionStatus.setText("Status: Connected");
                    lblConnectionStatus.setForeground(Color.GREEN);
                    appendOutput("Successfully connected to X.400 system\n");
                });
            } catch (Throwable t) {
                SwingUtilities.invokeLater(() -> {
                    lblConnectionStatus.setText("Status: Connection Failed");
                    lblConnectionStatus.setForeground(Color.RED);
                    
                    String errorMsg = t.getMessage();
                    appendOutput("Connection failed: " + errorMsg + "\n\n");
                    
                    // Provide specific guidance for native library errors
                    if (t instanceof java.lang.UnsatisfiedLinkError || 
                        (errorMsg != null && errorMsg.contains("Native library") && errorMsg.contains("DLL"))) {
                        appendOutput("=== NATIVE LIBRARY ERROR ===\n");
                        appendOutput("The Isode X.400 native libraries are not properly installed.\n");
                        appendOutput("Required files:\n");
                        appendOutput("  - pthreadvc2.dll\n");
                        appendOutput("  - CJavaInterface.dll\n");
                        appendOutput("Installation location: lib/amd64/ or lib/ directory\n\n");
                        appendOutput("Solution:\n");
                        appendOutput("1. Locate your Isode X.400 SDK installation\n");
                        appendOutput("2. Copy the above DLL files to: lib/amd64/\n");
                        appendOutput("3. Rebuild: mvn clean package\n");
                        appendOutput("4. Try connecting again\n\n");
                        appendOutput("For more details, see: README.md\n\n");
                    }
                    
                    // Provide additional troubleshooting for timeout errors
                    if (errorMsg != null && errorMsg.contains("timeout")) {
                        appendOutput("\n=== CONNECTION TIMEOUT TROUBLESHOOTING ===\n");
                        appendOutput("The server did not respond within the timeout period.\n");
                        appendOutput("Please check:\n");
                        appendOutput("1. Server is running and accessible at: " + presentationAddress + "\n");
                        appendOutput("2. Network connectivity: ping the server IP\n");
                        appendOutput("3. Port is open: telnet <server_ip> <port>\n");
                        appendOutput("4. Firewall settings allow the connection\n");
                        appendOutput("5. Try switching between P7 and P3 connection types\n");
                        appendOutput("6. Verify credentials (user/O/R address and password)\n\n");
                    }
                    
                    if (t instanceof X400APIException) {
                        appendOutput("Error code: " + ((X400APIException) t).getNativeErrorCode() + "\n");
                    } else {
                        java.io.StringWriter sw = new java.io.StringWriter();
                        t.printStackTrace(new java.io.PrintWriter(sw));
                        appendOutput(sw.toString() + "\n");
                    }
                });
            }
        }).start();
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
        
        String recipient = txtRecipient.getText().trim();
        String subject = txtSubject.getText().trim();
        String content = txtContent.getText().trim();
        X400_Priority priority = (X400_Priority) comboPriority.getSelectedItem();
        
        if (recipient.isEmpty() || subject.isEmpty() || content.isEmpty()) {
            appendOutput("Error: Recipient, subject, and content are required.\n");
            return;
        }
        
        appendOutput("Sending message...\n");
        
        new Thread(() -> {
            try {
                String msgId = messageService.sendMessage(recipient, subject, content, priority);
                SwingUtilities.invokeLater(() -> {
                    appendOutput("Message sent successfully!\n");
                    appendOutput("Message ID: " + msgId + "\n");
                });
            } catch (Throwable t) {
                SwingUtilities.invokeLater(() -> {
                    appendOutput("Failed to send message: " + t.getMessage() + "\n");
                    if (t instanceof X400APIException) {
                        appendOutput("Error code: " + ((X400APIException) t).getNativeErrorCode() + "\n");
                    }
                });
            }
        }).start();
    }
    
    private void receiveMessages() {
        if (!messageService.isConnected()) {
            appendOutput("Error: Not connected. Please connect first.\n");
            return;
        }
        
        appendOutput("Receiving messages...\n");
        
        new Thread(() -> {
            try {
                List<AMHSMessageService.MessageSummary> messages = messageService.receiveMessages(10);
                SwingUtilities.invokeLater(() -> {
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
                });
            } catch (Throwable t) {
                SwingUtilities.invokeLater(() -> {
                    appendOutput("Failed to receive messages: " + t.getMessage() + "\n");
                    if (t instanceof X400APIException) {
                        appendOutput("Error code: " + ((X400APIException) t).getNativeErrorCode() + "\n");
                    }
                });
            }
        }).start();
    }
    
    private void getMailboxSummary() {
        if (!messageService.isConnected()) {
            appendOutput("Error: Not connected. Please connect first.\n");
            return;
        }
        
        appendOutput("Getting mailbox summary...\n");
        
        new Thread(() -> {
            try {
                List<AMHSMessageService.MessageSummary> messages = messageService.getMailboxSummary();
                SwingUtilities.invokeLater(() -> {
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
                });
            } catch (Throwable t) {
                SwingUtilities.invokeLater(() -> {
                    appendOutput("Failed to get mailbox summary: " + t.getMessage() + "\n");
                    if (t instanceof X400APIException) {
                        appendOutput("Error code: " + ((X400APIException) t).getNativeErrorCode() + "\n");
                    }
                });
            }
        }).start();
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
        if (actionLogsPanel != null) {
            // Parse the text and log appropriately
            actionLogsPanel.logAction(text.trim());
        }
    }
    
    /**
     * Handle copying default configuration to Message Operations
     */
    private void handleCopyDefaults() {
        Map<String, String> defaults = selectorPanel.getSelectedSubcaseDefaults();
        if (!defaults.isEmpty()) {
            // Load recipient
            if (defaults.containsKey("recipient")) {
                txtRecipient.setText(defaults.get("recipient"));
            }
            
            // Load subject
            if (defaults.containsKey("subject")) {
                txtSubject.setText(defaults.get("subject"));
            }
            
            // Load priority
            if (defaults.containsKey("priority")) {
                String priorityStr = defaults.get("priority");
                if ("LOW".equalsIgnoreCase(priorityStr)) {
                    comboPriority.setSelectedItem(X400_Priority.LOW_PRIORITY);
                } else if ("HIGH".equalsIgnoreCase(priorityStr)) {
                    comboPriority.setSelectedItem(X400_Priority.HIGH_PRIORITY);
                } else if ("URGENT".equalsIgnoreCase(priorityStr)) {
                    comboPriority.setSelectedItem(X400_Priority.HIGH_PRIORITY);
                } else {
                    comboPriority.setSelectedItem(X400_Priority.NORMAL_PRIORITY);
                }
            }
            
            // Load content
            if (defaults.containsKey("content")) {
                txtContent.setText(defaults.get("content"));
            }
            
            if (actionLogsPanel != null) {
                actionLogsPanel.logAction("Defaults copied to Message Operations for subcase: " + 
                                         selectorPanel.getSelectedSubcase().getId());
            }
        }
    }
    
    /**
     * Handle sending message with default configuration
     */
    private void handleSendDefaults() {
        if (!messageService.isConnected()) {
            if (actionLogsPanel != null) {
                actionLogsPanel.logError("SEND", "Not connected to X.400 system", null);
            }
            return;
        }
        
        // First copy defaults
        handleCopyDefaults();
        
        // Then send
        new Thread(() -> {
            try {
                String recipient = txtRecipient.getText().trim();
                String subject = txtSubject.getText().trim();
                String content = txtContent.getText().trim();
                X400_Priority priority = (X400_Priority) comboPriority.getSelectedItem();
                
                if (recipient.isEmpty() || subject.isEmpty() || content.isEmpty()) {
                    if (actionLogsPanel != null) {
                        actionLogsPanel.logError("SEND", "Recipient, subject, and content are required", null);
                    }
                    return;
                }
                
                if (actionLogsPanel != null) {
                    actionLogsPanel.logSendMessage(
                        selectorPanel.getSelectedTestCase() != null ? selectorPanel.getSelectedTestCase().getId() : "N/A",
                        selectorPanel.getSelectedSubcase() != null ? selectorPanel.getSelectedSubcase().getId() : "N/A",
                        recipient, subject, content, priority.toString(), true, null, null
                    );
                }
                
                String msgId = messageService.sendMessage(recipient, subject, content, priority);
                if (actionLogsPanel != null) {
                    actionLogsPanel.logAction("Message sent successfully. Message ID: " + msgId);
                }
            } catch (Throwable t) {
                if (actionLogsPanel != null) {
                    java.io.StringWriter sw = new java.io.StringWriter();
                    t.printStackTrace(new java.io.PrintWriter(sw));
                    actionLogsPanel.logError("SEND", t.getMessage(), sw.toString());
                }
            }
        }).start();
    }
    
    /**
     * Handle loading default configuration for selected subcase
     */
    private void handleLoadDefaultsForSubcase() {
        TestSubcase selectedSubcase = selectorPanel.getSelectedSubcase();
        if (selectedSubcase != null && !selectedSubcase.getAmhsDefaults().isEmpty()) {
            Map<String, String> defaults = selectedSubcase.getAmhsDefaults();
            
            // Load recipient
            if (defaults.containsKey("recipient")) {
                txtRecipient.setText(defaults.get("recipient"));
            }
            
            // Load subject
            if (defaults.containsKey("subject")) {
                txtSubject.setText(defaults.get("subject"));
            }
            
            // Load priority
            if (defaults.containsKey("priority")) {
                String priorityStr = defaults.get("priority");
                if ("LOW".equalsIgnoreCase(priorityStr)) {
                    comboPriority.setSelectedItem(X400_Priority.LOW_PRIORITY);
                } else if ("HIGH".equalsIgnoreCase(priorityStr)) {
                    comboPriority.setSelectedItem(X400_Priority.HIGH_PRIORITY);
                } else if ("URGENT".equalsIgnoreCase(priorityStr)) {
                    comboPriority.setSelectedItem(X400_Priority.HIGH_PRIORITY);
                } else {
                    comboPriority.setSelectedItem(X400_Priority.NORMAL_PRIORITY);
                }
            }
            
            // Load content
            if (defaults.containsKey("content")) {
                txtContent.setText(defaults.get("content"));
            }
            
            if (actionLogsPanel != null) {
                actionLogsPanel.logAction("Loaded default configuration for subcase: " + selectedSubcase.getId());
            }
        } else {
            if (actionLogsPanel != null) {
                actionLogsPanel.logAction("No default configuration available for selected subcase");
            }
        }
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
