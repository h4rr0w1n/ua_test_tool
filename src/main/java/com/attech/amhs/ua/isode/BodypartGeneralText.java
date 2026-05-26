/*
 * BodypartGeneralText - General Text Body Part for AMHS X.400 messages
 * Supports ISO 646, ISO 8859-1, and other character sets as per EUR Doc 047
 */
package com.attech.amhs.ua.isode;

import com.isode.x400.highlevel.Bodypart;
import com.isode.x400.highlevel.X400APIException;

/**
 * Represents a general-text-body-part in X.400 messages
 * Used for extended IPMs with specific charset requirements
 */
public class BodypartGeneralText extends Bodypart {
    
    private String content;
    private String charsetRegNumber;
    private String charsetRepertoire;
    private String conversionWithLossProhibited;
    
    /**
     * Create a general text body part with default settings
     * @param content Text content
     */
    public BodypartGeneralText(String content) {
        this(content, null, null);
    }
    
    /**
     * Create a general text body part with charset configuration
     * @param content Text content
     * @param charsetRegNumber Charset registration number (e.g., "1", "6", "8859-1")
     * @param charsetRepertoire Charset repertoire name (e.g., "iso646", "cyrillic", "cjk")
     */
    public BodypartGeneralText(String content, String charsetRegNumber, String charsetRepertoire) {
        this.content = content != null ? content : "";
        this.charsetRegNumber = charsetRegNumber;
        this.charsetRepertoire = charsetRepertoire;
    }
    
    /**
     * Get the text content
     * @return Content string
     */
    public String getContent() {
        return content;
    }
    
    /**
     * Set the text content
     * @param content Text content
     */
    public void setContent(String content) {
        this.content = content;
    }
    
    /**
     * Get charset registration number
     * @return Charset reg number or null
     */
    public String getCharsetRegNumber() {
        return charsetRegNumber;
    }
    
    /**
     * Set charset registration number
     * @param charsetRegNumber Registration number
     */
    public void setCharsetRegNumber(String charsetRegNumber) {
        this.charsetRegNumber = charsetRegNumber;
    }
    
    /**
     * Get charset repertoire
     * @return Repertoire name or null
     */
    public String getCharsetRepertoire() {
        return charsetRepertoire;
    }
    
    /**
     * Set charset repertoire
     * @param charsetRepertoire Repertoire name
     */
    public void setCharsetRepertoire(String charsetRepertoire) {
        this.charsetRepertoire = charsetRepertoire;
    }
    
    /**
     * Get conversion with loss prohibited flag
     * @return Flag value or null
     */
    public String getConversionWithLossProhibited() {
        return conversionWithLossProhibited;
    }
    
    /**
     * Set conversion with loss prohibited flag
     * @param conversionWithLossProhibited Flag value ("conversion-with-loss-prohibited" or "conversion-with-loss-allowed")
     */
    public void setConversionWithLossProhibited(String conversionWithLossProhibited) {
        this.conversionWithLossProhibited = conversionWithLossProhibited;
    }
    
    /**
     * Check if this is ISO 646 (US-ASCII) compatible content
     * @return true if content only contains ISO 646 characters
     */
    public boolean isIso646Compatible() {
        if (content == null || content.isEmpty()) {
            return true;
        }
        
        // ISO 646 is essentially US-ASCII (7-bit)
        for (char c : content.toCharArray()) {
            if (c > 127) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Validate content against specified repertoire
     * @return true if content is valid for the configured repertoire
     */
    public boolean validateContentForRepertoire() {
        if (charsetRepertoire == null || charsetRepertoire.isEmpty()) {
            return true; // No restriction
        }
        
        String lowerRepertoire = charsetRepertoire.toLowerCase();
        
        if (lowerRepertoire.contains("iso646") || lowerRepertoire.contains("us-ascii")) {
            return isIso646Compatible();
        }
        
        // For other repertoires (ISO 8859-1, Cyrillic, CJK, etc.), 
        // validation would require specific character set checks
        // This is a simplified implementation
        return true;
    }
}
