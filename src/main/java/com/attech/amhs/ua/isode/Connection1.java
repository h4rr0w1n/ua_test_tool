/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.attech.amhs.ua.isode;

import com.attech.amhs.ua.isode.enums.ConnectionType;
import com.attech.amhs.ua.isode.enums.ConnectionStatus;
import com.attech.amhs.ua.common.MString;
import com.attech.amhs.ua.common.enums.MsgClass;
import com.isode.x400.highlevel.P3BindSession;
import com.isode.x400.highlevel.P7BindSession;
import com.isode.x400.highlevel.ReceiveMsg;
import com.isode.x400.highlevel.X400APIException;
import com.isode.x400.highlevel.X400Msg;
import com.isode.x400api.MSListResult;
import com.isode.x400api.MSMessage;
import com.isode.x400api.X400_att;
import com.isode.x400api.X400ms;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ANDH
 */
public class Connection1 {

    // private MLogger logger = MLogger.getLogger(Connection1.class);
    private Logger logger = LoggerFactory.getLogger(Connection1.class);

    public static int MSG_TYPE_STORED_MESSAGE = 0;
    public static int MSG_TYPE_SUBMITTED_MESSAGE = 1;
    public static int MSG_STATUS_NEW = 0;
    public static int MSG_STATUS_LISTED = 1;
    public static int MSG_STATUS_FETCH = 2;
    public static int MSG_STATUS_ANY = -1;

    private final DateFormat filingTimeFormat = new SimpleDateFormat("ddHHmm");
    private P7BindSession session;

    private String presentationAddress;
    private String user;
    private String password;
    private int autoAlertId = 0;
    private int waitingTimeout = 0;

    private ConnectionType type;
    private ConnectionStatus status;
//    private boolean isWaitingForNewMessage;
    private ConnectionStatusChangedEventHandler conStatusChangedHandler;

    // CONSTRUCTORS
    public Connection1() {
    }

    // PUBLIC METHODS
    public boolean isConnected() {
        return (session != null && ((P3BindSession) this.session).isBound());
    }

    //---------------------------------------------------------------
    //
    //
    //
    //---------------------------------------------------------------
    public Boolean connect() throws X400APIException {

        if (this.isConnected()) {
            return false;
        }

        // Ensure native Isode DLLs are self-contained and pre-loaded before binding
        NativeLibInitializer.initialize();

        // this.onStatusChanged(ConnectionStatus.Connecting);
        session = new P7BindSession(presentationAddress, user, password);
        session.SetSummarizeOnBind(false);
        session.bind();

        registerAutoAlert(this.autoAlertId);
        logger.debug("Connected");
        return true;

//        logger.debug("Refresh Number :" + session.getRefreshNumberOfMessages());
//        logger.debug("Number :" + session.GetNumMsgs());
    }
    //---------------------------------------------------------------
    //
    //
    //
    //---------------------------------------------------------------
    public void disconnect() {

        if (this.session != null && this.session.isBound()) {
            try {
                ((P3BindSession) session).unbind();
                logger.debug("Disconnected");
            } catch (X400APIException ex) {
                logger.error("Disconnected error ({})", ex.getNativeErrorString());
            }
        }
    }

    //---------------------------------------------------------------
    //
    //
    //
    //---------------------------------------------------------------
    protected void registerAutoAlert(int id) throws X400APIException {
        System.out.println("register autoaction ID=" + Integer.toString(id));
        int code = X400ms.x400_ms_msregisterautoaction(session, X400_att.X400_AUTO_ALERT, id);
        if (code != X400_att.X400_E_NOERROR) {
            throw new X400APIException("Set auto-aciton fail", code);
        }
    }
    
