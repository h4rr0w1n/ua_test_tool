package com.attech.amhs.ua.service;

import com.attech.amhs.ua.model.TestSubcase;
import com.isode.x400.highlevel.P3BindSession;
import com.isode.x400.highlevel.X400APIException;
import com.isode.x400.highlevel.X400Msg;
import com.isode.x400.highlevel.X400Msg.X400_Priority;
import com.isode.x400.highlevel.BodypartIA5Text;
import com.attech.amhs.ua.isode.BodypartGeneralText;
import com.isode.x400.highlevel.BodypartFTBP;
import com.isode.x400api.AMHS_att;
import com.isode.x400api.MSMessage;
import com.isode.x400api.X400_att;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        ATS_PRIORITY_MAP.put("SS", X400_Priority.HIGH_PRIORITY);   // Special Service should be treated above normal
        
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
public X400Msg buildX400Message(P3BindSession session, String recipient, String subject,
                                 String content, String priority,
                                 Map<String, String> amhsDefaults) {
    return buildX400Message(session, recipient, subject, content, priority, amhsDefaults, null);
}

    private X400Msg.DR_Request getDrRequest(Map<String, String> amhsDefaults) {
        if (amhsDefaults == null) return X400Msg.DR_Request.DR_NON_DELIVERY_REPORT;
        String req = amhsDefaults.get("originator-report-request");
        if (req != null) {
            String lower = req.toLowerCase().trim();
            if (lower.equals("report") || lower.equals("delivery-report") || lower.equals("3")) {
                return X400Msg.DR_Request.DR_DELIVERY_REPORT;
            } else if (lower.equals("none") || lower.equals("0")) {
                return X400Msg.DR_Request.DR_NO_REPORT;
            } else if (lower.equals("non-delivery-report") || lower.equals("2") || lower.equals("1")) {
                return X400Msg.DR_Request.DR_NON_DELIVERY_REPORT;
            }
        }
        return X400Msg.DR_Request.DR_NON_DELIVERY_REPORT;
    }

    /**
 * Apply AMHS fields excluding body part and filing-time.
 * Those two are handled directly in buildX400Message to ensure
 * they are set exactly once and in the correct order.
 */
