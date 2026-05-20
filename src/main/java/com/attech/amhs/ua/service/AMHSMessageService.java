/*
 * AMHS X.400 Message Service
 * Provides functionality for sending and receiving AMHS X.400 messages
 */
package com.attech.amhs.ua.service;

import com.isode.x400.highlevel.*;
import com.isode.x400api.X400_att;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for AMHS X.400 message operations
 * Provides methods for creating, sending, and receiving X.400 messages
 */
public class AMHSMessageService {
    
    private boolean isConnected;
    
    // Configuration
    private String presentationAddress;
    private String userOrAddress;
    private String password;
    private boolean useP3; // false = P7 Message Store, true = P3 Channel
    
    public AMHSMessageService() {
        this.isConnected = false;
        this.useP3 = false;
    }
    
    /**
     * Configure connection parameters for P7 Message Store
     */
    public void configureP7(String presentationAddress, String userOrAddress, String password) {
        this.presentationAddress = presentationAddress;
        this.userOrAddress = userOrAddress;
        this.password = password;
        this.useP3 = false;
    }
    
    /**
     * Configure connection parameters for P3 Channel
     */
    public void configureP3(String presentationAddress, String userOrAddress, String password) {
        this.presentationAddress = presentationAddress;
        this.userOrAddress = userOrAddress;
        this.password = password;
        this.useP3 = true;
    }
    
    /**
     * Connect to the X.400 Message Store or P3 Channel
     * @return true if connection successful
     */
    public boolean connect() throws X400APIException {
        if (isConnected) {
            return true;
        }
        
        try {
            System.out.println("DEBUG: Initializing X.400 system...");
            System.out.println("DEBUG: Library path: " + System.getProperty("java.library.path"));
            System.out.println("DEBUG: Working directory: " + System.getProperty("user.dir"));
            
            // Attempt connection with timeout
            final Exception[] bindException = {null};
            final boolean[] bindSuccess = {false};
            
            String addressToUse = presentationAddress;
            System.out.println("DEBUG: Original Presentation Address: " + addressToUse);
            
            Thread bindThread = new Thread(() -> {
                try {
                    if (useP3) {
                        System.out.println("Connecting to P3 Channel...");
                        System.out.println("DEBUG: Attempting P3 bind with address: " + addressToUse);
                        P3BindSession session = new P3BindSession(addressToUse, userOrAddress, password);
                        session.bind();
                    } else {
                        System.out.println("Connecting to P7 Message Store...");
                        System.out.println("DEBUG: Attempting P7 bind with address: " + addressToUse);
                        P7BindSession session = new P7BindSession(addressToUse, userOrAddress, password, false);
                        session.SetSummarizeOnBind(false);
                        System.out.println("DEBUG: P7BindSession created, calling bind()...");
                        session.bind();
                        System.out.println("DEBUG: bind() completed successfully");
                    }
                    bindSuccess[0] = true;
                } catch (Exception e) {
                    System.err.println("DEBUG: Bind thread exception: " + e.getClass().getName() + " - " + e.getMessage());
                    bindException[0] = e;
                }
            });
            
            bindThread.setName("AMHS-Bind-Thread");
            bindThread.setDaemon(false);
            bindThread.start();
            
            // Wait for bind with 60 second timeout (increased from 30)
            long startTime = System.currentTimeMillis();
            long timeout = 60000; // 60 seconds
            int dotCount = 0;
            
            while (bindThread.isAlive() && (System.currentTimeMillis() - startTime) < timeout) {
                Thread.sleep(500);
                System.out.print(".");
                dotCount++;
                if (dotCount % 60 == 0) {
                    System.out.println(" (" + (dotCount / 2) + "s)");
                }
            }
            System.out.println();
            
            if (bindThread.isAlive()) {
                System.err.println("ERROR: Connection timeout after 60 seconds");
                System.err.println("The ISODE X.400 bind() call is not responding.");
                System.err.println("\nPossible causes:");
                System.err.println("1. Server is not running or unreachable at: " + addressToUse);
                System.err.println("2. IP address/port is incorrect");
                System.err.println("3. Firewall blocking the connection");
                System.err.println("4. Server requires P3 instead of P7 (or vice versa)");
                System.err.println("5. Native library compatibility issue");
                System.err.println("\nSuggestions:");
                System.err.println("- Check if the server is running: ping " + extractIP(addressToUse));
                System.err.println("- Verify port 3001 is open: telnet " + extractIP(addressToUse) + " 3001");
                System.err.println("- Try toggling between P7 and P3 connection types in the UI");
                System.err.println("\nDebug: Address used: " + addressToUse);
                
                try {
                    bindThread.interrupt();
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    // Ignore
                }
                
                throw new X400APIException("Connection timeout after 60 seconds - server not responding");
            }
            
            if (bindException[0] != null) {
                throw bindException[0];
            }
            
            if (!bindSuccess[0]) {
                throw new X400APIException("Bind operation failed - unknown error");
            }
            
            isConnected = true;
            System.out.println("Connected successfully");
            return true;
        } catch (UnsatisfiedLinkError e) {
            String errorMsg = "Native library loading error: " + e.getMessage();
            System.err.println("ERROR: " + errorMsg);
            System.err.println("The Isode X.400 native libraries (DLLs) are not properly installed.");
            System.err.println("Required files: pthreadvc2.dll, CJavaInterface.dll");
            System.err.println("These files should be in: lib/amd64/ or lib/ directory");
            System.err.println("Please ensure you have installed the Isode X.400 libraries correctly.");
            throw new X400APIException(errorMsg);
        } catch (X400APIException e) {
            System.err.println("Connection failed: " + e.getMessage());
            e.printStackTrace();
            throw e;
        } catch (InterruptedException e) {
            String errorMsg = "Connection interrupted: " + e.getMessage();
            System.err.println("ERROR: " + errorMsg);
            Thread.currentThread().interrupt();
            throw new X400APIException(errorMsg);
        } catch (Throwable e) {
            String errorMsg = "Unexpected error during connection: " + e.getClass().getName() + " - " + e.getMessage();
            System.err.println("ERROR: " + errorMsg);
            e.printStackTrace();
            throw new X400APIException(errorMsg);
        }
    }
    
