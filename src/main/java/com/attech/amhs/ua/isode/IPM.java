/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.attech.amhs.ua.isode;

/**
 *
 * @author ANDH
 */
public class IPM {
    
    protected Integer sequenceNumber;
    
    protected int priority;
    
    protected String contentId;
    protected String messageId;
    protected String ipmId;
    
    protected String subject;
    protected String content;
    
    protected String aftnPriority;
    protected String aftnFilingTime;
    protected String aftnOHI;
    protected Boolean aftnExtention;
    
    protected String origin;
    
    

    /**
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @param subject the subject to set
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return the messageId
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * @param messageId the messageId to set
     */
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    /**
     * @return the ipmId
     */
    public String getIpmId() {
        return ipmId;
    }

    /**
     * @param ipmId the ipmId to set
     */
    public void setIpmId(String ipmId) {
        this.ipmId = ipmId;
    }

    /**
     * @return the aftnPriority
     */
    public String getAftnPriority() {
        return aftnPriority;
    }

    /**
     * @param aftnPriority the aftnPriority to set
     */
    public void setAftnPriority(String aftnPriority) {
        this.aftnPriority = aftnPriority;
    }

    /**
     * @return the aftnFilingTime
     */
    public String getAftnFilingTime() {
        return aftnFilingTime;
    }

    /**
     * @param aftnFilingTime the aftnFilingTime to set
     */
    public void setAftnFilingTime(String aftnFilingTime) {
        this.aftnFilingTime = aftnFilingTime;
    }

    /**
     * @return the aftnOHI
     */
    public String getAftnOHI() {
        return aftnOHI;
    }

    /**
     * @param aftnOHI the aftnOHI to set
     */
    public void setAftnOHI(String aftnOHI) {
        this.aftnOHI = aftnOHI;
    }

    /**
     * @return the aftnExtention
     */
    public Boolean getAftnExtention() {
        return aftnExtention;
    }

    /**
     * @param aftnExtention the aftnExtention to set
     */
    public void setAftnExtention(Boolean aftnExtention) {
        this.aftnExtention = aftnExtention;
    }

    /**
     * @return the origin
     */
    public String getOrigin() {
        return origin;
    }

    /**
     * @param origin the origin to set
     */
    public void setOrigin(String origin) {
        this.origin = origin;
    }

    /**
     * @return the sequenceNumber
     */
    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    /**
     * @param sequenceNumber the sequenceNumber to set
     */
    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    /**
     * @return the priority
     */
    public int getPriority() {
        return priority;
    }

    /**
     * @param priority the priority to set
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * @return the contentId
     */
    public String getContentId() {
        return contentId;
    }

    /**
     * @param contentId the contentId to set
     */
    public void setContentId(String contentId) {
        this.contentId = contentId;
    }


    
    
}