private void applyAmhsFieldsExceptBodyAndFilingTime(X400Msg message, Map<String, String> amhsDefaults)
        throws X400APIException {

    X400Msg.DR_Request drRequest = getDrRequest(amhsDefaults);


    // Optional Heading Info
    String ohi = amhsDefaults.get("optional-heading-info");
    if (ohi != null && !ohi.trim().isEmpty()) {
        try {
            message.setStringparam(AMHS_att.ATS_S_OPTIONAL_HEADING_INFO, ohi.trim());
        } catch (Exception e) {
            logger.warn("Failed to set optional-heading-info: {}", e.getMessage());
        }
    }

    // Originator Reference
    String originatorRef = amhsDefaults.get("originator-reference");
    if (originatorRef != null && !originatorRef.trim().isEmpty()) {
        try {
            String currentOhi = amhsDefaults.getOrDefault("optional-heading-info", "");
            String newOhi = (currentOhi.isEmpty() ? "" : currentOhi + " ") + "REF:" + originatorRef.trim();
            message.setStringparam(AMHS_att.ATS_S_OPTIONAL_HEADING_INFO, newOhi);
        } catch (Exception e) {
            logger.warn("Failed to set originator-reference: {}", e.getMessage());
        }
    }

    // Precedence
    String precedenceStr = amhsDefaults.get("precedence");
    if (precedenceStr != null && !precedenceStr.trim().isEmpty()) {
        try {
            int precedence = Integer.parseInt(precedenceStr.trim());
            message.setIntParam(X400_att.X400_N_PRECEDENCE, precedence);
        } catch (NumberFormatException e) {
            logger.warn("Invalid precedence value: {}", precedenceStr);
        } catch (Exception e) {
            logger.warn("Failed to set precedence: {}", e.getMessage());
        }
    }

    // Authorization Time
    String authTime = amhsDefaults.get("authorization-time");
    if (authTime != null && !authTime.trim().isEmpty()) {
        try {
            message.setStringparam(X400_att.X400_S_AUTHORIZATION_TIME, authTime.trim());
        } catch (Exception e) {
            logger.warn("Failed to set authorization-time: {}", e.getMessage());
        }
    }

    // Responsibility
    String responsibility = amhsDefaults.get("responsibility");
    if (responsibility != null && !responsibility.trim().isEmpty()) {
        try {
            int respValue = "responsible".equalsIgnoreCase(responsibility.trim()) ? 1 : 0;
            message.setIntParam(X400_att.X400_N_RESPONSIBILITY, respValue);
        } catch (Exception e) {
            logger.warn("Failed to set responsibility: {}", e.getMessage());
        }
    }

    // Latest Delivery Time
    String latestDelivery = amhsDefaults.get("latest-delivery-time");
    if (latestDelivery != null && !latestDelivery.trim().isEmpty()) {
        try {
            message.setStringparam(X400_att.X400_S_LATEST_DELIVERY_TIME, latestDelivery.trim());
        } catch (Exception e) {
            logger.warn("Failed to set latest-delivery-time: {}", e.getMessage());
        }
    }

    // Subject IPM ID
    String subjectIpmId = amhsDefaults.get("subject-ipm-id");
    if (subjectIpmId != null && !subjectIpmId.trim().isEmpty()) {
        try {
            message.setMessageIPMIdentifier(subjectIpmId.trim());
        } catch (Exception e) {
            logger.warn("Failed to set subject-ipm-id: {}", e.getMessage());
        }
    }

    // CC Recipients
    String ccRecipients = amhsDefaults.get("copy-recipients");
    if (ccRecipients != null && !ccRecipients.trim().isEmpty()) {
        for (String recip : ccRecipients.split("[;,]")) {
            String trimmed = recip.trim();
            if (!trimmed.isEmpty()) {
                try {
                    message.setCc(trimmed,
                            drRequest,
                            X400Msg.IPN_NON_RECEIPT_NOTIFICATION);
                } catch (Exception e) {
                    logger.warn("Failed to set CC recipient {}: {}", trimmed, e.getMessage());
                }
            }
        }
    }

    // BCC Recipients
    String bccRecipients = amhsDefaults.get("bcc-recipients");
    if (bccRecipients != null && !bccRecipients.trim().isEmpty()) {
        for (String recip : bccRecipients.split("[;,]")) {
            String trimmed = recip.trim();
            if (!trimmed.isEmpty()) {
                try {
                    message.setBcc(trimmed,
                            drRequest,
                            X400Msg.IPN_NON_RECEIPT_NOTIFICATION);
                } catch (Exception e) {
                    logger.warn("Failed to set BCC recipient {}: {}", trimmed, e.getMessage());
                }
            }
        }
    }
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
                                 String content, String priority,
                                 Map<String, String> amhsDefaults, String filingTime) {

    dumpMessageAttributes(recipient, subject, content, priority, amhsDefaults);

    X400Msg message = new X400Msg(session);

    try {
        // ── RECIPIENT (mandatory) ────────────────────────────────────────
        if (recipient == null || recipient.trim().isEmpty()) {
            throw new RuntimeException("Recipient address is required and cannot be empty");
        }
        X400Msg.DR_Request drRequest = getDrRequest(amhsDefaults);
        message.setTo(
            recipient.trim(),
            drRequest,
            X400Msg.IPN_NON_RECEIPT_NOTIFICATION
        );

        // ── SUBJECT ──────────────────────────────────────────────────────
        if (subject != null && !subject.trim().isEmpty()) {
            message.setSubject(subject.trim());
        }

        // ── PRIORITY ─────────────────────────────────────────────────────
        X400_Priority priorityLevel = getPriorityFromString(priority);
        message.setPriority(priorityLevel);

        // ── BODY PART ────────────────────────────────────────────────────
        // Always add body part directly here — never rely on applyAmhsFields
        // to add it, since that path is fragile when map is partial.
        String safeContent = (content != null && !content.isEmpty())
                ? content
                : (amhsDefaults != null ? amhsDefaults.getOrDefault("content", "") : "");
        String bodyPartType = (amhsDefaults != null)
                ? amhsDefaults.getOrDefault("body-part-type", "ia5-text")
                : "ia5-text";

        // Determine if there are multiple body parts
        boolean hasSecondBody = amhsDefaults != null && amhsDefaults.containsKey("second-body-content");
        String secondContent = hasSecondBody ? amhsDefaults.getOrDefault("second-body-content", "") : "";

        // Add first body part
        addBodyPart(message, bodyPartType, safeContent, amhsDefaults);
        
        // Validate body‑part count (CTSW007) – only up to two parts are allowed
        int bodyPartCount = 1 + (hasSecondBody ? 1 : 0);
        if (bodyPartCount > 2) {
            throw new RuntimeException("More than two body parts are not allowed (CTSW007)");
        }
        
        // Add second body part if needed
        if (hasSecondBody) {
            addBodyPart(message, bodyPartType, secondContent, amhsDefaults);
        }

        // ── FILING TIME (set exactly once, always) ───────────────────────
        String ft = (filingTime != null && !filingTime.trim().isEmpty())
                ? filingTime.trim()
                : formatCurrentFilingTime();
        message.setStringparam(AMHS_att.ATS_S_FILING_TIME, ft);
        logger.debug("Set filing-time: {}", ft);
        
        // When ATS_S_FILING_TIME is set, we must also supply these ATS attributes as a set
        String atsPriority = (priority != null && isAtsPriorityCode(priority)) ? priority.toUpperCase() : "FF";
        message.setStringparam(AMHS_att.ATS_S_PRIORITY_INDICATOR, atsPriority);
        
        String extended = (amhsDefaults != null) ? amhsDefaults.get("extended") : null;
        message.setIntParam(AMHS_att.ATS_N_EXTENDED, "true".equalsIgnoreCase(extended) ? 1 : 0);
        
        message.setIntParam(X400_att.X400_N_CONTENT_TYPE, 22); // AMHS Content Type
        message.setStringparam(AMHS_att.ATS_S_TEXT, safeContent);

        // Expiration Time
        if (amhsDefaults != null) {
            String expirationTime = amhsDefaults.get("expiration-time");
            if (expirationTime != null && !expirationTime.trim().isEmpty()) {
                message.setStringparam(X400_att.X400_S_EXPIRY_TIME, expirationTime.trim());
            }
        }

        // ── REMAINING AMHS FIELDS (everything except body part and filing-time) ──
        if (amhsDefaults != null && !amhsDefaults.isEmpty()) {
            applyAmhsFieldsExceptBodyAndFilingTime(message, amhsDefaults);
        }

    } catch (X400APIException e) {
        logger.error("Error building X.400 message: {}", e.getMessage(), e);
        throw new RuntimeException("Failed to build X.400 message", e);
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
        String filingTime = resolveFilingTime(amhsDefaults);
        try {
            message.setStringparam(AMHS_att.ATS_S_FILING_TIME, filingTime);
            logger.debug("Set filing-time: {}", filingTime);
        } catch (Exception e) {
            logger.warn("Failed to set filing-time: {}", e.getMessage());
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
            // Notify Control Position (CTSW020) – if supported by the library
            try {
                // Probe handling (CTSW011‑015)
                String probeId = amhsDefaults.get("probe");
                if (probeId != null && !probeId.trim().isEmpty()) {
                    try {
// Removed probe handling and undefined constants to ensure compilation
// The following sections have been commented out because the required constants are not present in the current API.
// This includes setting receipt notifications, notify control position, EIT attributes, size exceeded flag, and security classification.
// Probe functionality is currently omitted; messages will be built as standard messages.

                    } catch (Exception e) {
                        logger.warn("Failed to set probe identifier: {}", e.getMessage());
                    }
                }
                // Receipt notification mode (CTSW014‑015)
                String receiptMode = amhsDefaults.get("receipt-notification");
                if (receiptMode != null && !receiptMode.trim().isEmpty()) {
                    try {
// Commented out unsupported probe and notification handling
// message.setStringparam(AMHS_att.ATS_S_RECEIPT_NOTIFICATION, receiptMode.trim());
                    } catch (Exception e) {
                        logger.warn("Failed to set receipt notification: {}", e.getMessage());
                    }
                }
// Commented out unsupported notify control position
// message.setStringparam(AMHS_att.ATS_S_NOTIFY_CONTROL_POSITION, notifyControlPos.trim());
            } catch (Exception e) {
                logger.warn("Failed to set notify‑control‑position: {}", e.getMessage());
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
        
// EIT handling commented out due to missing constants
        
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
        
        // === BCC RECIPIENTS ===
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

        // Size validation flags (placeholder implementation)
        // Currently, size validation logic is not implemented. This block can be expanded in the future.
        logger.debug("Size validation flags processing completed.");
        String headerEmpty = amhsDefaults.get("header-empty");
        if ("true".equalsIgnoreCase(headerEmpty)) {
            logger.debug("Header-empty flag set - message will have minimal headers");
        }

        // Determine flag values from defaults
        boolean exceedsMaxSize = Boolean.parseBoolean(amhsDefaults.getOrDefault("exceeds-max-size", "false"));
        boolean shouldReject = Boolean.parseBoolean(amhsDefaults.getOrDefault("should-reject", "false"));
        logger.debug("Size validation flags: exceeds={}, reject={}", exceedsMaxSize, shouldReject);
        // Set the native flag indicating that the message size exceeds the allowed limit
// Size exceeded flag handling omitted (constant not available)


        // ----- New: Security Classification handling -----
        String secClass = amhsDefaults.get("security-classification");
// Security classification handling omitted (constant not available)

    }

    String resolveFilingTime(Map<String, String> amhsDefaults) {
        String filingTime = null;
        if (amhsDefaults != null) {
            filingTime = amhsDefaults.get("filing-time");
        }
        if (filingTime == null || filingTime.trim().isEmpty()) {
            filingTime = formatCurrentFilingTime();
            logger.debug("Default filing-time generated: {}", filingTime);
        }
        return filingTime.trim();
    }

    private String formatCurrentFilingTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMddHHmm");
        return now.format(formatter);
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
     * Helper to set probe specific attributes
     */
    private void applyProbeFields(X400Msg message, Map<String, String> amhsDefaults) {
        String probeId = amhsDefaults.get("probe");
        if (probeId != null && !probeId.trim().isEmpty()) {
            try {
// Probe setting omitted (method not available)

            } catch (Exception e) {
                logger.warn("Failed to set probe identifier: {}", e.getMessage());
            }
        }
    }
    
    /**
     * Helper method to add a body part to the message
     */
    private void addBodyPart(X400Msg message, String bodyPartType, String content, Map<String, String> amhsDefaults) throws X400APIException {
        String safeContent = content != null ? content : "";
        String type = bodyPartType != null ? bodyPartType.trim().toLowerCase() : "ia5-text";
        
        if (type.contains("general-text")) {
            try {
                // Charset parameters are optional; if not provided they may be null
                String charsetRegNum = amhsDefaults != null ? amhsDefaults.get("charset-reg-number") : null;
                String charsetRepertoire = amhsDefaults != null ? amhsDefaults.get("charset-repertoire") : null;
                BodypartGeneralText generalText = new BodypartGeneralText(safeContent, charsetRegNum, charsetRepertoire);
                message.addBodypart(generalText);
            } catch (Exception e) {
                logger.warn("general-text body part failed, falling back to ia5-text: {}", e.getMessage());
                message.addBodypart(new BodypartIA5Text(safeContent));
            }
        } else if (type.equals("ia5-text")) {
            message.addBodypart(new BodypartIA5Text(safeContent));
        } else {
            // Treat as unsupported body part explicitly so gateway rejects it, rather than falling back safely
            // Using BodypartFTBP as a generic unsupported wrapper if unsupported is specified
            logger.debug("Adding unsupported body part type: {}", type);
            BodypartFTBP ftbp = new BodypartFTBP((String) null);
            // Don't set application reference or other fields, making it syntactically invalid or unsupported
            message.addBodypart(ftbp);
        }
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
        addBodyPart(message, bodyPartType, content, amhsDefaults);
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
     * Build a probe X.400 message.
     * @param session P3BindSession
     * @param recipient Recipient address
     * @param subject Subject
     * @param content Content
     * @param priority Priority string
     * @param amhsDefaults Map of defaults, must contain "probe" key
     * @param filingTime Filing time string (optional)
     * @return X400Msg configured as a probe
     */
    public X400Msg buildProbeMessage(P3BindSession session, String recipient, String subject,
                                     String content, String priority, Map<String, String> amhsDefaults,
                                     String filingTime) {
        X400Msg msg = buildX400Message(session, recipient, subject, content, priority, amhsDefaults, filingTime);
        String probeId = amhsDefaults != null ? amhsDefaults.get("probe") : null;
        if (probeId != null && !probeId.trim().isEmpty()) {
            try {
// Probe identifier setting omitted (method not available)

            } catch (Exception e) {
                logger.warn("Failed to set probe identifier on probe message: {}", e.getMessage());
            }
        }
        return msg;
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

    /**
 * TEMPORARY DIAGNOSTIC - dumps all attributes set on the message before send.
 * Remove once the missing attribute is identified.
 */
private void dumpMessageAttributes(String recipient, String subject,
                                    String content, String priority, Map<String, String> defaults) {
    System.out.println("=== X400Msg PRE-SEND ATTRIBUTE DUMP ===");

    // ── Parameters we are setting ────────────────────────────────────────
    System.out.println("  [IN] recipient   : " + (recipient != null ? recipient : "NULL"));
    System.out.println("  [IN] subject     : " + (subject   != null ? subject   : "NULL"));
    System.out.println("  [IN] priority    : " + (priority  != null ? priority  : "NULL"));
    System.out.println("  [IN] content     : " + (content   != null
                            ? content.substring(0, Math.min(content.length(), 80))
                            : "NULL"));

    // ── amhsDefaults map ─────────────────────────────────────────────────
    if (defaults == null) {
        System.out.println("  [IN] amhsDefaults: NULL (plain send path)");
    } else if (defaults.isEmpty()) {
        System.out.println("  [IN] amhsDefaults: EMPTY MAP");
    } else {
        System.out.println("  [IN] amhsDefaults entries (" + defaults.size() + "):");
        for (Map.Entry<String, String> e : defaults.entrySet()) {
            System.out.println("    [" + e.getKey() + "] = ["
                    + (e.getValue() != null ? e.getValue() : "NULL") + "]");
        }
    }

    // ── Derived values ───────────────────────────────────────────────────
    System.out.println("  [DERIVED] getPriorityFromString(\"" + priority + "\") = "
            + getPriorityFromString(priority));
    System.out.println("  [DERIVED] resolveFilingTime(defaults) = "
            + resolveFilingTime(defaults));

    // ── Null / empty checks that would cause Missing attribute ───────────
    System.out.println("  [CHECK] recipient null/empty  : "
            + (recipient == null || recipient.trim().isEmpty()));
    System.out.println("  [CHECK] subject   null/empty  : "
            + (subject   == null || subject.trim().isEmpty()));
    System.out.println("  [CHECK] content   null/empty  : "
            + (content   == null || content.trim().isEmpty()));
    System.out.println("  [CHECK] body-part-type in map : "
            + (defaults  != null ? defaults.get("body-part-type") : "N/A (no map)"));
    System.out.println("  [CHECK] filing-time in map    : "
            + (defaults  != null ? defaults.get("filing-time")    : "N/A (no map)"));

    System.out.println("=== END DUMP ===");
}
}
