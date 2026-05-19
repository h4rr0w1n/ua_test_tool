/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.attech.amhs.ua.isode;

import com.attech.amhs.ua.db.tables.Inbox;
import com.attech.amhs.ua.isode.enums.MessageType;
import com.attech.amhs.ua.db.enums.Priority;
import com.attech.amhs.ua.db.enums.ReportType;
import com.attech.amhs.ua.db.tables.Ipn;
import com.attech.amhs.ua.db.tables.Report;
import com.attech.amhs.ua.db.tables.ReportDetail;
import com.attech.amhs.ua.db.tables.Sent;
import com.attech.amhs.ua.common.MessageUtils;
import com.attech.amhs.ua.common.enums.MsgClass;
import com.attech.amhs.ua.db.tables.InboxAttachment;
import com.isode.x400.highlevel.Bodypart;
import com.isode.x400.highlevel.BodypartIA5Text;
import com.isode.x400.highlevel.ReceiveMsg;
import com.isode.x400.highlevel.X400APIException;
import com.isode.x400api.AMHS_att;
import com.isode.x400api.BodyPart;
import com.isode.x400api.MSMessage;
import com.isode.x400api.Recip;
import com.isode.x400api.X400;
import com.isode.x400api.X400_att;
import com.isode.x400api.X400ms;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Saitama
 */
public class ReceivedMessage1 {

    /**
     * @return the filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * @param filename the filename to set
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * @return the filelength
     */
    public int getFilelength() {
        return filelength;
    }

    /**
     * @param filelength the filelength to set
     */
    public void setFilelength(int filelength) {
        this.filelength = filelength;
    }

    private final SimpleDateFormat dateFormatDay = new SimpleDateFormat("yyyy-MM-dd");

    private MsgClass clazz;

    private Integer sequenceNumber;

    private String messageId;
    private String ipmId;
    private String subjectIpmId;
    private String subjectId;

    private Integer ipnRequest;

    private Priority priority;
    private String subject;
    private String content;

    private String atsPriority;
    private String atsFilingTime;
    private String atsOHI;
    private Integer atsExtention;

    private String origin;
    private String submissionTime;
    private String deliveriedTime;

    private String ipnRecipient;
    private String ipnReceiptTime;
    private String contentID;

    
    
    private String oeit;
    private int    charset;
    
    private int numberOfBodyparts;

    
    private MessageType type;
    private Boolean isIPN;
    
    private String filename;
    private int    filelength;

    private List<Attachment> attachments;
//    private List<Attach> attachs;
    private List<ReportRecipient> reportRecips;
    private List<Recipient> recipients;
    private List<Recipient> ccRecipients;
    private List<Recipient> bccRecipient;
    private List<Recipient> envelopeRecipients;
    private Integer code;
    private String errorMessage;
    private MSMessage message;

    //private int NumberOfBodyPart;
    
    private String refOID;
    private String ConTDesc;
    private String modificaiondate;
    public  int extract_attachment_mode = 0;    
    
    private boolean isExtend = false;
    
    public ReceivedMessage1() {
        this.attachments = new ArrayList<>();
      
        
    }

    public ReceivedMessage1(MSMessage message,int mode) throws X400APIException {
        this();
        this.message = message;
        this.isIPN = this.verifyIPNMessage();
      
       
        switch (message.GetType()) {
            case X400_att.X400_MSG_REPORT:
                this.type = MessageType.REPORT;
                this.clazz = MsgClass.STORED_MSG;
                parseReport();
                break;

            case X400_att.X400_MSG_PROBE:
                this.type = MessageType.PROBE;
                this.clazz = MsgClass.SUBMITTED_MSG;
                parseProbe();
                break;

            case X400_att.X400_MSG_SUBMITTED_MESSAGE:
                this.clazz = MsgClass.SUBMITTED_MSG;

            case X400_att.X400_MSG_MESSAGE:
                this.clazz = MsgClass.STORED_MSG;

            default:
                if (this.isIPN) {
                    this.type = MessageType.IPN;
                    this.parseIPN();
                } else {
                    this.type = MessageType.IPM;
                    
                    this.parseIPM();
                }
                break;
        }
    }

    // CONSTRUCTORS
    //public class ReceiveMsg extends MSMessage 
    //    private MSMessage message;
    //public static void showMessage(ReceiveMsg rm) throws X400APIException {
    //int numOfBodyparts = rm.getNumberOfBodyparts();
//    int numOfBodyparts = rm.getNumberOfBodyparts();
//                if (numOfBodyparts == 0) {
//                    System.out.println("There are no bodyparts");
//                } else if (numOfBodyparts == 1) {
//                    System.out.println("There is one bodypart");
//                } else {
//                    System.out.println("There are " + numOfBodyparts + " bodyparts");
//                }
//                
    
    /*
     DUC
        NHAN DIEN VAN TU DAY
     public ReceivedMessage1 get(Integer seq) throws X400APIException, IOException 
    */
    public ReceivedMessage1(ReceiveMsg receivedMessage,int mode) throws X400APIException {
        this();

       // NumberOfBodyPart = receivedMessage.getNumberOfBodyparts();
        //System.out.println("DUC 6 Number of body parts = " + NumberOfBodyPart);
        
        this.message = receivedMessage;
        this.isIPN= this.verifyIPNMessage();
        this.extract_attachment_mode = mode;
        switch (receivedMessage.GetType()) {
            case X400_att.X400_MSG_REPORT:
                this.type = MessageType.REPORT;
                this.clazz = MsgClass.STORED_MSG;
                parseReport();
                break;

            case X400_att.X400_MSG_PROBE:
                this.type = MessageType.PROBE;
                this.clazz = MsgClass.SUBMITTED_MSG;
                parseProbe();
                break;

            case X400_att.X400_MSG_SUBMITTED_MESSAGE:
                this.clazz = MsgClass.SUBMITTED_MSG;
            case X400_att.X400_MSG_MESSAGE:
                this.clazz = MsgClass.STORED_MSG;
            default:
                if (this.isIPN) {
                    this.type = MessageType.IPN;
                    this.parseIPN();
                } else {
                    
                    this.type = MessageType.IPM;
                   // this.isExtend = receivedMessage.getIntParam(AMHS_att.ATS_N_EXTENDED);      // ALWAYS = -1
                    numberOfBodyparts = receivedMessage.getNumberOfBodyparts();
                    System.out.println("<ReceivedMessage1 235>  Number of body parts = " + numberOfBodyparts);
                    this.parseIPM();                    // Vao đây
                }
                break;
        }

//            case X400_att.X400_MSG_SUBMITTED_MESSAGE:
//                // this.type = MessageType.SUBMITTED_MESSAGE;
//                // this.isIPN = verifyIPNMessage(message);
//                if (!receivedMessage.isIPN()) {
//                    this.type = MessageType.IPN;
//                    parseIPN(message);
//                } else {
//                    this.type = MessageType.IPM;
//                    parseIPM(message);
//                }
//                break;
//
//            case X400_att.X400_MSG_MESSAGE:
//                this.type = MessageType.STORED_MESSAGE;
//                this.isIPN = verifyIPNMessage(message);
//                if (this.isIPN) {
//                    this.type = MessageType.IPN;
//                    parseIPN(message);
//                } else {
//                    this.type = MessageType.IPM;
//                    parseIPM(message);
//                }
//
//                break;
//
//            case X400_att.X400_MSG_REPORT:
//                this.type = MessageType.REPORT;
//                parseReport(message);
//                break;
//
//            case X400_att.X400_MSG_PROBE:
//                this.type = MessageType.PROBE;
//                parseProbe(message);
//                break;
    }
    //----------------------------------------------------------
    //
    //
    //----------------------------------------------------------
    // PUBLIC METHODS
    public Inbox getInbox() {
        if (this.type != MessageType.IPM) {
            return null;
        }

        Inbox inbox = new Inbox();
        inbox.setSeq(this.sequenceNumber);
        inbox.setAtsPriority(this.atsPriority);
        inbox.setAtsExtention(this.atsExtention);
        inbox.setAtsFilingTime(this.atsFilingTime);
        inbox.setAtsOhi(this.atsOHI);
        inbox.setCategory(MessageUtils.getCategory(this.content));

        // String timeStr = rm.getSubmitedTime();
        String timeStr = this.submissionTime;
        Date timeDate = MessageUtils.parseDate(timeStr);
        inbox.setSubmissionTimeStr(timeStr);
        inbox.setSubmissionTime(timeDate);
//        inbox.setDate(dateFormatDay.format(timeDate));

        timeStr = this.deliveriedTime;
        timeDate = MessageUtils.parseDate(timeStr);
        inbox.setDeliveryTimeStr(timeStr);
        inbox.setDeliveryTime(timeDate);
        inbox.setDate(dateFormatDay.format(timeDate));
//
//        if (!validateDOF(inbox.getDate())) {
//            return null;
//        }

        inbox.setOrigin(this.origin);
        inbox.setIpmId(this.ipmId);
        inbox.setIpnRequest(this.ipnRequest);
        inbox.setMessageId(this.messageId);
        inbox.setPriority(this.priority.value);
        inbox.setRead(false);

        inbox.setSubject(this.subject);
        inbox.setContent(MessageUtils.normalizeMessageContent(this.content));

        this.recipients.forEach((recipient) -> {
            inbox.addAddress(recipient.getAddress(), "TO");
        });

        this.ccRecipients.forEach((recipient) -> {
            inbox.addAddress(recipient.getAddress(), "CC");
        });

        this.bccRecipient.forEach((recipient) -> {
            inbox.addAddress(recipient.getAddress(), "BCC");
        });

        this.attachments.forEach((attachment) -> {
            InboxAttachment inboxAttachment = new InboxAttachment();
            inboxAttachment.setName(attachment.getName());
            inboxAttachment.setContent(attachment.getData());
//            inboxAttachment.setLength(attachment.get/);
            inbox.addAttachment(inboxAttachment);
        });

        inbox.setRef_oid(this.refOID);
        inbox.setDescription(this.ConTDesc);
         inbox.setModificatoinDate(this.modificaiondate);
        
        inbox.setOeit(this.oeit);
        inbox.setNumberOfBodyparts(this.numberOfBodyparts);
         
        inbox.setAttachedFile(this.attachments != null && !this.attachments.isEmpty());
        if( this.isExtend) {
            inbox.setExtended(true);
            
        } else {
            inbox.setExtended(false);
        }
        return inbox;
        
    }

