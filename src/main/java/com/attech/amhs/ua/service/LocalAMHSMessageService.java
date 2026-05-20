package com.attech.amhs.ua.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * In-memory, pure-Java AMHS service replacement.
 * Does not require native Isode libraries.
 * Designed for testing and scenario-driven simulations.
 */
public class LocalAMHSMessageService {
    private boolean isConnected;
    private String presentationAddress;
    private String userOrAddress;
    private String password;
    private boolean useP3;

    // Simple in-memory mailboxes keyed by O/R address string
    private static final Map<String, List<MessageSummary>> MAILBOXES = Collections.synchronizedMap(new HashMap<>());

    public LocalAMHSMessageService() {
        this.isConnected = false;
        this.useP3 = false;
    }

    public void configureP7(String presentationAddress, String userOrAddress, String password) {
        this.presentationAddress = presentationAddress;
        this.userOrAddress = userOrAddress;
        this.password = password;
        this.useP3 = false;
    }

    public void configureP3(String presentationAddress, String userOrAddress, String password) {
        this.presentationAddress = presentationAddress;
        this.userOrAddress = userOrAddress;
        this.password = password;
        this.useP3 = true;
    }

    public boolean connect() throws X400APIException {
        if (isConnected) return true;
        // Basic validation
        if (presentationAddress == null || userOrAddress == null) {
            throw new X400APIException("Missing connection configuration");
        }
        // Simulate connection success
        isConnected = true;
        // Ensure mailbox exists for this user
        MAILBOXES.computeIfAbsent(userOrAddress, k -> Collections.synchronizedList(new ArrayList<>()));
        return true;
    }

    public void disconnect() {
        isConnected = false;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public String sendMessage(String recipient, String subject, String content, MessagePriority priority) throws X400APIException {
        if (!isConnected) throw new X400APIException("Not connected");
        // Create message summary and append to recipient mailbox
        MessageSummary msg = new MessageSummary();
        msg.setSender(this.userOrAddress == null ? "unknown" : this.userOrAddress);
        msg.setSubject(subject);
        msg.setContent(content);
        msg.setSubmissionTime(Instant.now().toString());
        msg.setMessageId(UUID.randomUUID().toString());
        msg.setContentLength(content == null ? 0 : content.length());
        // Add to mailbox
        MAILBOXES.computeIfAbsent(recipient, k -> Collections.synchronizedList(new ArrayList<>())).add(msg);
        return msg.getMessageId();
    }

    public String sendMessage(String recipient, String subject, String content) throws X400APIException {
        return sendMessage(recipient, subject, content, MessagePriority.NORMAL_PRIORITY);
    }

    public List<MessageSummary> receiveMessages(int maxMessages) throws X400APIException {
        if (!isConnected) throw new X400APIException("Not connected");
        List<MessageSummary> result = new ArrayList<>();
        List<MessageSummary> mailbox = MAILBOXES.getOrDefault(userOrAddress, Collections.emptyList());
        synchronized (mailbox) {
            int toTake = Math.min(maxMessages, mailbox.size());
            for (int i = 0; i < toTake; i++) {
                result.add(mailbox.remove(0));
            }
        }
        return result;
    }

    public boolean waitForNewMessage(int timeoutSeconds) throws X400APIException {
        if (!isConnected) throw new X400APIException("Not connected");
        List<MessageSummary> mailbox = MAILBOXES.getOrDefault(userOrAddress, Collections.emptyList());
        int waited = 0;
        while (waited < timeoutSeconds) {
            if (!mailbox.isEmpty()) return true;
            try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); break; }
            waited++;
        }
        return false;
    }

    public List<MessageSummary> getMailboxSummary() throws X400APIException {
        if (!isConnected) throw new X400APIException("Not connected");
        List<MessageSummary> mailbox = MAILBOXES.getOrDefault(userOrAddress, Collections.emptyList());
        return new ArrayList<>(mailbox); // return copy
    }

    /* Small helper types used by the UI */
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

    public enum MessagePriority {
        NORMAL_PRIORITY, LOW_PRIORITY, HIGH_PRIORITY
    }

    public static class X400APIException extends Exception {
        private int nativeErrorCode = 0;
        public X400APIException(String msg) { super(msg); }
        public X400APIException(String msg, Throwable t) { super(msg, t); }
        public int getNativeErrorCode() { return nativeErrorCode; }
    }
}
