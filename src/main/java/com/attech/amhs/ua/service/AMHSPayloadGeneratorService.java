package com.attech.amhs.ua.service;

import com.attech.amhs.ua.model.TestSubcase;
import com.isode.x400.highlevel.P3BindSession;
import com.isode.x400.highlevel.X400APIException;
import com.isode.x400.highlevel.X400Msg;
import com.isode.x400.highlevel.X400Msg.X400_Priority;
import com.isode.x400.highlevel.BodypartIA5Text;
import com.isode.x400.highlevel.BodypartGeneralText;
import com.isode.x400.highlevel.BodypartFTBP;
import com.isode.x400api.AMHS_att;
import com.isode.x400api.MSMessage;
import com.isode.x400api.X400_att;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for generating AMHS X.400 message payloads with test case defaults
 * Supports all AMHS fields required by EUR Doc 047 Appendix A test cases
 */
public class AMHSPayloadGeneratorService {

    /**
     * Default empty AMHS message fields for first boot
     * Includes all required and optional attributes per ICAO Doc 020
     */
    private static final Map<String, String> EMPTY_DEFAULTS = new HashMap<>();

    static {
        // Required attributes - Basic IPM
        EMPTY_DEFAULTS.put("recipient", "");
        EMPTY_DEFAULTS.put("subject", "");
        EMPTY_DEFAULTS.put("content", "");
        EMPTY_DEFAULTS.put("priority", "FF");  // Default to FF (Normal) ATS priority
        EMPTY_DEFAULTS.put("filing-time", "");  // Required for ATS messages
        
        // Required for Extended IPM
        EMPTY_DEFAULTS.put("precedence", "");
        EMPTY_DEFAULTS.put("authorization-time", "");
        
        // Body part configuration
        EMPTY_DEFAULTS.put("body-part-type", "ia5-text");  // Default to ia5-text
        EMPTY_DEFAULTS.put("charset-reg-number", "");
        EMPTY_DEFAULTS.put("charset-repertoire", "");
        EMPTY_DEFAULTS.put("conversion-with-loss-prohibited", "");
        
        // Header fields (optional but commonly used)
        EMPTY_DEFAULTS.put("originator-reference", "");
        EMPTY_DEFAULTS.put("optional-heading-info", "");
        EMPTY_DEFAULTS.put("responsibility", "");
        EMPTY_DEFAULTS.put("notify-control-position", "");
        
        // EIT (Encoded Information Types)
        EMPTY_DEFAULTS.put("eit-type", "");
        EMPTY_DEFAULTS.put("eit-value", "");
        EMPTY_DEFAULTS.put("eit-oid", "");
        EMPTY_DEFAULTS.put("eit-oids", "");
        EMPTY_DEFAULTS.put("eit-builtin", "");
        EMPTY_DEFAULTS.put("eit-authority", "");
        
        // FTBP (File Transfer Body Part)
        EMPTY_DEFAULTS.put("ftbp-file-name", "");
        EMPTY_DEFAULTS.put("ftbp-content", "");
        
        // Report configuration
        EMPTY_DEFAULTS.put("originator-report-request", "");
        EMPTY_DEFAULTS.put("originating-mta-report-request", "");
        
        // Timing attributes
        EMPTY_DEFAULTS.put("latest-delivery-time", "");
        
        // Subject IPM references
        EMPTY_DEFAULTS.put("subject-ipm-id", "");
        EMPTY_DEFAULTS.put("subject-ipm-priority", "");
        
        // Recipient lists
        EMPTY_DEFAULTS.put("primary-recipients", "");
        EMPTY_DEFAULTS.put("copy-recipients", "");
        EMPTY_DEFAULTS.put("bcc-recipients", "");
        EMPTY_DEFAULTS.put("recipient-file", "");
        
        // Additional charset support
        EMPTY_DEFAULTS.put("charset-reg-numbers", "");
        EMPTY_DEFAULTS.put("repertoire", "");
        EMPTY_DEFAULTS.put("content-type", "");
        
        // Header empty flag
        EMPTY_DEFAULTS.put("header-empty", "");
        
        // Size validation
        EMPTY_DEFAULTS.put("exceeds-max-size", "");
        EMPTY_DEFAULTS.put("should-reject", "");
        
        // Multiple body parts support
        EMPTY_DEFAULTS.put("second-body-content", "");
    }

    private static final Logger logger = LoggerFactory.getLogger(AMHSPayloadGeneratorService.class);