    /**
     * Extract IP address from presentation address string
     */
    private String extractIP(String address) {
        if (address == null) return "unknown";
        // Try to extract IP from various formats
        if (address.contains("://")) {
            String[] parts = address.split("://");
            if (parts.length > 1) {
                String host = parts[1].split("[:/+|]")[0];
                return host;
            }
        }
        if (address.contains("=")) {
            String[] parts = address.split("=");
            if (parts.length > 1) {
                String host = parts[1].split("[:/+|]")[0];
                return host;
            }
        }
        return address;
    }
    
    /**
     * Disconnect from the X.400 system
     */
    public void disconnect() {
        isConnected = false;
        System.out.println("Disconnected");
    }
    
    /**
     * Check if currently connected
     */
    public boolean isConnected() {
        return isConnected;
    }
    
    /**
     * Send an X.400 message
     * @param recipient O/R Address of the recipient
     * @param subject Message subject
     * @param content Message content (IA5 text)
     * @param priority Message priority (optional)
     * @return Message submission ID
     */
    public String sendMessage(String recipient, String subject, String content, 
                              X400Msg.X400_Priority priority) throws X400APIException {
        if (!isConnected) {
            throw new X400APIException("Not connected to X.400 system");
        }
        
        P7BindSession bindSession = null;
        try {
            // Create bind session
            bindSession = new P7BindSession(presentationAddress, userOrAddress, password, false);
            bindSession.bind();
            
            // Create message
            X400Msg x400msg = new X400Msg(bindSession);
            
            // Set recipient with delivery report request
            x400msg.setTo(recipient, X400Msg.DR_Request.DR_NON_DELIVERY_REPORT, 
                         X400Msg.IPN_NON_RECEIPT_NOTIFICATION);
            
            // Set subject
            x400msg.setSubject(subject);
            
            // Set priority
            if (priority != null) {
                x400msg.setPriority(priority);
            } else {
                x400msg.setPriority(X400Msg.X400_Priority.NORMAL_PRIORITY);
            }
            
            // Add IA5 text bodypart
            BodypartIA5Text ia5 = new BodypartIA5Text(content);
            x400msg.addBodypart(ia5);
            
            // Send the message
            x400msg.sendMsg(bindSession);
            
            // Get submission details
            String msgSubId = x400msg.getMessageIdentifier();
            String ipmId = x400msg.getMessageIPMIdentifier();
            String submissionTime = x400msg.getSubmissionTime();
            
            System.out.println("Message submitted.");
            System.out.println("Message Submission ID: " + msgSubId);
            System.out.println("IPM ID: " + ipmId);
            System.out.println("Submission time: " + submissionTime);
            
            // Unbind
            bindSession.unbind();
            
            return msgSubId;
            
        } catch (X400APIException e) {
            if (bindSession != null) {
                try {
                    bindSession.unbind();
                } catch (X400APIException ex) {
                    // Ignore unbind errors
                }
            }
            throw e;
        }
    }
    