    public Ipn getIpn() {

//        if (!this.isIPN) {
//            return null;
//        }
        Ipn ipn = new Ipn();
        ipn.setIsSent((this.type == MessageType.SUBMITTED_MESSAGE));
        ipn.setMessageId(this.messageId);
        ipn.setOrAddress(this.origin);
        ipn.setReceiptTime(this.ipnReceiptTime);
        ipn.setRecipientAddress(this.ipnRecipient);
        ipn.setSubjectId(this.subjectIpmId);
        ipn.setSubmissionTime(this.submissionTime);
        ipn.setSequence(this.sequenceNumber);
        return ipn;
    }

    public Report getReportFromIpn() {

//        if (!this.isIPN) {
//            return null;
//        }
        Report report = new Report();
        report.setType(ReportType.IPN);
        report.setSequence(this.sequenceNumber);
        report.setOrigin(this.origin);
        report.setSubjectId(this.subjectIpmId);
        report.setDeliveriedTime(this.deliveriedTime);
        report.setReceiptTime(this.ipnReceiptTime);
        report.setRecipient(this.ipnRecipient);
        Date date = MessageUtils.parseDate(this.deliveriedTime);
        report.setReceivedDate(date);
        return report;
    }
    //----------------------------------------------------------------------
    //
    //
    //----------------------------------------------------------------------

    public Sent getSent(Sent sent) {
        final Date date = MessageUtils.parseDate(this.submissionTime);
        if (sent == null) {
            sent = new Sent();

            for (Recipient address : this.recipients) {
                sent.addAddress(address.getAddress(), "To");
            }

            for (Recipient address : this.ccRecipients) {
                sent.addAddress(address.getAddress(), "Cc");
            }

            for (Recipient address : this.bccRecipient) {
                sent.addAddress(address.getAddress(), "Bcc");
            }

            for (Attachment attachment : this.attachments) {
                sent.addAttach(attachment.getSentAttachment());
            }
        }

        sent.setAtsExtention(this.atsExtention);
        sent.setAtsFilingTime(this.atsFilingTime);
        sent.setAtsOhi(this.atsOHI);
        sent.setAtsPriority(this.atsPriority);
        sent.setCategory(MessageUtils.getCategory(this.content));

        sent.setIpmId(this.ipmId);
        sent.setMessageId(this.messageId);
        sent.setOrigin(this.origin);
        sent.setPriority(this.priority.value);
        sent.setSeq(this.sequenceNumber);
        sent.setSubject(this.subject);
        sent.setContent(MessageUtils.normalizeMessageContent(this.content));
        sent.setSubmissionTimeStr(this.submissionTime);
        sent.setSubmissionTime(date);
        sent.setDate(dateFormatDay.format(date));
        if (!validateDOF(sent.getDate())) {
            return null;
        }
        //        if (this.getType() == MessageType.PROBE) {
        //            sent.setTypeProbe(true);
        //        sent.setContentLenght(this.);
        //        }
        sent.setAttachedFile(this.attachments != null && !this.attachments.isEmpty());
        return sent;
    }
    //----------------------------------------------------------------------
    //
    //
    //----------------------------------------------------------------------
    public Sent getSent() {
        final Date date = MessageUtils.parseDate(this.submissionTime);
        Sent sent = new Sent();

        for (Recipient address : this.recipients) {
            sent.addAddress(address.getAddress(), "TO");
        }

        for (Recipient address : this.ccRecipients) {
            sent.addAddress(address.getAddress(), "CC");
        }

        for (Recipient address : this.bccRecipient) {
            sent.addAddress(address.getAddress(), "BCC");
        }

        for (Attachment attachment : this.attachments) {
            sent.addAttach(attachment.getSentAttachment());
        }

        sent.setType("IPM");                                        // Sens IPM Message
        sent.setAtsExtention((this.isExtend)?1:0);
        sent.setAtsFilingTime(this.atsFilingTime);
        sent.setAtsOhi(this.atsOHI);
        sent.setAtsPriority(this.atsPriority);
        sent.setCategory(MessageUtils.getCategory(this.content));

        sent.setIpmId(this.ipmId);
        sent.setMessageId(this.messageId);
        sent.setOrigin(this.origin);
        sent.setPriority(this.priority.value);
        sent.setSeq(this.sequenceNumber);
        sent.setSubject(this.subject);
        sent.setContent(this.content);
        sent.setSubmissionTimeStr(this.submissionTime);
        sent.setSubmissionTime(date);
        sent.setDate(dateFormatDay.format(date));
        
        sent.setOeit(this.oeit);
        sent.setNumberOfBodyparts(this.numberOfBodyparts);
        
        if (!validateDOF(sent.getDate())) {
            return null;
        }
//        if (this.getType() == MessageType.PROBE) {
//            sent.setTypeProbe(true);
//        sent.setContentLenght(this.);
//        }
        sent.setAttachedFile(this.attachments != null && !this.attachments.isEmpty());
        return sent;
    }
    //----------------------------------------------------------------------
    //
    //
    //----------------------------------------------------------------------

    public Report getReport() throws X400APIException {

        Report report = new Report();
        report.setType(ReportType.REPORT);
        report.setOrigin("<SYSTEM>");
        report.setSequence(getIntParam(this.message, X400_att.X400_N_MS_SEQUENCE_NUMBER));
        report.setSubjectId(getStrParam(this.message, X400_att.X400_S_SUBJECT_IDENTIFIER));
        report.setReportDetails(getReportDetail(this.message));
        return report;

//        this.sequenceNumber = getIntParam(message, X400_att.X400_N_MS_SEQUENCE_NUMBER);
//        this.origin = getStrParam(message, X400_att.X400_S_OR_ADDRESS);
//        this.messageId = getStrParam(message, X400_att.X400_S_MESSAGE_IDENTIFIER);
//        this.subjectId = getStrParam(message, X400_att.X400_S_SUBJECT_IDENTIFIER);
    }
    //----------------------------------------------------------------------
    //
    //
    //----------------------------------------------------------------------