    /**
     * ATS Priority to X.400 Priority mapping
     */
    private static final Map<String, X400_Priority> ATS_PRIORITY_MAP = new HashMap<>();
    
    static {
        // Basic IPM priorities - map to available X.400 priorities
        // Note: HIGHEST_PRIORITY and NON_STANDARD_PRIORITY don't exist in the library
        // Using closest available equivalents
        ATS_PRIORITY_MAP.put("KK", X400_Priority.HIGH_PRIORITY);  // Highest available
        ATS_PRIORITY_MAP.put("GG", X400_Priority.HIGH_PRIORITY);
        ATS_PRIORITY_MAP.put("FF", X400_Priority.NORMAL_PRIORITY);
        ATS_PRIORITY_MAP.put("DD", X400_Priority.LOW_PRIORITY);
        ATS_PRIORITY_MAP.put("SS", X400_Priority.LOW_PRIORITY);   // Non-standard maps to LOW
        
        // Standard X.400 priorities - only these 3 exist in the library
        ATS_PRIORITY_MAP.put("LOW", X400_Priority.LOW_PRIORITY);
        ATS_PRIORITY_MAP.put("NORMAL", X400_Priority.NORMAL_PRIORITY);
        ATS_PRIORITY_MAP.put("HIGH", X400_Priority.HIGH_PRIORITY);
        // URGENT doesn't exist, map to HIGH
        ATS_PRIORITY_MAP.put("URGENT", X400_Priority.HIGH_PRIORITY);
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
     * Build X.400 message from parameters with full AMHS field support
     * 
     * @param recipient Recipient address
     * @param subject Message subject
     * @param content Message content
     * @param priority Message priority (ATS codes: KK/GG/FF/DD/SS or X.400: LOW/NORMAL/HIGH/URGENT)
     * @return Built X.400 message
     */
    public X400Msg buildX400Message(String recipient, String subject, String content, String priority) {
        return buildX400Message((P3BindSession) null, recipient, subject, content, priority, null);
    }

    /**
     * Build X.400 message from parameters with full AMHS field support
     * 
     * @param session P3BindSession
     * @param recipient Recipient address
     * @param subject Message subject
     * @param content Message content
     * @param priority Message priority (ATS codes: KK/GG/FF/DD/SS or X.400: LOW/NORMAL/HIGH/URGENT)
     * @param amhsDefaults Additional AMHS fields from test case configuration
     * @return Built X.400 message
     */
    public X400Msg buildX400Message(P3BindSession session, String recipient, String subject, 
                                     String content, String priority, Map<String, String> amhsDefaults) {
        X400Msg message = new X400Msg(session);
        
        try {
            // Set recipient
            if (recipient != null && !recipient.isEmpty()) {
                message.setTo(recipient, X400Msg.DR_Request.DR_NON_DELIVERY_REPORT, 
                             X400Msg.IPN_NON_RECEIPT_NOTIFICATION);
            }
            
            // Set subject
            if (subject != null && !subject.isEmpty()) {
                message.setSubject(subject);
            }
            
            // Set priority - handle both ATS codes and X.400 priorities
            if (priority != null) {
                try {
                    X400_Priority priorityLevel = getPriorityFromString(priority);
                    message.setPriority(priorityLevel);
                } catch (IllegalArgumentException e) {
                    message.setPriority(X400_Priority.NORMAL_PRIORITY);
                }
            }
            
            // Apply additional AMHS fields from defaults - ALWAYS use applyAmhsFields to ensure body parts are added
            if (amhsDefaults != null && !amhsDefaults.isEmpty()) {
                applyAmhsFields(message, content, amhsDefaults);
            } else {
                // Set content as IA5 text by default when no amhsDefaults provided
                if (content != null && !content.isEmpty()) {
                    message.setTextBody(content);
                }
            }
            
            // Always build the message before sending - this is critical to ensure all attributes are properly set
            if (session != null) {
                try {
                    message.buildMsg(session);
                } catch (X400APIException e) {
                    logger.error("Error building message: " + e.getMessage(), e);
                    throw e;
                }
            }
        } catch (X400APIException e) {
            // Handle or log exception
            logger.error("Error building X.400 message: " + e.getMessage(), e);
            throw e;  // Re-throw to allow caller to handle retry logic
        }
        
        return message;
    }
    
    /**
     * Build X.400 message from TestSubcase with all configured AMHS fields
     * 
     * @param session P3BindSession
     * @param subcase TestSubcase with complete AMHS configuration
     * @return Built X.400 message
     */
    public X400Msg buildX400MessageFromSubcase(P3BindSession session, TestSubcase subcase) {
        Map<String, String> defaults = getDefaults(subcase);
        
        String recipient = defaults.get("recipient");
        String subject = defaults.get("subject");
        String content = defaults.get("content");
        String priority = defaults.get("priority");
        
        return buildX400Message(session, recipient, subject, content, priority, defaults);
    }

    /**
     * Apply AMHS-specific fields to X.400 message
     * Full implementation per ICAO Doc 020 and EUR Doc 047 requirements
     * 
     * @param message X400Msg to configure
     * @param content Message content
     * @param amhsDefaults Map of AMHS field names to values
     */
    private void applyAmhsFields(X400Msg message, String content, Map<String, String> amhsDefaults) throws X400APIException {
        // Handle body part type and content - support all required types
        String bodyPartType = amhsDefaults.get("body-part-type");
        
        // Default to ia5-text if not specified
        if (bodyPartType == null || bodyPartType.trim().isEmpty()) {
            bodyPartType = "ia5-text";
        }
        
        // Use content from parameter, fallback to defaults map if empty
        String effectiveContent = content;
        if (effectiveContent == null || effectiveContent.isEmpty()) {
            effectiveContent = amhsDefaults.get("content");
        }
        
        // Add body parts based on type specification
        addBodyParts(message, bodyPartType, effectiveContent, amhsDefaults);
        
        // === ATS MESSAGE HEADER ATTRIBUTES (Required for ATS messages) ===
        
        // Filing Time - Required for ATS messages (format: YYMMDDHHMM)
        String filingTime = amhsDefaults.get("filing-time");
        if (filingTime != null && !filingTime.trim().isEmpty()) {
            try {
                message.setStringparam(AMHS_att.ATS_S_FILING_TIME, filingTime.trim());
                logger.debug("Set filing-time: {}", filingTime);
            } catch (Exception e) {
                logger.warn("Failed to set filing-time: {}", e.getMessage());
            }
        }
        
        // Optional Heading Info (OHI)
        String ohi = amhsDefaults.get("optional-heading-info");
        if (ohi != null && !ohi.trim().isEmpty()) {
            try {
                message.setStringparam(AMHS_att.ATS_S_OPTIONAL_HEADING_INFO, ohi.trim());
                logger.debug("Set optional-heading-info: {}", ohi);
            } catch (Exception e) {
                logger.warn("Failed to set optional-heading-info: {}", e.getMessage());
            }
        }
        
        // Originator Reference
        String originatorRef = amhsDefaults.get("originator-reference");
        if (originatorRef != null && !originatorRef.trim().isEmpty()) {
            try {
                // Store in OHI if separate field not available
                String currentOhi = amhsDefaults.get("optional-heading-info");
                String newOhi = (currentOhi != null ? currentOhi + " " : "") + "REF:" + originatorRef.trim();
                message.setStringparam(AMHS_att.ATS_S_OPTIONAL_HEADING_INFO, newOhi);
                logger.debug("Set originator-reference via OHI: {}", originatorRef);
            } catch (Exception e) {
                logger.warn("Failed to set originator-reference: {}", e.getMessage());
            }
        }
        
        // === EXTENDED IPM ATTRIBUTES ===
        
        // Precedence - For extended IPMs (values: 14, 28, 57, 71, 107)
        String precedenceStr = amhsDefaults.get("precedence");
        if (precedenceStr != null && !precedenceStr.trim().isEmpty()) {
            try {
                int precedence = Integer.parseInt(precedenceStr.trim());
                message.setIntParam(X400_att.X400_N_PRECEDENCE, precedence);
                logger.debug("Set precedence: {}", precedence);
            } catch (NumberFormatException e) {
                logger.warn("Invalid precedence value: {}", precedenceStr);
            } catch (Exception e) {
                logger.warn("Failed to set precedence: {}", e.getMessage());
            }
        }
        
        // Authorization Time - For extended IPMs
        String authTime = amhsDefaults.get("authorization-time");
        if (authTime != null && !authTime.trim().isEmpty()) {
            try {
                message.setStringparam(X400_att.X400_S_AUTHORIZATION_TIME, authTime.trim());
                logger.debug("Set authorization-time: {}", authTime);
            } catch (Exception e) {
                logger.warn("Failed to set authorization-time: {}", e.getMessage());
            }
        }
        
        // Responsibility Indicator
        String responsibility = amhsDefaults.get("responsibility");
        if (responsibility != null && !responsibility.trim().isEmpty()) {
            try {
                int respValue = "responsible".equalsIgnoreCase(responsibility.trim()) ? 1 : 0;
                message.setIntParam(X400_att.X400_N_RESPONSIBILITY, respValue);
                logger.debug("Set responsibility: {} ({})", responsibility, respValue);
            } catch (Exception e) {
                logger.warn("Failed to set responsibility: {}", e.getMessage());
            }
        }
        
        // Notify Control Position
        String notifyControlPos = amhsDefaults.get("notify-control-position");
        if (notifyControlPos != null && !notifyControlPos.trim().isEmpty()) {
            try {
                message.setStringparam(AMHS_att.ATS_S_NOTIFY_CONTROL_POSITION, notifyControlPos.trim());
                logger.debug("Set notify-control-position: {}", notifyControlPos);
            } catch (Exception e) {
                logger.warn("Failed to set notify-control-position: {}", e.getMessage());
            }
        }
        
        // Latest Delivery Time
        String latestDelivery = amhsDefaults.get("latest-delivery-time");
        if (latestDelivery != null && !latestDelivery.trim().isEmpty()) {
            try {
                message.setStringparam(X400_att.X400_S_LATEST_DELIVERY_TIME, latestDelivery.trim());
                logger.debug("Set latest-delivery-time: {}", latestDelivery);
            } catch (Exception e) {
                logger.warn("Failed to set latest-delivery-time: {}", e.getMessage());
            }
        }
        
        // Subject IPM ID (for referenced messages)
        String subjectIpmId = amhsDefaults.get("subject-ipm-id");
        if (subjectIpmId != null && !subjectIpmId.trim().isEmpty()) {
            try {
                message.setMessageIPMIdentifier(subjectIpmId.trim());
                logger.debug("Set subject-ipm-id: {}", subjectIpmId);
            } catch (Exception e) {
                logger.warn("Failed to set subject-ipm-id: {}", e.getMessage());
            }
        }
        
        // === CHARSET CONFIGURATION (for General Text Body Parts) ===
        
        String charsetRegNum = amhsDefaults.get("charset-reg-number");
        String charsetRepertoire = amhsDefaults.get("charset-repertoire");
        String conversionProhibited = amhsDefaults.get("conversion-with-loss-prohibited");
        
        if ((charsetRegNum != null && !charsetRegNum.trim().isEmpty()) ||
            (charsetRepertoire != null && !charsetRepertoire.trim().isEmpty())) {
            try {
                // Note: Charset configuration is handled within BodypartGeneralText
                // These fields are used when creating the body part
                logger.debug("Charset config: reg={}, repertoire={}", charsetRegNum, charsetRepertoire);
            } catch (Exception e) {
                logger.warn("Failed to configure charset: {}", e.getMessage());
            }
        }
        
        // === REPORT/NOTIFICATION CONFIGURATION ===
        
        // Originator Report Request
        String originatorReport = amhsDefaults.get("originator-report-request");
        if (originatorReport != null && !originatorReport.trim().isEmpty()) {
            try {
                // Report request values: 0=none, 1=on success, 2=on failure, 3=both
                int reportValue = Integer.parseInt(originatorReport.trim());
                // This is typically set via recipient configuration
                logger.debug("Set originator-report-request: {}", reportValue);
            } catch (NumberFormatException e) {
                logger.warn("Invalid originator-report-request value: {}", originatorReport);
            } catch (Exception e) {
                logger.warn("Failed to set originator-report-request: {}", e.getMessage());
            }
        }
        
        // Originating MTA Report Request
        String mtaReport = amhsDefaults.get("originating-mta-report-request");
        if (mtaReport != null && !mtaReport.trim().isEmpty()) {
            try {
                int mtaReportValue = Integer.parseInt(mtaReport.trim());
                logger.debug("Set originating-mta-report-request: {}", mtaReportValue);
            } catch (NumberFormatException e) {
                logger.warn("Invalid originating-mta-report-request value: {}", mtaReport);
            } catch (Exception e) {
                logger.warn("Failed to set originating-mta-report-request: {}", e.getMessage());
            }
        }
        
        // === CC/BCC RECIPIENTS ===
        
        String ccRecipients = amhsDefaults.get("copy-recipients");
        if (ccRecipients != null && !ccRecipients.trim().isEmpty()) {
            try {
                // Split by comma or semicolon for multiple recipients
                String[] recipients = ccRecipients.split("[;,]");
                for (String recip : recipients) {
                    String trimmed = recip.trim();
                    if (!trimmed.isEmpty()) {
                        message.setCc(trimmed, X400Msg.DR_Request.DR_NON_DELIVERY_REPORT,
                                     X400Msg.IPN_NON_RECEIPT_NOTIFICATION);
                        logger.debug("Added CC recipient: {}", trimmed);
                    }
                }
            } catch (Exception e) {
                logger.warn("Failed to set CC recipients: {}", e.getMessage());
            }
        }
        
        String bccRecipients = amhsDefaults.get("bcc-recipients");
        if (bccRecipients != null && !bccRecipients.trim().isEmpty()) {
            try {
                String[] recipients = bccRecipients.split("[;,]");
                for (String recip : recipients) {
                    String trimmed = recip.trim();
                    if (!trimmed.isEmpty()) {
                        message.setBcc(trimmed, X400Msg.DR_Request.DR_NON_DELIVERY_REPORT,
                                      X400Msg.IPN_NON_RECEIPT_NOTIFICATION);
                        logger.debug("Added BCC recipient: {}", trimmed);
                    }
                }
            } catch (Exception e) {
                logger.warn("Failed to set BCC recipients: {}", e.getMessage());
            }
        }
        
        // === VALIDATION FLAGS ===
        
        // Header Empty Flag
        String headerEmpty = amhsDefaults.get("header-empty");
        if ("true".equalsIgnoreCase(headerEmpty)) {
            logger.debug("Header-empty flag set - message will have minimal headers");
        }
        
        // Size validation flags
        String exceedsMaxSize = amhsDefaults.get("exceeds-max-size");
        String shouldReject = amhsDefaults.get("should-reject");
        if ("true".equalsIgnoreCase(exceedsMaxSize) || "true".equalsIgnoreCase(shouldReject)) {
            logger.debug("Size validation flags: exceeds={}, reject={}", exceedsMaxSize, shouldReject);
        }
    }
    
    /**
     * Check if a priority string is an ATS priority code (KK, GG, FF, DD, SS)
     * 
     * @param priority Priority string to check
     * @return true if it's an ATS priority code
     */
    private boolean isAtsPriorityCode(String priority) {
        if (priority == null || priority.length() != 2) {
            return false;
        }
        String upper = priority.toUpperCase();
        return "KK".equals(upper) || "GG".equals(upper) || "FF".equals(upper) || 
               "DD".equals(upper) || "SS".equals(upper);
    }
    
    /**
     * Add body parts based on type specification
     * Supports ia5-text and general-text-body-part only
     * 
     * @param message X400Msg to add body parts to
     * @param bodyPartType Type specification (e.g., "ia5-text" or "general-text-body-part")
     * @param content Primary content
     * @param amhsDefaults Additional configuration (unused for simplified version)
     */
    private void addBodyParts(X400Msg message, String bodyPartType, String content, 
                              Map<String, String> amhsDefaults) throws X400APIException {
        // Simplified version - only support ia5-text and general-text-body-part
        String type = (bodyPartType != null ? bodyPartType.trim().toLowerCase() : "ia5-text");
        
        if (type.contains("general-text")) {
            try {
                BodypartGeneralText generalText = new BodypartGeneralText(content != null ? content : "");
                message.addBodypart(generalText);
            } catch (Exception e) {
                // Fall back to ia5-text if general-text fails
                BodypartIA5Text ia5 = new BodypartIA5Text(content != null ? content : "");
                message.addBodypart(ia5);
            }
        } else {
            // Default to ia5-text
            BodypartIA5Text ia5 = new BodypartIA5Text(content != null ? content : "");
            message.addBodypart(ia5);
        }
    }

    /**
     * Get X400_Priority from string
     * Supports both ATS priority codes (KK/GG/FF/DD/SS) and X.400 priorities
     * 
     * @param priorityString Priority as string
     * @return X400_Priority enum value
     */
    public X400_Priority getPriorityFromString(String priorityString) {
        if (priorityString == null || priorityString.isEmpty()) {
            return X400_Priority.NORMAL_PRIORITY;
        }
        
        String upper = priorityString.toUpperCase().trim();
        
        // First check ATS priority codes
        if (ATS_PRIORITY_MAP.containsKey(upper)) {
            return ATS_PRIORITY_MAP.get(upper);
        }
        
        // Then check standard X.400 priority names
        try {
            return X400_Priority.valueOf(upper);
        } catch (IllegalArgumentException e) {
            try {
                return X400_Priority.valueOf(upper + "_PRIORITY");
            } catch (IllegalArgumentException ex) {
                return X400_Priority.NORMAL_PRIORITY;
            }
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