    /**
     * Send a message with default normal priority
     */
    public String sendMessage(String recipient, String subject, String content) 
            throws X400APIException {
        return sendMessage(recipient, subject, content, X400Msg.X400_Priority.NORMAL_PRIORITY);
    }
    
    /**
     * Receive messages from the mailbox
     * @param maxMessages Maximum number of messages to receive
     * @return List of received messages
     */
    public List<MessageSummary> receiveMessages(int maxMessages) throws X400APIException {
        List<MessageSummary> messages = new ArrayList<>();
        
        try {
            P7BindSession session = new P7BindSession(presentationAddress, userOrAddress, password, false);
            session.bind();
            
            int numMsgs = session.getRefreshNumberOfMessages();
            if (numMsgs == 0) {
                System.out.println("No messages in mailbox");
                session.unbind();
                return messages;
            }
            
            int toReceive = Math.min(maxMessages, numMsgs);
            System.out.println("Found " + numMsgs + " messages, receiving " + toReceive);
            
            for (int i = 0; i < toReceive; i++) {
                ReceiveMsg rm = session.receiveNextAvailableMessage();
                
                MessageSummary summary = new MessageSummary();
                summary.setSubject(rm.getSubject());
                summary.setSender(rm.getFrom());
                summary.setContent(rm.getTextContent());
                
                messages.add(summary);
                
                rm.finishWithMessage(0, 0);
            }
            
            session.unbind();
            
        } catch (X400APIException e) {
            System.err.println("Error receiving messages: " + e.getMessage());
            throw e;
        }
        
        return messages;
    }
    
    /**
     * Wait for new messages with timeout
     * @param timeoutSeconds Timeout in seconds
     * @return true if new message arrived
     */
    public boolean waitForNewMessage(int timeoutSeconds) throws X400APIException {
        if (!isConnected) {
            throw new X400APIException("Not connected to X.400 system");
        }
        
        try {
            P7BindSession session = new P7BindSession(presentationAddress, userOrAddress, password, false);
            session.bind();
            
            System.out.println("Waiting for new message (" + timeoutSeconds + " seconds)...");
            int status = session.waitForNewMessages(timeoutSeconds);
            
            session.unbind();
            
            if (status == X400_att.X400_E_NOERROR) {
                System.out.println("New message received!");
                return true;
            } else if (status == X400_att.X400_E_TIMED_OUT) {
                System.out.println("Timeout - no new messages");
                return false;
            } else {
                System.out.println("Error waiting for messages: " + status);
                return false;
            }
            
        } catch (X400APIException e) {
            System.err.println("Error waiting for messages: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Get mailbox summary (list of messages without downloading)
     * @return List of message summaries
     */
    public List<MessageSummary> getMailboxSummary() throws X400APIException {
        List<MessageSummary> summaries = new ArrayList<>();
        
        try {
            P7BindSession session = new P7BindSession(presentationAddress, userOrAddress, password, false);
            session.bind();
            
            ArrayList<ListResult> listArray = session.listMailbox(null, 
                P7BindSession.Entry_Class.MS_ENTRY_CLASS_STORED_MESSAGES, false);
            
            for (int i = 1; i < listArray.size(); i++) {
                ListResult lr = listArray.get(i);
                MessageSummary summary = new MessageSummary();
                summary.setSubject(lr.getSubject());
                summary.setSender(lr.getSender());
                summary.setSubmissionTime(lr.getSubmissionTime());
                summary.setMessageId(lr.getMsgID());
                summary.setContentLength(lr.getContLength());
                summaries.add(summary);
            }
            
            session.unbind();
            
        } catch (X400APIException e) {
            System.err.println("Error getting mailbox summary: " + e.getMessage());
            throw e;
        }
        
        return summaries;
    }
    
    /**
     * Inner class for message summary
     */
    public static class MessageSummary {
        private String subject;
        private String sender;
        private String submissionTime;
        private String messageId;
        private int contentLength;
        private String content;
        
        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }
        
        public String getSender() { return sender; }
        public void setSender(String sender) { this.sender = sender; }
        
        public String getSubmissionTime() { return submissionTime; }
        public void setSubmissionTime(String submissionTime) { this.submissionTime = submissionTime; }
        
        public String getMessageId() { return messageId; }
        public void setMessageId(String messageId) { this.messageId = messageId; }
        
        public int getContentLength() { return contentLength; }
        public void setContentLength(int contentLength) { this.contentLength = contentLength; }
        
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        
        @Override
        public String toString() {
            return "From: " + sender + " | Subject: " + subject + " | Time: " + submissionTime;
        }
    }
}
