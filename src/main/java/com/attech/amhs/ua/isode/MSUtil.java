/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.attech.amhs.ua.isode;

import com.isode.x400.highlevel.BodypartFTBP;
import com.isode.x400.highlevel.X400APIException;
import com.isode.x400api.BodyPart;
import com.isode.x400api.MSMessage;
import com.isode.x400api.Recip;
import com.isode.x400api.X400;
import com.isode.x400api.X400_att;
import com.isode.x400api.X400ms;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author andh
 */
public class MSUtil {

    // private final static MLogger logger = MLogger.getLogger(MSUtil.class);
    private static final Logger logger = LoggerFactory.getLogger(MSUtil.class);
    private final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd.HHmmss");
    private final static SimpleDateFormat ipmDateFormat = new SimpleDateFormat("yyMMddHHmmssSSS");
    private final static SimpleDateFormat filingTime = new SimpleDateFormat("ddHHmm");
    private final static SimpleDateFormat arrivalTimeFormat = new SimpleDateFormat("yyMMddHHmmss");

    public static void addRecip(MSMessage msMessage, int type, int no, Recipient address) throws X400APIException {
        Recip recip_obj = new Recip();
        int status = com.isode.x400api.X400ms.x400_ms_recipnew(msMessage, type, recip_obj);
        if (status != X400_att.X400_E_NOERROR) {
            throw new X400APIException("Cannot add recipient");
        }

        MSUtil.setStrParam(recip_obj, X400_att.X400_S_OR_ADDRESS, address.getAddress(), -1);
        MSUtil.setIntParam(recip_obj, X400_att.X400_N_ORIGINAL_RECIPIENT_NUMBER, no);
        // X400_N_MTA_REPORT_REQUEST
        // X400_N_REPORT_REQUEST
        // X400_N_ORIGINAL_RECIPIENT_NUMBER
        // X400_N_NOTIFICATION_REQUEST
    }

    public static void addRecip(MSMessage msMessage, int type, int no, DeliveryAddress address) throws X400APIException {
        Recip recip_obj = new Recip();
        int status = com.isode.x400api.X400ms.x400_ms_recipnew(msMessage, type, recip_obj);
        if (status != X400_att.X400_E_NOERROR) {
            throw new X400APIException("Cannot add recipient");
        }

        MSUtil.setStrParam(recip_obj, X400_att.X400_S_OR_ADDRESS, address.getAddress(), -1);
        MSUtil.setIntParam(recip_obj, X400_att.X400_N_ORIGINAL_RECIPIENT_NUMBER, no);
        MSUtil.setIntParam(recip_obj, X400_att.X400_N_MTA_REPORT_REQUEST, 3);
        // MSUtil.setIntParam(msMessage, X400_att.X400_N_REPORT_REQUEST, address.getReportRequest());
        MSUtil.setIntParam(recip_obj, X400_att.X400_N_REPORT_REQUEST, 2);
        // MSUtil.setIntParam(msMessage, X400_att.X400_N_NOTIFICATION_REQUEST, address.getNotificationRequest());
        MSUtil.setIntParam(recip_obj, X400_att.X400_N_NOTIFICATION_REQUEST, 7);
        // X400_N_MTA_REPORT_REQUEST
        // X400_N_REPORT_REQUEST
        // X400_N_ORIGINAL_RECIPIENT_NUMBER
        // X400_N_NOTIFICATION_REQUEST
    }

    // MSMessage 's common function
    public static Integer getIntParam(MSMessage ms, int attribute) {
        final int stt = X400ms.x400_ms_msggetintparam(ms, attribute);
        if (stt != X400_att.X400_E_NOERROR) {
            // System.out.println(String.format("Fail to get int attribute from msmessage object. (Attribute: %s, Code: %s)", attribute, stt));
            return null;
        }
        return ms.GetIntValue();
    }

    public static Boolean getBoolParam(MSMessage ms, int attribute) {
        final int stt = X400ms.x400_ms_msggetintparam(ms, attribute);
        if (stt != X400_att.X400_E_NOERROR) {
            //System.out.println(String.format("Fail to get int attribute from msmessage object. (Attribute: %s, Code: %s)", attribute, stt));
            return null;
        }
        return ms.GetIntValue() != 0;
    }

    public static String getStrParam(MSMessage ms, int attribute) {
        StringBuffer value = new StringBuffer();
        final int stt = X400ms.x400_ms_msggetstrparam(ms, attribute, value);
        if (stt != X400_att.X400_E_NOERROR) {
            //System.out.println(String.format("Fail to get int attribute from msmessage object. (Attribute: %s, Code: %s)", attribute, stt));
            return null;
        }
        return value.toString();
    }