    public Sent getSentFromIpn() {
        final Date date = MessageUtils.parseDate(this.submissionTime);

        Sent sent = new Sent();
        sent.setType("IPN");

//        for (Recipient address : receivedMessage.getRecipients()) {
//            sent.addAddress(address.getAddress(), "To");
//        }
//        
        sent.addAddress(this.ipnRecipient, "To");

//        for (Recipient address : receivedMessage.getCcRecipients()) {
//            sent.addAddress(address.getAddress(), "Cc");
//        }
//
//        for (Recipient address : receivedMessage.getBccRecipient()) {
//            sent.addAddress(address.getAddress(), "Bcc");
//        }
//
//        for (Attachment attachment : receivedMessage.getAttachments()) {
//            sent.addAttach(attachment.getSentAttachment());
//        }
//        sent.setAtsExtention(receivedMessage.getAtsExtention());
//        sent.setAtsFilingTime(receivedMessage.getAtsFilingTime());
//        sent.setAtsOhi(receivedMessage.getAtsOHI());
//        sent.setAtsPriority(receivedMessage.getAtsPriority());
//        sent.setCategory(MessageUtils.getCategory(receivedMessage.getContent()));
//        sent.setIpmId(receivedMessage.getIpmId());
        sent.setMessageId(this.messageId);
        sent.setOrigin(this.origin);
        sent.setPriority(this.priority.value);
        sent.setSeq(this.sequenceNumber);
        sent.setSubject("");

        StringBuilder contentBuilder = new StringBuilder();
        contentBuilder.append("Receipt Notification For Message\n");
        contentBuilder.append("Subject IPM: ").append(this.subjectIpmId).append("\n");
        contentBuilder.append("Recept Time: ").append(this.ipnReceiptTime).append("\n");
        sent.setContent(contentBuilder.toString());
        sent.setSubmissionTimeStr(this.submissionTime);
        sent.setSubmissionTime(date);
        sent.setDate(dateFormatDay.format(date));
        sent.setAttachedFile(false);
        return sent;
    }

    public Sent getSentFromProbe() {
        final Date date = MessageUtils.parseDate(this.getSubmissionTime());

        Sent sent = new Sent();
        sent.setType("PROBE");

        for (Recipient address : this.recipients) {
            sent.addAddress(address.getAddress(), "To");
        }

        for (Recipient address : this.ccRecipients) {
            sent.addAddress(address.getAddress(), "Cc");
        }

        for (Recipient address : this.bccRecipient) {
            sent.addAddress(address.getAddress(), "Bcc");
        }

        for (Attachment attachment : this.attachments) {
            sent.addAttach(attachment.getSentAttachment());
        }

        sent.setAtsExtention(this.atsExtention);
        sent.setAtsFilingTime(this.atsFilingTime);
        sent.setAtsOhi(this.atsOHI);
        sent.setAtsPriority(this.atsPriority);
        // sent.setCategory(MessageUtils.getCategory( receivedMessage.getContent()));

        sent.setIpmId(this.ipmId);
        sent.setMessageId(this.messageId);
        sent.setOrigin(this.origin);
        sent.setPriority(this.priority.value);
        sent.setSeq(this.sequenceNumber);
        sent.setSubject(this.subject);
        // sent.setContent(receivedMessage.getContent());
        sent.setSubmissionTimeStr(this.submissionTime);
        sent.setSubmissionTime(date);
        sent.setDate(dateFormatDay.format(date));
        sent.setAttachedFile(false);
        return sent;

    }

    private boolean validateDOF(String currentDate) {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, 30);
        String limitationDay = dateFormatDay.format(calendar.getTime());
        return currentDate.compareTo(limitationDay) <= 0;
    }

    // PRIVATE METHODS
    private boolean verifyIPNMessage(MSMessage message) {
        final Integer isIpn = MSUtil.getIntParam(message, X400_att.X400_N_IS_IPN);
        return !(isIpn == null || isIpn == 0);
    }

    private boolean verifyIPNMessage() {
        final Integer isIpn = MSUtil.getIntParam(this.message, X400_att.X400_N_IS_IPN);
        return !(isIpn == null || isIpn == 0);
    }

    
    private void parseProbe() throws X400APIException {

        // Parsing common message
        this.sequenceNumber = getIntParam(message, X400_att.X400_N_MS_SEQUENCE_NUMBER);
        this.priority = Priority.NORMAL;
        this.messageId = getStrParam(message, X400_att.X400_S_MESSAGE_IDENTIFIER);
        this.ipmId = getStrParam(message, X400_att.X400_S_IPM_IDENTIFIER);

        this.submissionTime = getStrParam(message, X400_att.X400_S_MESSAGE_SUBMISSION_TIME);
        this.deliveriedTime = getStrParam(message, X400_att.X400_S_MESSAGE_DELIVERY_TIME);
        this.origin = getStrParam(message, X400_att.X400_S_OR_ADDRESS);
        this.subject = getStrParam(message, X400_att.X400_S_SUBJECT);

        this.recipients = getRecipients(message, X400_att.X400_RECIP_PRIMARY);
        this.envelopeRecipients = getRecipients(message, X400_att.X400_RECIP_ENVELOPE);
        this.bccRecipient = getRecipients(message, X400_att.X400_RECIP_BCC);
        this.ccRecipients = getRecipients(message, X400_att.X400_RECIP_CC);
//        this.attachs = getMessageListAttachFile(this.attachments);
        // extractAttachment();

        if (!envelopeRecipients.isEmpty() && this.getType() == MessageType.STORED_MESSAGE) {
            String enRecipient = envelopeRecipients.get(0).getAddress();
            for (Recipient recip : this.recipients) {
                if (!recip.getAddress().equalsIgnoreCase(enRecipient)) {
                    continue;
                }
                this.setIpnRequest(recip.getNotificationRequest());
                break;
            }
        }
    }
    //----------------------------------------------------------------------
    //
    //
    //
    //----------------------------------------------------------------------
    private void parseIPM() throws X400APIException {
        
        
      //  this.isExtend = getIntParam(message, AMHS_att.ATS_N_EXTENDED);
        
        // Parsing common message
        this.sequenceNumber = getIntParam(message, X400_att.X400_N_MS_SEQUENCE_NUMBER);
        System.out.println("<ReceivedMessage1 660> Receiver message sequence <" + Integer.toString(this.sequenceNumber) + ">");
        //if(this.sequenceNumber == 3426) {
        //    sequenceNumber = 3426;
        //}
        this.priority = Priority.valueOf(getIntParam(message, X400_att.X400_N_PRIORITY));
        this.messageId = getStrParam(message, X400_att.X400_S_MESSAGE_IDENTIFIER);
        this.ipmId = getStrParam(message, X400_att.X400_S_IPM_IDENTIFIER);
        this.submissionTime = getStrParam(message, X400_att.X400_S_MESSAGE_SUBMISSION_TIME);
        this.deliveriedTime = getStrParam(message, X400_att.X400_S_MESSAGE_DELIVERY_TIME);
        this.origin = getStrParam(message, X400_att.X400_S_OR_ADDRESS);
        this.subject = getStrParam(message, X400_att.X400_S_SUBJECT);                           //
        this.recipients = getRecipients(message, X400_att.X400_RECIP_PRIMARY);
        this.envelopeRecipients = getRecipients(message, X400_att.X400_RECIP_ENVELOPE);
        this.bccRecipient = getRecipients(message, X400_att.X400_RECIP_BCC);
        this.ccRecipients = getRecipients(message, X400_att.X400_RECIP_CC);
        this.contentID = getStrParam(message, X400_att.X400_S_CONTENT_IDENTIFIER);
        this.oeit = getStrParam(message, X400_att.X400_S_ORIGINAL_ENCODED_INFORMATION_TYPES);

        ReceiveMsg rm = (ReceiveMsg)this.getMessage();
        if (rm.getStringParam(X400_att.X400_S_PRECEDENCE_POLICY_ID).equals("1.3.27.8.0.0")) {          // Extended
            this.isExtend = true;
            this.setAtsFilingTime(getStrParam(this.getMessage(), AMHS_att.ATS_S_FILING_TIME));
            this.setAtsPriority(getStrParam(this.getMessage(), AMHS_att.ATS_S_PRIORITY_INDICATOR));
            String ohi = getStrParam(this.getMessage(), AMHS_att.ATS_S_OPTIONAL_HEADING_INFO);                  // LEO
            if (ohi != null && !ohi.isEmpty()) {
                this.setAtsOHI(ohi);
            }
        } else {
            this.isExtend = false;
        }
         
        // Lay dien van ra
          System.err.println("<================extract_attachment_mode====================> " + Integer.toString(extract_attachment_mode));
        if(extract_attachment_mode == 1) {
            extractAttachment1();
        } else {
            extractAttachment();
        }
        
        // Nhan duoc IPN REQUESTs
        // 0
        // 2
        // 3s
        if (!envelopeRecipients.isEmpty()) {
            String enRecipient = envelopeRecipients.get(0).getAddress();
            for (Recipient recip : this.recipients) {
                 System.out.println("IPM detect Recipient NOTIFY : " + recip.getAddress() +  " Notification: " + recip.getNotificationRequest() + "  Report: " + recip.getReportRequest());

                if (!recip.getAddress().equalsIgnoreCase(enRecipient)) {
                    continue;
                }

                this.setIpnRequest(recip.getNotificationRequest());
                break;
            }
        }
    }

    //----------------------------------------------------------------------
    //
    //
    //
    //----------------------------------------------------------------------
    
    private void parseIPN() {

        this.sequenceNumber = getIntParam(message, X400_att.X400_N_MS_SEQUENCE_NUMBER);
        this.priority = Priority.valueOf(getIntParam(message, X400_att.X400_N_PRIORITY));
        this.messageId = getStrParam(message, X400_att.X400_S_MESSAGE_IDENTIFIER);
        this.subjectIpmId = getStrParam(message, X400_att.X400_S_SUBJECT_IPM);

        this.submissionTime = getStrParam(message, X400_att.X400_S_MESSAGE_SUBMISSION_TIME);
        this.deliveriedTime = getStrParam(message, X400_att.X400_S_MESSAGE_DELIVERY_TIME);
        this.origin = getStrParam(message, X400_att.X400_S_OR_ADDRESS);

        this.ipnReceiptTime = getStrParam(message, X400_att.X400_S_RECEIPT_TIME);
        this.envelopeRecipients = getRecipients(message, X400_att.X400_RECIP_ENVELOPE);
        if (!envelopeRecipients.isEmpty()) {
            this.setIpnRecipient(envelopeRecipients.get(0).getAddress());
        }
    }

    //----------------------------------------------------------------------
    //
    //
    //
    //----------------------------------------------------------------------
    private void parseReport() throws X400APIException {
        this.sequenceNumber = getIntParam(this.message, X400_att.X400_N_MS_SEQUENCE_NUMBER);
        this.subjectId = getStrParam(this.message, X400_att.X400_S_SUBJECT_IDENTIFIER);
        this.reportRecips = getReportRecipient(message);
    }

