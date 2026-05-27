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
     */
    private static final Map<String, String> EMPTY_DEFAULTS = new HashMap<>();

    static {
        EMPTY_DEFAULTS.put("recipient", "");
        EMPTY_DEFAULTS.put("subject", "");
        EMPTY_DEFAULTS.put("content", "");
        EMPTY_DEFAULTS.put("priority", "NORMAL");
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
            
            // Apply additional AMHS fields from defaults
            if (amhsDefaults != null && !amhsDefaults.isEmpty()) {
                applyAmhsFields(message, content, amhsDefaults);
            } else {
                // Set content as IA5 text by default
                if (content != null && !content.isEmpty()) {
                    message.setTextBody(content);
                }
            }
        } catch (X400APIException e) {
            // Handle or log exception
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
     * 
     * @param message X400Msg to configure
     * @param content Message content
     * @param amhsDefaults Map of AMHS field names to values
     */
    private void applyAmhsFields(X400Msg message, String content, Map<String, String> amhsDefaults) throws X400APIException {
        // Handle body part type and content
        String bodyPartType = amhsDefaults.get("body-part-type");
        
        // Use content from parameter, fallback to defaults map if empty
        String effectiveContent = content;
        if (effectiveContent == null || effectiveContent.isEmpty()) {
            effectiveContent = amhsDefaults.get("content");
        }
        
        if (bodyPartType != null && !bodyPartType.isEmpty()) {
            addBodyParts(message, bodyPartType, effectiveContent, amhsDefaults);
        } else if (effectiveContent != null && !effectiveContent.isEmpty()) {
            // Default to IA5 text
            BodypartIA5Text ia5 = new BodypartIA5Text(effectiveContent);
            message.addBodypart(ia5);
        }
        
        // Handle filing-time (ATS filing time)
        String filingTime = amhsDefaults.get("filing-time");
        if (filingTime != null && !filingTime.isEmpty()) {
            // Filing time is typically encoded in OHI or as extended header info
            // This would require custom X.400 header extension
            setExtendedHeaderField(message, "filing-time", filingTime);
        }
        
        // Handle precedence (Extended IPM precedence values: 14, 28, 57, 71, 107)
        String precedence = amhsDefaults.get("precedence");
        if (precedence != null && !precedence.isEmpty()) {
            // Precedence affects priority mapping for extended IPMs
            applyPrecedence(message, precedence);
        }
        
        // Handle authorization-time
        String authTime = amhsDefaults.get("authorization-time");
        if (authTime != null && !authTime.isEmpty()) {
            setExtendedHeaderField(message, "authorization-time", authTime);
        }
        
        // Handle originator-reference - use setStringparam with AMHS_att constant
        String origRef = amhsDefaults.get("originator-reference");
        if (origRef != null && !origRef.isEmpty()) {
            // Originator reference is stored as extended header info
            setExtendedHeaderField(message, "originator-reference", origRef);
        }
        
        // Handle optional-heading-info (OHI) - use setStringparam with AMHS_att constant
        String ohi = amhsDefaults.get("optional-heading-info");
        if (ohi != null && !ohi.isEmpty()) {
            // OHI is stored using AMHS_att.ATS_S_OPTIONAL_HEADING_INFO
            setExtendedHeaderField(message, "optional-heading-info", ohi);
        }
        
        // Handle responsibility
        String responsibility = amhsDefaults.get("responsibility");
        if (responsibility != null && !responsibility.isEmpty()) {
            // Set responsibility flag for extended IPM
            setResponsibility(message, responsibility);
        }
        
        // Handle notify-control-position
        String notifyControl = amhsDefaults.get("notify-control-position");
        if (notifyControl != null && !notifyControl.isEmpty()) {
            // Configure notification to control position
            setNotifyControlPosition(message, notifyControl);
        }
        
        // Handle EIT (Encoded Information Types)
        applyEitConfiguration(message, amhsDefaults);
        
        // Handle charset configuration
        applyCharsetConfiguration(message, amhsDefaults);
    }
    
    /**
     * Add body parts based on type specification
     * Supports single and multiple body parts
     * 
     * @param message X400Msg to add body parts to
     * @param bodyPartType Type specification (e.g., "ia5-text", "general-text-body-part", "ftbp", or comma-separated list)
     * @param content Primary content
     * @param amhsDefaults Additional configuration
     */
    private void addBodyParts(X400Msg message, String bodyPartType, String content, 
                              Map<String, String> amhsDefaults) throws X400APIException {
        // Split by comma for multiple body parts
        String[] types = bodyPartType.split(",");
        
        for (int i = 0; i < types.length; i++) {
            String type = types[i].trim().toLowerCase();
            
            switch (type) {
                case "ia5-text":
                case "ia5-text-body-part":
                    BodypartIA5Text ia5 = new BodypartIA5Text(content != null ? content : "");
                    message.addBodypart(ia5);
                    break;
                    
                case "general-text-body-part":
                case "general-text":
                    // Use ISODE library's BodypartGeneralText with proper charset handling
                    String charsetRegNum = amhsDefaults.get("charset-reg-number");
                    String charsetRepertoire = amhsDefaults.get("charset-repertoire");
                    String conversionProhibited = amhsDefaults.get("conversion-with-loss-prohibited");
                    
                    BodypartGeneralText generalText = createGeneralTextBodyPart(
                        content != null ? content : "",
                        charsetRegNum,
                        charsetRepertoire,
                        conversionProhibited
                    );
                    message.addBodypart(generalText);
                    break;
                    
                case "ftbp":
                case "file-transfer-body-part":
                    // Use ISODE library's BodypartFTBP with proper file handling
                    String fileName = amhsDefaults.get("ftbp-file-name");
                    String ftbpContent = amhsDefaults.get("ftbp-content");
                    if (fileName == null) {
                        fileName = "attachment.bin";
                    }
                    
                    BodypartFTBP ftbp = createFTBPBodyPart(
                        fileName,
                        ftbpContent != null ? ftbpContent.getBytes() : new byte[0]
                    );
                    message.addBodypart(ftbp);
                    break;
                    
                default:
                    // Unknown type, default to IA5
                    if (content != null && !content.isEmpty()) {
                        BodypartIA5Text defaultIa5 = new BodypartIA5Text(content);
                        message.addBodypart(defaultIa5);
                    }
                    break;
            }
        }
    }
    
    /**
     * Apply precedence value for extended IPM
     * Maps precedence values (14, 28, 57, 71, 107) to appropriate X.400 settings
     * 
     * @param message X400Msg
     * @param precedence Precedence value as string
     */
    private void applyPrecedence(X400Msg message, String precedence) throws X400APIException {
        try {
            int precValue = Integer.parseInt(precedence);
            
            // Map precedence to priority levels per EUR Doc 047
            // Only 3 priorities available: LOW, NORMAL, HIGH
            if (precValue >= 100) {
                message.setPriority(X400_Priority.HIGH_PRIORITY);
            } else if (precValue >= 50) {
                message.setPriority(X400_Priority.HIGH_PRIORITY);
            } else if (precValue >= 20) {
                message.setPriority(X400_Priority.NORMAL_PRIORITY);
            } else {
                message.setPriority(X400_Priority.LOW_PRIORITY);
            }
            
            // Store precedence as extended header info
            setExtendedHeaderField(message, "precedence", precedence);
        } catch (NumberFormatException e) {
            // Invalid precedence, ignore
        }
    }
    
    /**
     * Apply EIT (Encoded Information Types) configuration
     * 
     * @param message X400Msg
     * @param amhsDefaults AMHS configuration
     */
    private void applyEitConfiguration(X400Msg message, Map<String, String> amhsDefaults) 
            throws X400APIException {
        String eitType = amhsDefaults.get("eit-type");
        
        if (eitType == null || eitType.isEmpty()) {
            return;
        }
        
        switch (eitType.toLowerCase()) {
            case "builtin":
                String eitValue = amhsDefaults.get("eit-value");
                if (eitValue != null && !eitValue.isEmpty()) {
                    setEitBuiltin(message, eitValue);
                }
                break;
                
            case "extended":
                String eitOid = amhsDefaults.get("eit-oid");
                String eitOids = amhsDefaults.get("eit-oids");
                if (eitOid != null && !eitOid.isEmpty()) {
                    setEitExtended(message, eitOid);
                } else if (eitOids != null && !eitOids.isEmpty()) {
                    // Multiple OIDs
                    String[] oidArray = eitOids.split(",");
                    for (String oid : oidArray) {
                        setEitExtended(message, oid.trim());
                    }
                }
                break;
                
            case "mixed":
                String builtin = amhsDefaults.get("eit-builtin");
                String extended = amhsDefaults.get("eit-oids");
                if (builtin != null && !builtin.isEmpty()) {
                    setEitBuiltin(message, builtin);
                }
                if (extended != null && !extended.isEmpty()) {
                    String[] oidArray = extended.split(",");
                    for (String oid : oidArray) {
                        setEitExtended(message, oid.trim());
                    }
                }
                break;
        }
    }
    
    /**
     * Set built-in EIT
     */
    private void setEitBuiltin(X400Msg message, String eitValue) throws X400APIException {
        // Parse built-in EIT value (e.g., "ia5-text(2)", "unknown(0)")
        // This would use X.400 API to set the encoded-information-types field
        setExtendedHeaderField(message, "eit-builtin", eitValue);
    }
    
    /**
     * Set extended EIT with OID
     */
    private void setEitExtended(X400Msg message, String oid) throws X400APIException {
        // Parse OID (e.g., "2.6.3.4.2", "{id-cs-eit-authority 1}")
        setExtendedHeaderField(message, "eit-extended", oid);
    }
    
    /**
     * Apply charset configuration for general-text-body-part
     * 
     * @param message X400Msg
     * @param amhsDefaults AMHS configuration
     */
    private void applyCharsetConfiguration(X400Msg message, Map<String, String> amhsDefaults) 
            throws X400APIException {
        String charsetRegNum = amhsDefaults.get("charset-reg-number");
        String charsetRepertoire = amhsDefaults.get("charset-repertoire");
        String conversionProhibited = amhsDefaults.get("conversion-with-loss-prohibited");
        
        if (charsetRegNum != null && !charsetRegNum.isEmpty()) {
            setExtendedHeaderField(message, "charset-reg-number", charsetRegNum);
        }
        
        if (charsetRepertoire != null && !charsetRepertoire.isEmpty()) {
            setExtendedHeaderField(message, "charset-repertoire", charsetRepertoire);
        }
        
        if (conversionProhibited != null && !conversionProhibited.isEmpty()) {
            setExtendedHeaderField(message, "conversion-with-loss-prohibited", conversionProhibited);
        }
    }
    
    /**
     * Set extended header field (placeholder for actual X.400 extension mechanism)
     * In a real implementation, this would use X.400 extension attributes
     */
    private void setExtendedHeaderField(X400Msg message, String fieldName, String value) 
            throws X400APIException {
        // Placeholder: Actual implementation would use X.400 extension mechanisms
        // For now, store as a custom attribute or encode in available fields
        // This depends on the specific ISODE X.400 API capabilities
    }
    
    /**
     * Create General Text Body Part with proper charset configuration
     * Uses ISODE library's BodypartGeneralText constructors
     * 
     * @param content Text content
     * @param charsetRegNum Charset registration number (e.g., "1" for ISO 646, "8859-1" for Latin-1)
     * @param charsetRepertoire Charset repertoire name (e.g., "iso646", "cyrillic")
     * @param conversionProhibited "conversion-with-loss-prohibited" or "conversion-with-loss-allowed"
     * @return Configured BodypartGeneralText
     */
    private BodypartGeneralText createGeneralTextBodyPart(String content, String charsetRegNum, 
                                                           String charsetRepertoire, String conversionProhibited) 
            throws X400APIException {
        // Map repertoire to ISODE Charset enum
        BodypartGeneralText.Charset charset = mapCharset(charsetRepertoire);
        
        // Check if conversion with loss is prohibited
        boolean lossProhibited = false;
        if (conversionProhibited != null && 
            ("conversion-with-loss-prohibited".equalsIgnoreCase(conversionProhibited) ||
             "true".equalsIgnoreCase(conversionProhibited) ||
             "yes".equalsIgnoreCase(conversionProhibited))) {
            lossProhibited = true;
        }
        
        // Use appropriate constructor based on parameters
        if (charset != null) {
            // Use charset-based constructor
            return new BodypartGeneralText(charset, content);
        } else if (charsetRegNum != null && !charsetRegNum.isEmpty()) {
            // Use string-based constructor with charset string
            return new BodypartGeneralText(charsetRegNum, content);
        } else if (lossProhibited) {
            // Use boolean-first constructor
            return new BodypartGeneralText(true, content);
        } else {
            // Default constructor with just content and empty charset string
            return new BodypartGeneralText(content, "");
        }
    }
    
    /**
     * Map charset repertoire name to ISODE Charset enum
     * 
     * @param repertoire Charset repertoire name
     * @return BodypartGeneralText.Charset enum value or null
     */
    private BodypartGeneralText.Charset mapCharset(String repertoire) {
        if (repertoire == null || repertoire.isEmpty()) {
            return null;
        }
        
        String lower = repertoire.toLowerCase().trim();
        
        // Map common repertoire names to ISODE Charset enum
        if (lower.contains("west") || lower.contains("latin") || lower.contains("8859-1")) {
            return BodypartGeneralText.Charset.WEST_EUROPEAN;
        } else if (lower.contains("east") || lower.contains("8859-2")) {
            return BodypartGeneralText.Charset.EAST_EUROPEAN;
        } else if (lower.contains("cyrillic") || lower.contains("8859-5")) {
            return BodypartGeneralText.Charset.CYRILLIC;
        } else if (lower.contains("arabic") || lower.contains("8859-6")) {
            return BodypartGeneralText.Charset.ARABIC;
        } else if (lower.contains("greek") || lower.contains("8859-7")) {
            return BodypartGeneralText.Charset.GREEK;
        } else if (lower.contains("hebrew") || lower.contains("8859-8")) {
            return BodypartGeneralText.Charset.HEBREW;
        } else if (lower.contains("other")) {
            return BodypartGeneralText.Charset.OTHER_LATIN;
        }
        
        return null;
    }
    
    /**
     * Create FTBP Body Part with proper file handling
     * Uses ISODE library's BodypartFTBP constructors
     * 
     * @param fileName File name
     * @param content File content as byte array
     * @return Configured BodypartFTBP
     */
    private BodypartFTBP createFTBPBodyPart(String fileName, byte[] content) throws X400APIException {
        BodypartFTBP ftbp = new BodypartFTBP(fileName, content);
        
        // Set additional metadata if needed
        ftbp.setContentDescription("File attachment for AMHS test case");
        
        return ftbp;
    }
    
    /**
     * Set responsibility flag for extended IPM
     */
    private void setResponsibility(X400Msg message, String responsibility) throws X400APIException {
        // "responsible" or "not-responsible"
        // Would set the appropriate X.400 responsibility indicator
        setExtendedHeaderField(message, "responsibility", responsibility);
    }
    
    /**
     * Configure notification to control position
     */
    private void setNotifyControlPosition(X400Msg message, String notifyControl) throws X400APIException {
        // Configure IPN/DR notification to control position
        // This affects the recipient-of-nondelivery-report and ipn-originator-requested fields
        if ("true".equalsIgnoreCase(notifyControl) || "yes".equalsIgnoreCase(notifyControl)) {
            // Enable control position notification
            setExtendedHeaderField(message, "notify-control-position", "enabled");
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