    public static void setIntParam(MSMessage ms, int attribute, Integer value) {
        if (value == null) {
            return;
        }
        final int status = com.isode.x400api.X400ms.x400_ms_msgaddintparam(ms, attribute, value);
        if (status != X400_att.X400_E_NOERROR) {
            // System.out.println("x400_ms_msgaddintparam failed " + status);
        }
    }
    
    public static void setAttachFile(MSMessage ms, String path) throws X400APIException {
        int status;
        BodypartFTBP ftbp = new BodypartFTBP(path);
//        BodypartFTBP ftbp = new BodypartFTBP("D:\\Data\\DE_TAI\\2018\\PhanTichNguonLucNCPT_29 11 2018.xlsx");
        BodyPart bp = new BodyPart();
        bp = ftbp.getBodypartObject();
        status = com.isode.x400api.X400ms.x400_ms_msgaddbodypart(ms, bp);
        if (status != X400_att.X400_E_NOERROR) {
            System.out.println("x400_msgaddbodypart failed " + status);
        }
    }
    
    public static void setAttachFile(MSMessage ms, String name, byte [] bytes) throws X400APIException {
        BodypartFTBP ftbp = new BodypartFTBP(name, bytes);
        BodyPart bp  = ftbp.getBodypartObject();
        int status = com.isode.x400api.X400ms.x400_ms_msgaddbodypart(ms, bp);
        if (status != X400_att.X400_E_NOERROR) {
            throw new X400APIException(String.format("Không đính kèm được file \"%s\"", name), status);
        }
    }
    
    public static void setStrParam(MSMessage ms, int attribute, String value) {
        if (value == null) {
            return;
        }
        final int status = com.isode.x400api.X400ms.x400_ms_msgaddstrparam(ms, attribute, value, value.length());
        if (status != X400_att.X400_E_NOERROR) {
            System.out.println("x400_ms_msgaddintparam failed " + status + " attribute: " + attribute);
            // throw new X400APIException("U");
        }
    }

    public static void setStrParam(MSMessage ms, int attribute, String value, int flag) {
        if (value == null) {
            return;
        }
        final int status = com.isode.x400api.X400ms.x400_ms_msgaddstrparam(ms, attribute, value, flag);
        if (status != X400_att.X400_E_NOERROR) {
            //System.out.println("x400_ms_msgaddintparam failed " + status);
        }
    }

    // Recipient 's common function
    public static Integer getIntParam(Recip recip, int attribute) throws X400APIException {

        final int code = com.isode.x400api.X400ms.x400_ms_recipgetintparam(recip, attribute);
        if (code != X400_att.X400_E_NOERROR) {
            //System.out.printf("Fail to get attribute %s from Recipient Object %n", attribute);
            return null;
        }
        return recip.GetIntValue();

    }

    public static String getStrParam(Recip recip, int attribute) throws X400APIException {
        final StringBuffer buffer = new StringBuffer();
        final int code = com.isode.x400api.X400ms.x400_ms_recipgetstrparam(recip, attribute, buffer);
        if (code != X400_att.X400_E_NOERROR) {
            throw new X400APIException("Fail to get attribute %s from Recipient Object " + attribute);
            // System.out.printf("Fail to get attribute %s from Recipient Object %n", attribute);
            // return null;
        }
        return buffer.toString();
    }

    // Bodypart 's common function
    public static String getStrParam(BodyPart bodypart, int att) {
        StringBuffer value = new StringBuffer();
        byte[] bytes = new byte[32000];
        int status = com.isode.x400api.X400.x400_bodypartgetstrparam(bodypart, att, value, bytes);
        if (status != X400_att.X400_E_NOERROR) {
            //System.out.println(" Getting body part content has some trouble [ code" + status + "]");
            return null;
        }

        return value.toString();
    }

    public static Integer getIntParam(BodyPart bodypart_obj, int attribute) {
        final int code = X400.x400_bodypartgetintparam(bodypart_obj, attribute);
        if (code != X400_att.X400_E_NOERROR) {
            //System.out.printf("Fail to get attribute %s from BodyPart Object %n", attribute);
            return null;
        }
        return bodypart_obj.GetIntValue();
    }