//    private void extractAttachmentX400HighLevel() throws X400APIException {
//        ReceiveMsg rm = new ReceiveMsg(p7BindSession, this.sequenceNumber);
//        int numOfBodyparts = rm.getNumberOfBodyparts();
//        for (int i = 1; i <= numOfBodyparts; i++) {
//            Bodypart bp = rm.getBodypart(i);
//            Attachment attachment;
//            if (bp instanceof BodypartIA5Text) {
//                BodypartIA5Text bpt = (BodypartIA5Text) bp;
//                final String content = bpt.getTextContent();//getStrParam(bodypart, X400_att.X400_S_BODY_DATA);
//                if (content != null && !content.isEmpty() && content.contains("\u0001")) {
//                    String[] lines = content.split("\u0002");
//                    if (lines.length > 1) {
//                        this.setContent(lines[1]);
//                    }
//
//                    String header = lines[0].replace("\u0001", "");
//                    String[] headers = header.split("\r\n");
//                    for (String h : headers) {
//                        if (h.startsWith("PRI:")) {
//                            this.setAtsPriority(h.split(":")[1].trim());
//                        }
//                        if (h.startsWith("OHI:")) {
//                            this.setAtsOHI(h.split(":")[1].trim());
//                        }
//                        if (h.startsWith("FT:")) {
//                            this.setAtsFilingTime(h.split(":")[1].trim());
//                        }
//                    }
//                } else {
//                    this.setContent(content);
//                    this.setAtsFilingTime(getStrParam(this.getMessage(), AMHS_att.ATS_S_FILING_TIME));
//                    this.setAtsPriority(getStrParam(this.getMessage(), AMHS_att.ATS_S_PRIORITY_INDICATOR));
//                    this.setAtsOHI(getStrParam(this.getMessage(), AMHS_att.ATS_S_OPTIONAL_HEADING_INFO));
//                }
//            } else if (bp instanceof BodypartGeneralText) {
//                BodypartGeneralText gtbp = (BodypartGeneralText) bp;
//                final String heading = gtbp.getStringRepresentation();//getStrParam(bodypart, X400_att.X400_S_BODY_DATA);
//                if (heading == null || heading.isEmpty()) {
//                    break;
//                }
//
//                String[] lines = heading.split("\u0002");
//                if (lines.length > 1) {
//                    this.setContent(lines[1]);
//                }
//
//                String header = lines[0].replace("\u0001", "");
//                String[] headers = header.split("\r\n");
//                for (String h : headers) {
//                    if (h.startsWith("PRI:")) {
//                        this.setAtsPriority(h.split(":")[1].trim());
//                    }
//                    if (h.startsWith("OHI:")) {
//                        this.setAtsOHI(h.split(":")[1].trim());
//                    }
//                    if (h.startsWith("FT:")) {
//                        this.setAtsFilingTime(h.split(":")[1].trim());
//                    }
//                }
//            } else if (bp instanceof BodypartFTBP) {
//                attachment = getStrParam(bp);
//                if (attachment == null) {
//                    return;
//                }
//                this.attachments.add(attachment);
//            } else if (bp.getType() == Bodypart.Bodypart_Type.BODYPART_BINARY) {
//                attachment = getStrParam(bp);
//                if (attachment == null) {
//                    return;
//                }
//                this.attachments.add(attachment);
//            } else if (bp.getType() == Bodypart.Bodypart_Type.BODYPART_MESSAGE) {
//                Message fwdMsg = new Message();
//                com.isode.x400api.X400ms.x400_ms_msggetmessagebody(rm, i, fwdMsg);
//                BodypartForwardedMessage fwd = new BodypartForwardedMessage(bp.getBodypartObject());
//                fwd.setFwdMessage(fwdMsg);
////                System.out.println(fwd.getStringRepresentation());
//                final String content = fwd.getStringRepresentation();
//                if (content != null && !content.isEmpty() && content.contains("\u0001")) {
//                    String[] lines = content.split("\u0002");
//                    if (lines.length > 1) {
//                        this.setContent(lines[1]);
//                    }
//
//                    String header = lines[0].replace("\u0001", "");
//                    String[] headers = header.split("\r\n");
//                    for (String h : headers) {
//                        if (h.startsWith("PRI:")) {
//                            this.setAtsPriority(h.split(":")[1].trim());
//                        }
//                        if (h.startsWith("OHI:")) {
//                            this.setAtsOHI(h.split(":")[1].trim());
//                        }
//                        if (h.startsWith("FT:")) {
//                            this.setAtsFilingTime(h.split(":")[1].trim());
//                        }
//                    }
//                } else {
//                    this.setContent(content);
//                    this.setAtsFilingTime(getStrParam(this.getMessage(), AMHS_att.ATS_S_FILING_TIME));
//                    this.setAtsPriority(getStrParam(this.getMessage(), AMHS_att.ATS_S_PRIORITY_INDICATOR));
//                    this.setAtsOHI(getStrParam(this.getMessage(), AMHS_att.ATS_S_OPTIONAL_HEADING_INFO));
//                }
//            }
//        }
////        }
//    }
    
    /*
    
    private MSMessage message;
    public class ReceiveMsg extends MSMessage
    
    */
    //NEW
    private void extractAttachment() throws X400APIException {
        BodyPart bodypart = new BodyPart();
        int index = 1;
        int stat = 0;
        String _content;

        // Check remove
        ReceiveMsg rm = (ReceiveMsg)this.getMessage();
        //extend_encode = rm.getIntParam(AMHS_att.ATS_N_EXTENDED);
        
                
        // Thử lấy thử từ 0 nếu OK tức là Number Of Body part > 1
        // Nêu no OK thì chỉ có 1 Number Of Body part , như điên văn IWXXM chỉ có 1 Body part
        /*
        stat = com.isode.x400api.X400ms.x400_ms_msggetbodypart(this.getMessage(), index, bodypart);
        if (stat != X400_att.X400_E_NOERROR  && stat != X400_att.X400_E_MESSAGE_BODY) {      //|| _code == X400_att.X400_E_MISSING_ATTR) {
            index = 1;
        }
        */
      
       /* 
       if(this.sequenceNumber ==12311) {
           System.out.println("DEBUD STOP");
       }
       
       if(this.sequenceNumber ==12272) {
           System.out.println("DEBUD STOP");
       }
       */
       
        //com.isode.x400api.X400ms.x400_ms

        //      com.isode.x400api MHS.
        /// MSMessage mess = new MSMessage();
        //public class ReceiveMsg extends MSMessage
       
       
        int numOfBodyparts = rm.getNumberOfBodyparts();
        this.numberOfBodyparts = numOfBodyparts;
        //String OEIT = this.oeit;
        
        /*
        if(numOfBodyparts > 1) {
            index = 0;
        } else if(numOfBodyparts == 1){
            index = 1;
        }
        */
        
//        //if(this.oeit.startsWith("ia5-text", 0) ||  this.oeit.startsWith("undefined", 0)) {
//        if(this.oeit.startsWith("ia5-text", 0) || this.oeit.startsWith("2.6.3.4.2" ) || this.oeit.startsWith("2.6.1.11.0" ) ) {
//            index = 0;
//        }
//        else  {
//            index = 1;
//        }
//        
//        
//          
//        /*
//        Note: For IA5 bodyparts, the number starts at 0. For General Text or FTBP the number starts at 1!
//        */
//        
//        /*
//        if(this.oeit.startsWith("ia5-text", 0) ) {
//            index = 0;
//        } else {
//            index = 1;          // General Text         
//        }
//        */
//        //
//        //if(this.oeit.startsWith("ia5-text 2.6.1.12.0", 0) ) {
//        //    index = 0;
//        //} 
//        
//        
//        if(this.oeit.startsWith("undefined", 0)) {
//            index = 1;
//        }
//        
//        //--- Lam sua ngay 15/02/2025 de thu 1 body, general text cua Cam
//        
//        if (numOfBodyparts ==1 ) {
//            stat = com.isode.x400api.X400ms.x400_ms_msggetbodypart(this.getMessage(), 0, bodypart);
//            Integer bodypartType1 = getIntParam(bodypart, X400_att.X400_N_BODY_TYPE);
//            if (stat ==0 && bodypartType1==401 ) {
//                if (index ==1) {
//                    index=0;
//                }
//            }
//            
//        }
//        
//        
//        
//        
//        else if (numOfBodyparts == 2 && this.oeit.startsWith("ia5-text 2.6.1.12.0", 0) && !this.isExtend ) {
//            stat = com.isode.x400api.X400ms.x400_ms_msggetbodypart(this.getMessage(), 0, bodypart);
//            stat = com.isode.x400api.X400ms.x400_ms_msggetbodypart(this.getMessage(), 1, bodypart);
//            Integer bodypartType1 = getIntParam(bodypart, X400_att.X400_N_BODY_TYPE);
//        // index = numOfBodyparts - 1;
//        // NEU co 1 BP se lay tu 1
//            index = 1;
//      
//        }
            // Tim xem index = 0 hay = 1 
            // doc 0 neu ok thi index = 0
            // neu loi
            // Doc
           
            // Thu doc de xem index la  hay 1
            index = 0;
            stat = com.isode.x400api.X400ms.x400_ms_msggetbodypart(this.getMessage(), 0, bodypart);
            if(stat == 0) {
                index=0;
            } else {
                stat = com.isode.x400api.X400ms.x400_ms_msggetbodypart(this.getMessage(), 1, bodypart);
                if(stat == 0) {
                    index=1;
                }
            }
        
        
        // index = 0 : 1    
        for (int i = 0; i < numOfBodyparts; i++, index++) {

            if (i > 0) {
                stat = com.isode.x400api.X400ms.x400_ms_msggetbodypart(this.getMessage(), index, bodypart);
                if (stat != X400_att.X400_E_NOERROR && stat != X400_att.X400_E_MESSAGE_BODY) {      //|| _code == X400_att.X400_E_MISSING_ATTR) {
                    break;
                }
            }

            //Bodypart bp = rm.getBodypart(index);
            //ftbp.getApplicationReferenceOID();
            final Integer bodypartType = getIntParam(bodypart, X400_att.X400_N_BODY_TYPE);

            Attachment attachment;

            switch (bodypartType) {
                case X400_att.X400_T_IA5TEXT:
                    /*
                   Bodypart bp; 
                    if(this.isExtend) {
                        bp = rm.getBodypart(1); 
                    } else {
                        bp = rm.getBodypart(numOfBodyparts); 
                    }
                     */
                    if (this.isExtend) {
                        Bodypart bp = rm.getBodypart(index + 1);
                        BodypartIA5Text bpt = null;
                        if (bp instanceof BodypartIA5Text) {
                            bpt = (BodypartIA5Text) bp;
                            System.out.println(bpt.getTextContent());
                        }
                        if (bpt != null) {
                            _content = bpt.getTextContent();
                        } else {
                            _content = getStrParam(bodypart, X400_att.X400_S_BODY_DATA);
                        }
                    } else {
                        if (numOfBodyparts == 1) {
                            Bodypart bp = rm.getBodypart(1);
                            BodypartIA5Text bpt = null;
                            if (bp instanceof BodypartIA5Text) {
                                bpt = (BodypartIA5Text) bp;
                                System.out.println(bpt.getTextContent());
                            }
                            if (bpt != null) {
                                _content = bpt.getTextContent();
                            } else {
                                _content = getStrParam(bodypart, X400_att.X400_S_BODY_DATA);
                            }
                        } else {
                            Bodypart bp = rm.getBodypart(2);
                            BodypartIA5Text bpt = null;
                            if (bp instanceof BodypartIA5Text) {
                                bpt = (BodypartIA5Text) bp;
                                System.out.println(bpt.getTextContent());
                            }
                            if (bpt != null) {
                                _content = bpt.getTextContent();
                            } else {
                                _content = getStrParam(bodypart, X400_att.X400_S_BODY_DATA);
                            }
                        }

                    }
                    //final String _content = bp.getStringParam(AMHS_att.ATS_S_TEXT);
                    //final String _content = "CHUA LAY DUOC CONTENT";

                    //BodypartIA5Text aaaa = (BodypartIA5Text) bodypart;
                    //System.out.println(bpt.getTextContent());
// DUC RAO                    
                    //getIntParam(this.getMessage(), AMHS_att.ATS_N_EXTENDED); 
                    //isExtend = x400msg.GetIntValue();
                    // BASIC ENCODE
                    if (!isExtend) {
                        if (_content != null && !_content.isEmpty() && _content.contains("\u0001")) {
                            String[] lines = _content.split("\u0002");
                            if (lines.length > 1) {
                                //this.setContent(lines[1]);
                                //String decode = _content.replace("\u0001", "<soh/>");
                                //decode = decode.replace("\u0002", "<stx/>");
                                //this.setContent(decode);
                                int stxIndex = _content.indexOf('\u0002'); // or (char) 0x02
                                if (stxIndex != -1) {
                                    String afterStx = _content.substring(stxIndex + 1); // Skip the STX char
                                    this.setContent(afterStx);
                                }
                            }

                            String header = lines[0].replace("\u0001", "");
                            String[] headers = header.split("\r\n");
                            for (String h : headers) {
                                if (h.startsWith("PRI:")) {
                                    this.setAtsPriority(h.split(":")[1].trim());
                                }
                                if (h.startsWith("OHI:")) {
                                    this.setAtsOHI(h.split(":")[1].trim());
                                }
                                if (h.startsWith("FT:")) {
                                    this.setAtsFilingTime(h.split(":")[1].trim());
                                }
                            }
                        }
                    } // EXTENDED ENCODE
                    else {
                        // this.setContent(getStrParam(bodypart, X400_att.X400_S_BODY_DATA));
                        // USE IHE
                        // 
                        this.setContent(_content);
                        this.setAtsFilingTime(getStrParam(this.getMessage(), AMHS_att.ATS_S_FILING_TIME));
                        this.setAtsPriority(getStrParam(this.getMessage(), AMHS_att.ATS_S_PRIORITY_INDICATOR));
                        String ohi = getStrParam(this.getMessage(), AMHS_att.ATS_S_OPTIONAL_HEADING_INFO);                  // LEO
                        if (ohi != null && !ohi.isEmpty()) {
                            this.setAtsOHI(ohi);
                        }
                    }

                    break;
                // DUC VIET THEM GET BINARY    
                case X400_att.X400_T_BINARY:
                    // High level
                    Bodypart bph = rm.getBodypart(1);
                    attachment = getStrParamBinary(bodypart, bph, X400_att.X400_S_BODY_DATA);
                    if (attachment == null) {
                        break;
                    }
                    this.attachments.add(attachment);
                    break;
                case X400_att.X400_T_FTBP:
                    attachment = getStrParam(bodypart);
                    if (attachment == null) {
                        break;
                    }
                    this.attachments.add(attachment);
                    break;
                case X400_att.X400_T_GENERAL_TEXT:
                    final String _content1 = getStrParam(bodypart, X400_att.X400_S_BODY_DATA);

                    if (_content1 != null && !_content1.isEmpty() && _content1.contains("\u0001")) {
                        String[] lines = _content1.split("\u0002");
                        if (lines.length > 1) {
                            this.setContent(lines[1]);
                        }

                        String header = lines[0].replace("\u0001", "");
                        String[] headers = header.split("\r\n");
                        for (String h : headers) {
                            if (h.startsWith("PRI:")) {
                                this.setAtsPriority(h.split(":")[1].trim());
                            }
                            if (h.startsWith("OHI:")) {
                                this.setAtsOHI(h.split(":")[1].trim());
                            }
                            if (h.startsWith("FT:")) {
                                this.setAtsFilingTime(h.split(":")[1].trim());
                            }
                        }
                    } else {
                        this.setContent(_content1);
                        this.setAtsFilingTime(getStrParam(this.getMessage(), AMHS_att.ATS_S_FILING_TIME));
                        // this.setAtsPriority(getStrParam(this.getMessage(), AMHS_att.ATS_S_PRIORITY_INDICATOR));
                        String ohi = getStrParam(this.getMessage(), AMHS_att.ATS_S_OPTIONAL_HEADING_INFO);                  // LEO
                        if (ohi != null && !ohi.isEmpty()) {
                            this.setAtsOHI(ohi);
                        }
                    }
                    /*
                    if (heading == null || heading.isEmpty()) {
                        break;
                    }

                    // if (isExtend == 0) {
                    String[] lines = heading.split("\u0002");
                    if (lines.length > 1) {
                        this.setContent(lines[1]);

                        String header = lines[0].replace("\u0001", "");
                        String[] headers = header.split("\r\n");
                        for (String h : headers) {
                            if (h.startsWith("PRI:")) {
                                this.setAtsPriority(h.split(":")[1].trim());
                            }
                            if (h.startsWith("OHI:")) {
                                this.setAtsOHI(h.split(":")[1].trim());
                            }
                            if (h.startsWith("FT:")) {
                                this.setAtsFilingTime(h.split(":")[1].trim());
                            }
                        }

                    } else {
                        this.setContent(heading);               // DUC THEM VAO
                    }
                     */
                    break;
                default:
                    break;
            }
        }
    }
    
    // OLD
    private void extractAttachment1() throws X400APIException {
        BodyPart bodypart = new BodyPart();
        int index = 1;
        int stat = 0;
        String _content;

        // Check remove
        ReceiveMsg rm = (ReceiveMsg)this.getMessage();
        //extend_encode = rm.getIntParam(AMHS_att.ATS_N_EXTENDED);
        
                
        // Thử lấy thử từ 0 nếu OK tức là Number Of Body part > 1
        // Nêu no OK thì chỉ có 1 Number Of Body part , như điên văn IWXXM chỉ có 1 Body part
        /*
        stat = com.isode.x400api.X400ms.x400_ms_msggetbodypart(this.getMessage(), index, bodypart);
        if (stat != X400_att.X400_E_NOERROR  && stat != X400_att.X400_E_MESSAGE_BODY) {      //|| _code == X400_att.X400_E_MISSING_ATTR) {
            index = 1;
        }
        */
      
        
       if(this.sequenceNumber ==12311) {
           System.out.println("DEBUD STOP");
       }
       
       if(this.sequenceNumber ==12272) {
           System.out.println("DEBUD STOP");
       }
        //com.isode.x400api.X400ms.x400_ms

        //      com.isode.x400api MHS.
        /// MSMessage mess = new MSMessage();
        //public class ReceiveMsg extends MSMessage
       
       
        int numOfBodyparts = rm.getNumberOfBodyparts();
        this.numberOfBodyparts = numOfBodyparts;
        String OEIT = this.oeit;
        
        /*
        if(numOfBodyparts > 1) {
            index = 0;
        } else if(numOfBodyparts == 1){
            index = 1;
        }
        */
        
        //if(this.oeit.startsWith("ia5-text", 0) ||  this.oeit.startsWith("undefined", 0)) {
        if(this.oeit.startsWith("ia5-text", 0) || this.oeit.startsWith("2.6.3.4.2" ) || this.oeit.startsWith("2.6.1.11.0" ) ) {
            index = 0;
        }
        else  {
            index = 1;
        }
        
        
          
        /*
        Note: For IA5 bodyparts, the number starts at 0. For General Text or FTBP the number starts at 1!
        */
        
        /*
        if(this.oeit.startsWith("ia5-text", 0) ) {
            index = 0;
        } else {
            index = 1;          // General Text         
        }
        */
        //
        //if(this.oeit.startsWith("ia5-text 2.6.1.12.0", 0) ) {
        //    index = 0;
        //} 
        
        
        if(this.oeit.startsWith("undefined", 0)) {
            index = 1;
        }
        
        //--- Lam sua ngay 15/02/2025 de thu 1 body, general text cua Cam
        
        if (numOfBodyparts ==1 ) {
            stat = com.isode.x400api.X400ms.x400_ms_msggetbodypart(this.getMessage(), 0, bodypart);
            Integer bodypartType1 = getIntParam(bodypart, X400_att.X400_N_BODY_TYPE);
            if (stat ==0 && bodypartType1==401 ) {
                if (index ==1) {
                    index=0;
                }
            }
            
        }
        
        
        
        
        else if (numOfBodyparts == 2 && this.oeit.startsWith("ia5-text 2.6.1.12.0", 0) && !this.isExtend ) {
            stat = com.isode.x400api.X400ms.x400_ms_msggetbodypart(this.getMessage(), 0, bodypart);
            stat = com.isode.x400api.X400ms.x400_ms_msggetbodypart(this.getMessage(), 1, bodypart);
            Integer bodypartType1 = getIntParam(bodypart, X400_att.X400_N_BODY_TYPE);
        // index = numOfBodyparts - 1;
        // NEU co 1 BP se lay tu 1
            index = 1;
      
        }
        
        
        
        // index = numOfBodyparts - 1;
        // NEU co 1 BP se lay tu 1
        //index = 0;
        for (;; index++) {
                
                stat = com.isode.x400api.X400ms.x400_ms_msggetbodypart(this.getMessage(), index, bodypart);
                if (stat != X400_att.X400_E_NOERROR  && stat != X400_att.X400_E_MESSAGE_BODY) {      //|| _code == X400_att.X400_E_MISSING_ATTR) {
                    break;
                }
            
            //Bodypart bp = rm.getBodypart(index);
           
                    
            //ftbp.getApplicationReferenceOID();
            
            final Integer bodypartType = getIntParam(bodypart, X400_att.X400_N_BODY_TYPE);
            
            Attachment attachment;
            
            switch (bodypartType) {
                case X400_att.X400_T_IA5TEXT :
                    /*
                   Bodypart bp; 
                    if(this.isExtend) {
                        bp = rm.getBodypart(1); 
                    } else {
                        bp = rm.getBodypart(numOfBodyparts); 
                    }
                    */
                    if (this.isExtend) {
                        Bodypart bp = rm.getBodypart(index + 1);;
                        BodypartIA5Text bpt = null;
                        if (bp instanceof BodypartIA5Text) {
                            bpt = (BodypartIA5Text) bp;
                            //System.out.println(bpt.getTextContent());
                        }
                        if (bpt != null) {
                            _content = bpt.getTextContent();
                        } else {
                            _content = getStrParam(bodypart, X400_att.X400_S_BODY_DATA);
                        }
                    } else {
                        if(numOfBodyparts==1) {
                        Bodypart bp = rm.getBodypart(1);;
                        BodypartIA5Text bpt = null;
                        if (bp instanceof BodypartIA5Text) {
                            bpt = (BodypartIA5Text) bp;
                            //System.out.println(bpt.getTextContent());
                        }
                        if (bpt != null) {
                            _content = bpt.getTextContent();
                        } else {
                            _content = getStrParam(bodypart, X400_att.X400_S_BODY_DATA);
                        }
                        } else {
                         Bodypart bp = rm.getBodypart(2);;
                        BodypartIA5Text bpt = null;
                        if (bp instanceof BodypartIA5Text) {
                            bpt = (BodypartIA5Text) bp;
                            //System.out.println(bpt.getTextContent());
                        }
                        if (bpt != null) {
                            _content = bpt.getTextContent();
                        } else {
                            _content = getStrParam(bodypart, X400_att.X400_S_BODY_DATA);
                        }   
                        }

                    }
                    //final String _content = bp.getStringParam(AMHS_att.ATS_S_TEXT);
                    //final String _content = "CHUA LAY DUOC CONTENT";
                    
                    
                    //BodypartIA5Text aaaa = (BodypartIA5Text) bodypart;
                    //System.out.println(bpt.getTextContent());
// DUC RAO                    
                    //getIntParam(this.getMessage(), AMHS_att.ATS_N_EXTENDED); 
                     //isExtend = x400msg.GetIntValue();

// DUC SUA NGAY 22/05/2025
// DETECT FORMAT DIEN VAN                    
                    // BASIC ENCODE
                    if (!isExtend) {
                        if (_content != null && !_content.isEmpty() && _content.contains("\u0001")) {
                            String[] lines = _content.split("\u0002");
                            if (lines.length > 1) {
                                //this.setContent(lines[1]);
                                //String decode = _content.replace("\u0001", "<sohsss/>");
                                //decode = decode.replace("\u0002", "<stx/>");
                                int stxIndex = _content.indexOf('\u0002'); // or (char) 0x02
                                if (stxIndex != -1) {
                                    String afterStx = _content.substring(stxIndex + 1); // Skip the STX char
                                    this.setContent(afterStx);
                                }
                            }

                            String header = lines[0].replace("\u0001", "");
                            String[] headers = header.split("\r\n");
                            for (String h : headers) {
                                if (h.startsWith("PRI:")) {
                                    this.setAtsPriority(h.split(":")[1].trim());
                                }
                                if (h.startsWith("OHI:")) {
                                    this.setAtsOHI(h.split(":")[1].trim());
                                }
                                if (h.startsWith("FT:")) {
                                    this.setAtsFilingTime(h.split(":")[1].trim());
                                }
                            }
                        }
                    }
                    // EXTENDED ENCODE
                    else {
                        // this.setContent(getStrParam(bodypart, X400_att.X400_S_BODY_DATA));
                        // USE IHE
                        // 
                        this.setContent(_content);
                        this.setAtsFilingTime(getStrParam(this.getMessage(), AMHS_att.ATS_S_FILING_TIME));
                        this.setAtsPriority(getStrParam(this.getMessage(), AMHS_att.ATS_S_PRIORITY_INDICATOR));
                        String ohi = getStrParam(this.getMessage(), AMHS_att.ATS_S_OPTIONAL_HEADING_INFO);                  // LEO
                        if (ohi != null && !ohi.isEmpty()) {
                            this.setAtsOHI(ohi);
                        }
                    }

                    break;
                // DUC VIET THEM GET BINARY    
                case X400_att.X400_T_BINARY:
                    // High level
                    Bodypart bph = rm.getBodypart(1);
                    attachment = getStrParamBinary(bodypart,bph, X400_att.X400_S_BODY_DATA);
                    if (attachment == null) {
                        break;
                    }
                    this.attachments.add(attachment);
                    break;
                case X400_att.X400_T_FTBP:
                    attachment = getStrParam(bodypart);
                    if (attachment == null) {
                        break;
                    }
                    this.attachments.add(attachment);
                    break;
                case X400_att.X400_T_GENERAL_TEXT:
                    final String _content1 = getStrParam(bodypart, X400_att.X400_S_BODY_DATA);
                    
                    
                    if (_content1 != null && !_content1.isEmpty() && _content1.contains("\u0001")) {
                        String[] lines = _content1.split("\u0002");
                        if (lines.length > 1) {
                            this.setContent(lines[1]);
                        }

                        String header = lines[0].replace("\u0001", "");
                        String[] headers = header.split("\r\n");
                        for (String h : headers) {
                            if (h.startsWith("PRI:")) {
                                this.setAtsPriority(h.split(":")[1].trim());
                            }
                            if (h.startsWith("OHI:")) {
                                this.setAtsOHI(h.split(":")[1].trim());
                            }
                            if (h.startsWith("FT:")) {
                                this.setAtsFilingTime(h.split(":")[1].trim());
                            }
                        }
                    }
                    else {
                        this.setContent(_content1);
                        this.setAtsFilingTime(getStrParam(this.getMessage(), AMHS_att.ATS_S_FILING_TIME));
                        // this.setAtsPriority(getStrParam(this.getMessage(), AMHS_att.ATS_S_PRIORITY_INDICATOR));
                        String ohi = getStrParam(this.getMessage(), AMHS_att.ATS_S_OPTIONAL_HEADING_INFO);                  // LEO
                        if (ohi != null && !ohi.isEmpty()) {
                            this.setAtsOHI(ohi);
                        }
                    }
                    /*
                    if (heading == null || heading.isEmpty()) {
                        break;
                    }

                    // if (isExtend == 0) {
                    String[] lines = heading.split("\u0002");
                    if (lines.length > 1) {
                        this.setContent(lines[1]);

                        String header = lines[0].replace("\u0001", "");
                        String[] headers = header.split("\r\n");
                        for (String h : headers) {
                            if (h.startsWith("PRI:")) {
                                this.setAtsPriority(h.split(":")[1].trim());
                            }
                            if (h.startsWith("OHI:")) {
                                this.setAtsOHI(h.split(":")[1].trim());
                            }
                            if (h.startsWith("FT:")) {
                                this.setAtsFilingTime(h.split(":")[1].trim());
                            }
                        }

                    } else {
                        this.setContent(heading);               // DUC THEM VAO
                    }
                        */
                    break;
                default:
                    break;
            }
        }
    }

    private List<ReportRecipient> getReportRecipient(MSMessage message) {
        int status;
        int num = 1;

        List<ReportRecipient> adds = new ArrayList<>();
        Recip recip = new Recip();

        for (num = 1;; num++) {
            status = com.isode.x400api.X400ms.x400_ms_recipget(message, X400_att.X400_RECIP_REPORT, num, recip);
            if (status == X400_att.X400_E_NO_RECIP) {
                break;
            }
            if (status != X400_att.X400_E_NOERROR) {
                break;
            }
            adds.add(new ReportRecipient(recip));
        }
        return adds;
    }

    private List<ReportDetail> getReportDetail(MSMessage message) {
        int status;
        int num = 1;
        List<ReportDetail> adds = new ArrayList<>();
        Recip recip = new Recip();
        for (num = 1;; num++) {
            status = com.isode.x400api.X400ms.x400_ms_recipget(message, X400_att.X400_RECIP_REPORT, num, recip);
            if (status == X400_att.X400_E_NO_RECIP) {
                break;
            }
            if (status != X400_att.X400_E_NOERROR) {
                break;
            }
            adds.add(new ReportRecipient(recip).getReportDetail());
        }
        return adds;
    }

    private List<Recipient> getRecipients(MSMessage message, int type) {
        int index;
        int _code;

        final List<Recipient> addresses = new ArrayList<>();

        Recip recip = new Recip();
        Recipient recipient;
        for (index = 1;; index++) {

            _code = com.isode.x400api.X400ms.x400_ms_recipget(message, type, index, recip);
            if (_code != X400_att.X400_E_NOERROR) {
                break;
            }
            recipient = new Recipient(recip);
            if (recipient.getAddress() == null) {
                break;
            }
            addresses.add(recipient);
        }
        return addresses;
    }

    private Integer getIntParam(BodyPart bodypart_obj, int attribute) {
        final int _code = X400.x400_bodypartgetintparam(bodypart_obj, attribute);
        if (_code != X400_att.X400_E_NOERROR) {
            //System.out.printf("Fail to get attribute %s from BodyPart Object %n", attribute);
            return null;
        }
        return bodypart_obj.GetIntValue();
    }

     private Attachment getStrParamBinary(BodyPart bodypart, Bodypart bph, int attribute) {
        StringBuffer value = new StringBuffer();
        this.filelength = bph.getSize();
        System.out.println("Bodypart size = " + filelength);
        //this.filelength = getIntParam(bodypart, X400_att.X400_N_FTBP_OBJECT_SIZE);
        byte[] bytes = new byte[filelength];
        
        int status = com.isode.x400api.X400.x400_bodypartgetstrparam(bodypart, attribute, value, bytes);
        if (status != X400_att.X400_E_NOERROR) {
            return null;
        }
        
        return new Attachment("BINARY", bytes, "", "");
    }
    
    private String getStrParam(BodyPart bodypart, int attribute) {
        StringBuffer value = new StringBuffer();
        byte[] bytes = new byte[32000];
        int status = com.isode.x400api.X400.x400_bodypartgetstrparam(bodypart, attribute, value, bytes);
        if (status != X400_att.X400_E_NOERROR) {
            return null;
        }

        return value.toString();
    }

    // Lay ten file và data dua vao CLASS
    private Attachment getStrParam(BodyPart bodypart) {
        
       // this.filelength = 10000;
        this.filename = getStrParam(bodypart, X400_att.X400_S_FTBP_FILENAME);
        this.filelength = getIntParam(bodypart, X400_att.X400_N_FTBP_OBJECT_SIZE);
        this.refOID = getStrParam(bodypart, X400_att.X400_S_FTBP_APPLICATION_REFERENCE_OID);
        this.ConTDesc = getStrParam(bodypart, X400_att.X400_S_FTBP_CONTENT_DESCRIPTION);
        this.modificaiondate = getStrParam(bodypart, X400_att.X400_S_FTBP_MODIFICATION_DATE);

        final StringBuffer value = new StringBuffer();
        final byte[] bytes = new byte[this.filelength];
        final int status = com.isode.x400api.X400.x400_bodypartgetstrparam(bodypart, X400_att.X400_S_BODY_DATA, value, bytes);
        if (status != X400_att.X400_E_NOERROR) {
            return null;
        }
        return new Attachment(this.filename, bytes, this.refOID, this.ConTDesc);
    }

