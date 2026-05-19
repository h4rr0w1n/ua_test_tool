package com.attech.amhs.ua.service;

import com.attech.amhs.ua.model.TestSubcase;
import com.isode.x400.highlevel.X400Msg;
import com.isode.x400.highlevel.X400Msg.X400_Priority;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for generating AMHS X.400 message payloads with test case defaults
 */
public class AMHSPayloadGeneratorService {

    /**
     * Default empty AMHS message fields for first boot
     */
    private static final Map<String, String> EMPTY_DEFAULTS = new HashMap<>();

    static {
        EMPTY_DEFAULTS.put("recipient", "");
        EMPTY_DEFAULTS.put("subject", "");
        EMPTY_DEFAULTS.put("content", "");
        EMPTY_DEFAULTS.put("priority", "NORMAL");
    }

    /**
     * Get default AMHS fields for a test subcase
     * If subcase has defaults, use them; otherwise use empty defaults
     * 
     * @param subcase TestSubcase with potential default values
     * @return Map of field names to values
     */
    public Map<String, String> getDefaults(TestSubcase subcase) {
        if (subcase == null || subcase.getAmhsDefaults().isEmpty()) {
            return new HashMap<>(EMPTY_DEFAULTS);
        }
        
        Map<String, String> defaults = new HashMap<>(EMPTY_DEFAULTS);
        defaults.putAll(subcase.getAmhsDefaults());
        return defaults;
    }

    /**
     * Get empty defaults for first boot or new message
     * 
     * @return Map of empty default fields
     */
    public Map<String, String> getEmptyDefaults() {
        return new HashMap<>(EMPTY_DEFAULTS);
    }

    /**
     * Build X.400 message from parameters
     * 
     * @param recipient Recipient address
     * @param subject Message subject
     * @param content Message content
     * @param priority Message priority (LOW, NORMAL, HIGH, URGENT)
     * @return Built X.400 message
     */
    public X400Msg buildX400Message(String recipient, String subject, String content, String priority) {
        X400Msg message = new X400Msg();
        
        // Set recipient
        if (recipient != null && !recipient.isEmpty()) {
            message.setRecipient(recipient);
        }
        
        // Set subject
        if (subject != null && !subject.isEmpty()) {
            message.setSubject(subject);
        }
        
        // Set content
        if (content != null && !content.isEmpty()) {
            message.setContent(content);
        }
        
        // Set priority
        if (priority != null) {
            try {
                X400_Priority priorityLevel = X400_Priority.valueOf(priority.toUpperCase());
                message.setPriority(priorityLevel);
            } catch (IllegalArgumentException e) {
                message.setPriority(X400_Priority.NORMAL);
            }
        }
        
        return message;
    }

    /**
     * Get X400_Priority from string
     * 
     * @param priorityString Priority as string
     * @return X400_Priority enum value
     */
    public X400_Priority getPriorityFromString(String priorityString) {
        if (priorityString == null || priorityString.isEmpty()) {
            return X400_Priority.NORMAL;
        }
        
        try {
            return X400_Priority.valueOf(priorityString.toUpperCase());
        } catch (IllegalArgumentException e) {
            return X400_Priority.NORMAL;
        }
    }

    /**
     * Validate AMHS message fields
     * 
     * @param recipient Recipient address
     * @param subject Message subject
     * @return true if fields are valid, false otherwise
     */
    public boolean validateMessage(String recipient, String subject) {
        return recipient != null && !recipient.trim().isEmpty() &&
               subject != null && !subject.trim().isEmpty();
    }

    /**
     * Generate payload string for logging/display
     * 
     * @param recipient Recipient
     * @param subject Subject
     * @param content Content
     * @param priority Priority
     * @return String representation of X.400 message
     */
    public String generatePayloadString(String recipient, String subject, String content, String priority) {
        StringBuilder payload = new StringBuilder();
        payload.append("X.400 Message Payload:\n");
        payload.append("Recipient: ").append(recipient != null ? recipient : "").append("\n");
        payload.append("Subject: ").append(subject != null ? subject : "").append("\n");
        payload.append("Priority: ").append(priority != null ? priority : "NORMAL").append("\n");
        payload.append("Content:\n").append(content != null ? content : "").append("\n");
        return payload.toString();
    }
}