    //---------------------------------------------------------------
    //
    //
    //
    //---------------------------------------------------------------
    /*
        public int wait4NewMessage() throws X400APIException {
//        X400ms.x400_ms_waitnew(session, autoAlertId)
        logger.debug("Waiting new message ({})", this.waitingTimeout);
        System.out.println("Waiting new message " + Integer.toString(this.waitingTimeout));
        X400ms.x400_ms_enablewait();
        return this.session.waitForNewMessages(this.waitingTimeout);
    }
    */
    //---------------------------------------------------------------
    //
    //
    //
    //---------------------------------------------------------------
     public void prepareWait4NewMessage() throws X400APIException {
         logger.debug("->>>>>>> Waiting new message ({})", this.waitingTimeout);
        System.out.println("->>>>>>> wait4NewMessage Waiting new message for seconds : " + Integer.toString(this.waitingTimeout));
        X400ms.x400_ms_enablewait();
        registerAutoAlert(autoAlertId);
        X400ms.x400_ms_waitnew(session, autoAlertId);
        
     }
     
// DUC RAO CAI NAY NO CHAN     
     /*
     public int wait4NewMessage() throws X400APIException {
        
        
       // return this.session.waitForNewMessages(this.waitingTimeout);    // isode api
        return this.session.waitForNewMessages(1);    // isode api
        
        //return this.session.waitForNewMessages(1);    
        //return 1;
    }
     */
    //---------------------------------------------------------------
    //
    //
    //
    //---------------------------------------------------------------
    
    public int wait4NewMessage(int val) throws X400APIException {
        int i = 0;
        X400ms.x400_ms_waitnew(session, autoAlertId);
        logger.debug("Waiting new message ({})", val);
        X400ms.x400_ms_enablewait();

        i = this.session.waitForNewMessages(val);

        return i;
    }
    
    //---------------------------------------------------------------
    //
    //
    //
    //---------------------------------------------------------------
    public ReceivedMessage1 getOne(int mode) throws X400APIException {

        ReceiveMsg msg = new ReceiveMsg(this.session);
        ReceivedMessage1 rm1 = new ReceivedMessage1(msg,mode);
        if (msg.GetType() == X400_att.X400_MSG_MESSAGE) {
            System.out.println("ReceivedMessage1 ==========================");
            System.out.println("SEQ: " + rm1.getSequenceNumber());
            System.out.println("SUBJ: " + msg.getSubject());
            System.out.println("CONTENT:" + msg.getTextContent());

        } else if (msg.GetType() == X400_att.X400_MSG_REPORT) {
            System.out.println("The DR content is:\n" + msg.getReportContentAsText());
        }
        // logger.debug("----------------------------- \n\n\n\n");
//        logger.debug("COUNT: " + this.session.getRefreshNumberOfMessages());
        this.session.deleteMessageObject(msg);
        return rm1;
    }

    public void getNext() throws X400APIException {

        ReceiveMsg msg = this.session.receiveNextAvailableMessage();
        // ReceiveMsg msg = new ReceiveMsg(this.session);

        if (msg.GetType() == X400_att.X400_MSG_MESSAGE) {
            logger.debug(msg.getFrom());
            logger.debug(msg.getSubject());
            logger.debug(msg.getTextContent());
        } else {
            System.out.println("The DR content is:\n" + msg.getReportContentAsText());
        }

        logger.debug("-----------------------------");
        logger.debug("COUNT: " + this.session.getRefreshNumberOfMessages());
        this.session.deleteMessageObject(msg);

    }

    public void cancelWaiting() {
//        this.session.cancelWait();
        X400ms.x400_ms_cancelwait();
        // logger.info("Cancel waiting");
    }