    public static List<Recipient> getRecipients(MSMessage msmessage_obj, int type) throws X400APIException {
        int recip_num;
        int code;
        Recip recip_obj = new Recip();
        List<Recipient> addresses = new ArrayList<>();
        for (recip_num = 1;; recip_num++) {
            // get the recip
            code = com.isode.x400api.X400ms.x400_ms_recipget(msmessage_obj, type, recip_num, recip_obj);
            if (code == X400_att.X400_E_NO_RECIP) {
                //System.out.println("no more recips ...");
                break;
            }

            if (code != X400_att.X400_E_NOERROR) {
                throw new X400APIException("Get recipients fail with code " + code);
            }

            String displayName = MSUtil.getStrParam(recip_obj, X400_att.X400_S_OR_ADDRESS);
            //System.out.printf("ADD: %s%n", displayName);

            Integer notification = MSUtil.getIntParam(recip_obj, X400_att.X400_N_NOTIFICATION_REQUEST);
            //System.out.printf("NOTIFICATION: %s%n", notification);

            Recipient address = new Recipient();
            address.setNotificationRequest(notification);
            addresses.add(address);
        }
        return addresses;
    }

    // REPORT RECIPIENTS
    public static String getStr(Recip recipObj, int att) {
        StringBuffer value = new StringBuffer();
        int status = X400ms.x400_ms_recipgetstrparam(recipObj, att, value);

        if (status != X400_att.X400_E_NOERROR) {
            //System.out.println(" Getting attribute trouble [ code" + status + "]");
            return null;
        }
        return value.toString();
    }

    public static Integer getInt(Recip recipObj, int att) {
        int status = X400ms.x400_ms_recipgetintparam(recipObj, att);
        if (status != X400_att.X400_E_NOERROR) {
            //System.out.println(" Getting attribute trouble [ code" + status + "]");
            return null;
        }
        return recipObj.GetIntValue();
    }

    public static void setIntParam(Recip recipObj, int att, Integer value) {
        if (value == null) {
            return;
        }
        final int status = com.isode.x400api.X400ms.x400_ms_recipaddintparam(recipObj, att, value);
        if (status != X400_att.X400_E_NOERROR) {
            // System.out.println("x400_ms_recipaddstrparam failed " + status);
            logger.debug("Set param {} error ({})", att, status);
        }
    }

    public static void setStrParam(Recip recipObj, int att, String value) {
        if (value == null) {
            return;
        }
        final int status = com.isode.x400api.X400ms.x400_ms_recipaddstrparam(recipObj, att, value, value.length());
        if (status != X400_att.X400_E_NOERROR) {
            ///System.out.println("x400_ms_recipaddstrparam failed " + status);
        }
    }

    public static void setStrParam(Recip recipObj, int att, String value, int mode) {
        if (value == null) {
            return;
        }
        final int status = com.isode.x400api.X400ms.x400_ms_recipaddstrparam(recipObj, att, value, mode);
        if (status != X400_att.X400_E_NOERROR) {
            //System.out.println("x400_ms_recipaddstrparam failed " + status);
            logger.debug("Set param {} error ({})", att, status);
        }
    }

    // GENERATE INFO
    public static String generateMessageId() {
        String hostname = "/PRMD=VIETNAM/ADMD=ICAO/C=XX/";
        String domain = "amhs.attech";
        Date date = new Date();
        int n = (int) (date.getTime() % 1000);
        return String.format("[%s;%s.%s-%s]", domain, hostname, n, dateFormat.format(date));

    }

    public static String generateIpmId(String originator) {
        String time = ipmDateFormat.format(new Date());
        String ipmFormat = "1 %sZ*%s";
        return String.format(ipmFormat, time, originator);
    }

    public static String generateFilingTime(Date date) {
        return filingTime.format(date);
    }

    public static String generateFilingTime() {
        return generateFilingTime(new Date());
    }

    public static String generateArrivalTime() {
        return arrivalTimeFormat.format(new Date()) + "Z";
    }