//    private Attachment getStrParam(Bodypart bp) throws X400APIException {
//        BodypartFTBP ftbp = (BodypartFTBP) bp;
//        System.out.println(ftbp.getStringRepresentation());
//        String fileName = ftbp.getFileName();;
//        Integer length = ftbp.getSize();
//        byte[] bytes = new byte[length];
////            fileName = ftbp.getStringRepresentation(); 
//        bytes = ftbp.getBodyData();
//        if (fileName == null || bytes == null) {
//            return null;
//        }
//        String refOID = ftbp.getApplicationReferenceOID();
//        String ContDesc = ftbp.getContentDescription();
//        
//        return new Attachment(fileName, bytes,refOID,ContDesc);
//    }

    private Integer getIntParam(MSMessage ms, int attribute) {
        final int stt = X400ms.x400_ms_msggetintparam(ms, attribute);
        if (stt != X400_att.X400_E_NOERROR) {
            System.out.println(String.format("DUC 7 Fail to get int attribute from msmessage object. (Attribute: %s, Code: %s)", attribute, stt));
            return null;
        }
        return ms.GetIntValue();
    }

    private String getStrParam(MSMessage ms, int attribute) {
        StringBuffer value = new StringBuffer();
        final int stt = X400ms.x400_ms_msggetstrparam(ms, attribute, value);
        if (stt != X400_att.X400_E_NOERROR) {
            return null;
        }
        return value.toString();
    }

    /* PROPERTIES */
    //<editor-fold defaultstate="collapsed" desc="Class properties">
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
     * @return the subjectIpmId
     */
    public String getSubjectIpmId() {
        return subjectIpmId;
    }

    /**
     * @param subjectIpmId the subjectIpmId to set
     */
    public void setSubjectIpmId(String subjectIpmId) {
        this.subjectIpmId = subjectIpmId;
    }

    /**
     * @return the subjectId
     */
    public String getSubjectId() {
        return subjectId;
    }

    /**
     * @param subjectId the subjectId to set
     */
    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    /**
     * @return the ipnRequest
     */
    public Integer getIpnRequest() {
        return ipnRequest;
    }

    /**
     * @param ipnRequest the ipnRequest to set
     */
    public void setIpnRequest(Integer ipnRequest) {
        this.ipnRequest = ipnRequest;
    }

    /**
     * @return the priority
     */
    public Priority getPriority() {
        return priority;
    }

    /**
     * @param priority the priority to set
     */
    public void setPriority(Priority priority) {
        this.priority = priority;
    }

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
     * @return the atsPriority
     */
    public String getAtsPriority() {
        return atsPriority;
    }

    /**
     * @param atsPriority the atsPriority to set
     */
    public void setAtsPriority(String atsPriority) {
        this.atsPriority = atsPriority;
    }

    /**
     * @return the atsFilingTime
     */
    public String getAtsFilingTime() {
        return atsFilingTime;
    }

    /**
     * @param atsFilingTime the atsFilingTime to set
     */
    public void setAtsFilingTime(String atsFilingTime) {
        this.atsFilingTime = atsFilingTime;
    }

    /**
     * @return the atsOHI
     */
    public String getAtsOHI() {
        return atsOHI;
    }

    /**
     * @param atsOHI the atsOHI to set
     */
    public void setAtsOHI(String atsOHI) {
        this.atsOHI = atsOHI;
    }

    /**
     * @return the atsExtention
     */
    public Integer getAtsExtention() {
        return atsExtention;
    }

    /**
     * @param atsExtention the atsExtention to set
     */
    public void setAtsExtention(Integer atsExtention) {
        this.atsExtention = atsExtention;
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
     * @return the submissionTime
     */
    public String getSubmissionTime() {
        return submissionTime;
    }

    /**
     * @param submissionTime the submissionTime to set
     */
    public void setSubmissionTime(String submissionTime) {
        this.submissionTime = submissionTime;
    }

    /**
     * @return the deliveriedTime
     */
    public String getDeliveriedTime() {
        return deliveriedTime;
    }

    /**
     * @param deliveriedTime the deliveriedTime to set
     */
    public void setDeliveriedTime(String deliveriedTime) {
        this.deliveriedTime = deliveriedTime;
    }

    /**
     * @return the ipnRecipient
     */
    public String getIpnRecipient() {
        return ipnRecipient;
    }

    /**
     * @param ipnRecipient the ipnRecipient to set
     */
    public void setIpnRecipient(String ipnRecipient) {
        this.ipnRecipient = ipnRecipient;
    }

    /**
     * @return the ipnReceiptTime
     */
    public String getIpnReceiptTime() {
        return ipnReceiptTime;
    }

    /**
     * @param ipnReceiptTime the ipnReceiptTime to set
     */
    public void setIpnReceiptTime(String ipnReceiptTime) {
        this.ipnReceiptTime = ipnReceiptTime;
    }

    /**
     * @return the type
     */
    public MessageType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(MessageType type) {
        this.type = type;
    }

    /**
     * @return the isIPN
     */
    public Boolean getIsIPN() {
        return isIPN;
    }

    /**
     * @param isIPN the isIPN to set
     */
    public void setIsIPN(Boolean isIPN) {
        this.isIPN = isIPN;
    }

    /**
     * @return the attachments
     */
    public List<Attachment> getAttachments() {
        return attachments;
    }

    /**
     * @param attachments the attachments to set
     */
    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    /**
     * @return the reportRecips
     */
    public List<ReportRecipient> getReportRecips() {
        return reportRecips;
    }

    /**
     * @param reportRecips the reportRecips to set
     */
    public void setReportRecips(List<ReportRecipient> reportRecips) {
        this.reportRecips = reportRecips;
    }

    /**
     * @return the recipients
     */
    public List<Recipient> getRecipients() {
        return recipients;
    }

    /**
     * @param recipients the recipients to set
     */
    public void setRecipients(List<Recipient> recipients) {
        this.recipients = recipients;
    }

    /**
     * @return the ccRecipients
     */
    public List<Recipient> getCcRecipients() {
        return ccRecipients;
    }

    /**
     * @param ccRecipients the ccRecipients to set
     */
    public void setCcRecipients(List<Recipient> ccRecipients) {
        this.ccRecipients = ccRecipients;
    }

    /**
     * @return the bccRecipient
     */
    public List<Recipient> getBccRecipient() {
        return bccRecipient;
    }

    /**
     * @param bccRecipient the bccRecipient to set
     */
    public void setBccRecipient(List<Recipient> bccRecipient) {
        this.bccRecipient = bccRecipient;
    }

    /**
     * @return the envelopeRecipients
     */
    public List<Recipient> getEnvelopeRecipients() {
        return envelopeRecipients;
    }

    /**
     * @param envelopeRecipients the envelopeRecipients to set
     */
    public void setEnvelopeRecipients(List<Recipient> envelopeRecipients) {
        this.envelopeRecipients = envelopeRecipients;
    }

    /**
     * @return the _code
     */
    public Integer getCode() {
        return code;
    }

    /**
     * @param code the _code to set
     */
    public void setCode(Integer code) {
        this.code = code;
    }

    /**
     * @return the message
     */
    public MSMessage getMessage() {
        return message;
    }

    /**
     * @return the errorMessage
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * @param errorMessage the errorMessage to set
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * @return the contentID
     */
    public String getContentID() {
        return contentID;
    }

    /**
     * @param contentID the contentID to set
     */
    public void setContentID(String contentID) {
        this.contentID = contentID;
    }

//
//    public List<Attach> getAttachs() {
//        return attachs;
//    }
//
//    public void setAttachs(List<Attach> attachs) {
//        this.attachs = attachs;
//    }
    //</editor-fold>

    /**
     * @return the modificaiondate
     */
    public String getModificaiondate() {
        return modificaiondate;
    }

    /**
     * @param modificaiondate the modificaiondate to set
     */
    public void setModificaiondate(String modificaiondate) {
        this.modificaiondate = modificaiondate;
    }
}