    public ReceiveMsg get1(Integer seq,int mode) throws X400APIException, IOException {
       
            ReceiveMsg message;
            message = new ReceiveMsg(this.session, seq);
            return message;
    }
    // new function
    public ReceivedMessage1 get(Integer seq,int mode) throws X400APIException, IOException {
        try {
            ReceiveMsg message;
            message = new ReceiveMsg(this.session, seq);
            ReceivedMessage1 receivedMessage = new ReceivedMessage1(message,mode);
            // dung add
//            receivedMessage.setSequenceNumber(seq);
            return receivedMessage;
            /*      
//        final MSMessage message = new MSMessage();
//        int result = X400ms.x400_ms_msgget(this.session, seq, message);

        if (result == X400_att.X400_E_INT_ERROR || result == X400_att.X400_E_SYSERROR) {
            String error = X400ms.x400_ms_get_string_error(session, result);
//            this.disconnect();
            throw new X400APIException(error, result);
        }

        if (result != 0) {
            String error = X400ms.x400_ms_get_string_error(session, result);
            logger.debug(String.format("Getting message (seq: %s, code: %s - %s)", seq, result, error));
//            this.disconnect();
            throw new X400APIException(error, result);
        }

        //code 119: There is no file on server
        if (result != X400_att.X400_E_NOERROR) {
            String error = X400ms.x400_ms_get_string_error(session, result);
            logger.debug(String.format("Getting message (seq: %s, code: %s - %s)", seq, result, error));
            throw new X400APIException(error, result);
//            // logger.error("Getting message fail (code %s)", result);
//            // this.disconnect();
//            // throw new X400APIException(String.format("Getting message fail (seq: %s, code: %s)", seq, result));
//            final ReceivedMessage receivedMessage = new ReceivedMessage(result, getSession());
//            receivedMessage.setSequenceNumber(seq);
//            receivedMessage.setErrorMessage(error);
//            return receivedMessage;
        }

        final ReceivedMessage receivedMessage = new ReceivedMessage(message, getSession());
        receivedMessage.setSequenceNumber(seq);
        return receivedMessage;
             */
        } catch (X400APIException ex) {
            java.util.logging.Logger.getLogger(Connection1.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    // old function
    public ReceivedMessage1 getStart(Integer seq,int mode) throws X400APIException, IOException {

        MSMessage message = new MSMessage();
        int result = X400ms.x400_ms_msgget(this.session, seq, message);

        if (result == X400_att.X400_E_INT_ERROR || result == X400_att.X400_E_SYSERROR) {
            String error = X400ms.x400_ms_get_string_error(session, result);
            throw new X400APIException(error, result);
        }

        if (result != 0) {
            String error = X400ms.x400_ms_get_string_error(session, result);
            logger.debug("Getting message #{} error ({}-{})", seq, result, error);
            throw new X400APIException(error, result);
        }

        //code 119: There is no file on server
        if (result != X400_att.X400_E_NOERROR) {
            String error = X400ms.x400_ms_get_string_error(session, result);
            logger.debug("Getting message #{} error ({}-{})", seq, result, error);
            throw new X400APIException(error, result);
//            // logger.error("Getting message fail (code %s)", result);
//            // this.disconnect();
//            // throw new X400APIException(String.format("Getting message fail (seq: %s, code: %s)", seq, result));
//            final ReceivedMessage receivedMessage = new ReceivedMessage(result, getSession());
//            receivedMessage.setSequenceNumber(seq);
//            receivedMessage.setErrorMessage(error);
//            return receivedMessage;
        }

        final ReceivedMessage1 receivedMessage = new ReceivedMessage1(message,mode);
        receivedMessage.setSequenceNumber(seq);
        return receivedMessage;
    }

    public void deleteMsg(MSMessage msmessage_objq) throws X400APIException, IOException {
        // X400ms.x400_ms_msgdel(msmessage_objq, 1);
        this.session.deleteMessageObject(msmessage_objq);
    }
    //---------------------------------------------------
    //
    //
    //
    //---------------------------------------------------
    public void returnIPN(MSMessage message) throws X400APIException {
        
        MSMessage ipnMessage = new MSMessage();
        int result = X400ms.x400_ms_msgmakeIPN(message, -1, ipnMessage);
        

        if (result != X400_att.X400_E_NOERROR) {
            String errorMsg = String.format("Making IPN message failed with error code: 0x%04X", result);
            logger.error(errorMsg);
            throw new X400APIException(errorMsg);
        }

        result = com.isode.x400api.X400ms.x400_ms_msgsend(ipnMessage);
        if (result != X400_att.X400_E_NOERROR) {
            String errorMsg = String.format("Delivery of IPN message failed with error code: 0x%04X", result);
            logger.error(errorMsg);
            disconnect();
            throw new X400APIException(errorMsg);
        }
    }
    //---------------------------------------------------
    //
    //
    //
    //---------------------------------------------------
    
    public int send(MSMessage message) throws X400APIException {
        if (!isConnected()) {
            throw new X400APIException("Not connected to Message Store. Cannot send message.");
        }
        
        logger.error("Delivery message");
        final int sts = com.isode.x400api.X400ms.x400_ms_msgsend(message);
        if (sts != X400_att.X400_E_NOERROR) {
            String errorMsg = String.format("Delivery failed with error code: 0x%04X", sts);
            logger.error(errorMsg);
            disconnect();
            throw new X400APIException(errorMsg);
        }
        return sts;
    }

    public void getFinish(MSMessage item) throws X400APIException {
        if (item == null || !this.isConnected()) {
            return;
        }

        int result = X400ms.x400_ms_msggetfinish(item, 0, 0);
        if (result != X400_att.X400_E_NOERROR) {
            throw new X400APIException("Disconnect fail (code " + result + ")");
        }
    }

    public List<Index> listMessages(MsgClass msgClass, String since) throws X400APIException {
        MSListResult messageList = new MSListResult();

        int code = X400ms.x400_ms_list(session, since, messageList);
        // int code = X400ms.x400_ms_listex(session, since, msgClass.getCode(), messageList);
        if (code != X400_att.X400_E_NOERROR) {
            disconnect();
            // logger.error("Fetching message fail (code %s)", code);
            throw new X400APIException(String.format("Fetching message fail (code %s)", code), code);
        }

        List<Index> indexes = parse(messageList, msgClass);
        X400ms.x400_ms_listfree(messageList);
        return indexes;
    }

    public List<Index> listMessages(MsgClass msgClass, String since, String until) throws X400APIException {
        final MSListResult messageList = new MSListResult();
        final int code = X400ms.x400_ms_listexauxpribefore(session, since, until, msgClass.getCode(), -1, X400_att.X400_PRIORITY_ANY, messageList); // ex(session, since, entryClass, messageList);
        if (code != X400_att.X400_E_NOERROR) {
            disconnect();
            throw new X400APIException(String.format("Fetching message fail (code %s)", code), code);
        }

        final List<Index> indexes = parse(messageList, msgClass);
        X400ms.x400_ms_listfree(messageList);
        return indexes;
    }

    /*---------------------------------------------------
    
    LAY DIEN VAN TU SERVER
    
    
    -----------------------------------------------------*/
    public List<Index> listMessages(String since, String until) throws X400APIException {

        MSListResult listResult = new MSListResult();
        List<Index> indexList = new ArrayList<>();
        
        int code = X400ms.x400_ms_listexauxpribefore(
                session,
                since,
                until,
                MsgClass.STORED_MSG.getCode(),
                -1,
                X400_att.X400_PRIORITY_ANY,
                listResult);

        if (code != X400_att.X400_E_NOERROR) {
            disconnect();
            throw new X400APIException(String.format("Fetching message fail (code %s)", code), code);
        }

        parse(listResult, MsgClass.STORED_MSG, indexList);

        code = X400ms.x400_ms_listexauxpribefore(
                session,
                since,
                until,
                MsgClass.SUBMITTED_MSG.getCode(),
                -1,
                X400_att.X400_PRIORITY_ANY,
                listResult);

        if (code != X400_att.X400_E_NOERROR) {
            disconnect();
            throw new X400APIException(String.format("Fetching message fail (code %s)", code), code);
        }
        parse(listResult, MsgClass.SUBMITTED_MSG, indexList);
        X400ms.x400_ms_listfree(listResult);
        Collections.sort(indexList);
        return indexList;
    }
    /*---------------------------------------------------
    
    
    
    
    -----------------------------------------------------*/
    
    public List<Index> listAllNewMessage(String since, String until) throws X400APIException {

        MSListResult listResult = new MSListResult();
        List<Index> indexList = new ArrayList<>();

        int code = X400ms.x400_ms_listexauxpribefore(
                session,
                since,
                until,
                MsgClass.STORED_MSG.getCode(),
                0,
                X400_att.X400_PRIORITY_ANY,
                listResult);

        if (code != X400_att.X400_E_NOERROR) {
            disconnect();
            throw new X400APIException(String.format("Fetching message fail (code %s)", code), code);
        }
        parse(listResult, MsgClass.STORED_MSG, indexList);
        X400ms.x400_ms_listfree(listResult);
        Collections.sort(indexList);
        return indexList;
    }

    /*---------------------------------------------------
    
    
    
    
    -----------------------------------------------------*/
    public MSMessage newMessage(int type) throws X400APIException {
        final MSMessage msmessage = new MSMessage();
        final int sts = com.isode.x400api.X400ms.x400_ms_msgnew(this.session, type, msmessage);
        if (sts != X400_att.X400_E_NOERROR) {
            logger.error("Creating new x400 message fail ({})", sts);
            throw new X400APIException(String.format("Creating new x400 message fail (code %s)", sts));
        }
        return msmessage;
    }

    public X400Msg newMessage(boolean check) throws X400APIException {
        final X400Msg msmessage;
        if (check) {
            msmessage = new X400Msg((P3BindSession) session, check);
        } else {
            msmessage = new X400Msg((P3BindSession) session);
        }
        return msmessage;
    }

//    public void setLogCategory(Class clzz) {
//        logger = LoggerFactory.getLogger(clzz);
//    }
    public void setStatusChangedEventHandler(ConnectionStatusChangedEventHandler conStatusChangedEventHandler) {
        this.conStatusChangedHandler = conStatusChangedEventHandler;
    }

    // PRIVATE METHODS
    private List<Index> parse(MSListResult msListResult, MsgClass entryClass) {
        final List<Index> results = new ArrayList<>();
        int code = 0;
        int i;
        for (i = 0;; i++) {
            code = X400ms.x400_ms_listgetintparam(msListResult, X400_att.X400_N_MS_SEQUENCE_NUMBER, i);
            if (code == X400_att.X400_E_NO_MORE_RESULTS) {
                break;
            }

            if (code == X400_att.X400_E_NO_VALUE) {
                continue;
            }

            if (code != X400_att.X400_E_NOERROR) {
                break;
            }

            Index item = new Index(msListResult, i);

            if (entryClass != null) {
                item.setClazz(entryClass);
            }

            results.add(item);
        }
        // X400ms.x400_ms_listfree(msListResult);
        return results;
    }

    private void parse(MSListResult msListResult, MsgClass entryClass, List<Index> indices) {

        // final List<Index> results = new ArrayList<>();
        int code = 0;
        int i;
        for (i = 1;; i++) {
            code = X400ms.x400_ms_listgetintparam(msListResult, X400_att.X400_N_MS_SEQUENCE_NUMBER, i);
            if (code == X400_att.X400_E_NO_MORE_RESULTS) {
                break;
            }

            if (code == X400_att.X400_E_NO_VALUE) {
                continue;
            }

            if (code != X400_att.X400_E_NOERROR) {
                break;
            }

            Index item = new Index(msListResult, i);
            item.setClazz(entryClass);
            indices.add(item);
        }
    }
    
//    private List<Index> parse(MSListResult msListResult) {
//        List<Index> results = new ArrayList<>();
//        int code = 0;
//        int i;
//        for (i = 1;; i++) {
//            code = X400ms.x400_ms_listgetintparam(msListResult, X400_att.X400_N_MS_SEQUENCE_NUMBER, i);
//            if (code == X400_att.X400_E_NO_MORE_RESULTS) {
//                break;
//            }
//
//            if (code == X400_att.X400_E_NO_VALUE) {
//                continue;
//            }
//
//            if (code != X400_att.X400_E_NOERROR) {
//                break;
//            }
//
//            Index item = new Index(msListResult, i);
//            results.add(item);
//        }
//        return results;
//    }
//
//    private void onStatusChanged(ConnectionStatus status) {
//        if (this.conStatusChangedHandler == null) {
//            return;
//        }
//        this.conStatusChangedHandler.onConnectionStatusChanged(status);
//    }

    @Override
    public String toString() {
        MString builder = new MString("Connection info : \n");
        builder.append("  > Presentation Address: %s%n", this.presentationAddress);
        builder.append("  > Account: %s%n", this.user);
        builder.append("  > Password: %s%n", this.password);
        builder.append("  > Connection Type: %s%n", this.type);
        builder.append("  > Connection Status: %s%n", this.status);
        return builder.toString();
    }

    //<editor-fold defaultstate="collapsed" desc="Class property methods">
    /**
     * @return the autoAlertId
     */
    public int getAutoAlertId() {
        return autoAlertId;
    }

    /**
     * @param autoAlertId the autoAlertId to set
     */
    public void setAutoAlertId(int autoAlertId) {
        this.autoAlertId = autoAlertId;
    }

    /**
     * @param presentationAddress the presentationAddress to set
     */
    public void setPresentationAddress(String presentationAddress) {
        this.presentationAddress = presentationAddress;
    }

    /**
     * @param user the user to set
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @param waitingTimeout the waitingTimeout to set
     */
    public void setWaitingTimeout(int waitingTimeout) {
        this.waitingTimeout = waitingTimeout;
    }

    //</editor-fold>
}