    public static String getErrorString(int code) {

        switch (code) {
            case 0:
                return "X400_E_NOERROR";
            case 1:
                return "X400_E_SYSERROR";
            case 2:
                return "X400_E_NOMEMORY";
            case 3:
                return "X400_E_BADPARAM";
            case 4:
                return "X400_E_INT_ERROR";
            case 5:
                return "X400_E_CONFIG_ERROR";
            case 6:
                return "X400_E_NYI";
            case 7:
                return "X400_E_NO_CHANNEL";
            case 8:
                return "X400_E_INV_MSG";
            case 10:
                return "X400_E_NOCONNECT";
            case 11:
                return "X400_E_BADCREDENTIALS";
            case 12:
                return "X400_E_CONNECT_REJ";
            case 13:
                return "X400_E_QMGR_CONGESTED";
            case 14:
                return "X400_E_BAD_QMGR_RESP";
            case 20:
                return "X400_E_MISSING_ATTR";
            case 21:
                return "X400_E_CONFLICT_ATTR";
            case 22:
                return "X400_E_INVALID_ATTR";
            case 23:
                return "X400_E_INVALID_VALUE";
            case 24:
                return "X400_E_NO_VALUE";
            case 30:
                return "X400_E_NO_MESSAGE";
            case 31:
                return "X400_E_TIMED_OUT";
            case 32:
                return "X400_E_NO_RECIP";
            case 33:
                return "X400_E_NOSPACE";
            case 64:
                return "X400_E_ADDRESS_ERROR";
            case 65:
                return "X400_E_BUFFER_FILE_ERROR";
            case 66:
                return "X400_E_BUFFER_FILENAME_ERROR";
            case 67:
                return "X400_E_BUFFER_TYPE_ERROR";
            case 68:
                return "X400_E_CONTENT_ERROR";
            case 69:
                return "X400_E_CONTENT_DATA_ERROR";
            case 70:
                return "X400_E_CONTENT_INIT_ERROR";
            case 71:
                return "X400_E_CONTENT_BODY_ERROR";
            case 72:
                return "X400_E_INIT_ERROR";
            case 73:
                return "X400_E_MESSAGE_ERROR";
            case 74:
                return "X400_E_NO_CONTENT";
            case 75:
                return "X400_E_ORIGINATOR_ERROR";
            case 76:
                return "X400_E_PARMS_ERROR";
            case 77:
                return "X400_E_RECIPIENT_ERROR";
            case 78:
                return "X400_E_TRANSFERABLE_ERROR";
            case 79:
                return "X400_E_REPORT_ERROR";
            case 80:
                return "X400_E_COMPLEX_BODY";
            case 81:
                return "X400_E_MESSAGE_BODY";
            case 82:
                return "X400_E_UNSUPPORTED_BODY";
            case 83:
                return "X400_E_X509_ENV";
            case 84:
                return "X400_E_X509_INTERNAL_ERROR";
            case 85:
                return "X400_E_X509_INIT";
            case 87:
                return "X400_E_X509_VERIFY_FAIL_NO_CERT";
            case 88:
                return "X400_E_X509_VERIFY_FAIL_NO_PUBKEY";
            case 89:
                return "X400_E_X509_VERIFY_FAIL_INCOMPAT_ALG";
            case 90:
                return "X400_E_X509_VERIFY_FAIL_UNSUPPORTED_ALG";
            case 91:
                return "X400_E_X509_VERIFY_FAIL";
            case 92:
                return "X400_E_X509_CERT_INVALID";
            case 93:
                return "X400_E_X509_ITEM_INVALID";
            case 94:
                return "X400_E_SIGN_NO_IDENTITY";
            case 95:
                return "X400_E_SIGN";
            case 96:
                return "X400_E_NONDELIVERED";
            case 99:
                return "X400_E_NO_MORE_RESULTS";
            case 100:
                return "X400_E_WAIT_WRITE";
            case 101:
                return "X400_E_WAIT_READ";
            case 102:
                return "X400_E_WAIT_READ_WRITE";
            case 110:
                return "X400_E_CONNECTION_LOST";
            case 111:
                return "X400_E_SHUTDOWN";
            case 112:
                return "X400_E_NO_MATCH";
            case 113:
                return "X400_E_S4406_TRIPLE_WRAPPED";
            case 114:
                return "X400_E_P7_ATTRIBUTE_ERROR";
            case 115:
                return "X400_E_P7_AUTOACTION_REQUEST_ERROR";
            case 116:
                return "X400_E_P7_DELETE_ERROR";
            case 117:
                return "X400_E_P7_INVALID_PARAMETERS_ERROR";
            case 118:
                return "X400_E_P7_RANGE_ERROR";
            case 119:
                return "X400_E_P7_SERVICE_ERROR";
            case 120:
                return "X400_E_P7_SUBMISSION_CONTROL_VIOLATED";
            case 121:
                return "X400_E_P7_ELEMENT_OF_SERVICE_NOT_SUBSCRIBED";
            case 122:
                return "X400_E_P7_MESSAGE_SUBMISSION_IDENTIFIER_INVALID";
            case 123:
                return "X400_E_P7_INCONSISTENT_REQUEST_ERROR";
            case 124:
                return "X400_E_P7_UNSUPPORTED_CRITICAL_FUNCTION";
            case 125:
                return "X400_E_P7_REMOTE_BIND_ERROR";
            case 126:
                return "X400_E_P7_FETCH_RESTRICTION_ERROR";
            case 127:
                return "X400_E_P7_SECURITY_ERROR";
            default :
                return "UNKNOWN";
        }

    }
}
