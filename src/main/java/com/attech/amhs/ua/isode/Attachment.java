/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.attech.amhs.ua.isode;

import com.attech.amhs.ua.db.tables.InboxAttachment;
import com.attech.amhs.ua.db.tables.SentAttachment;

/**
 *
 * @author ANDH
 */
public class Attachment {

    /**
     * @return the AppRefOID
     */
    public String getAppRefOID() {
        return AppRefOID;
    }

    /**
     * @param AppRefOID the AppRefOID to set
     */
    public void setAppRefOID(String AppRefOID) {
        this.AppRefOID = AppRefOID;
    }

    /**
     * @return the ContDesc
     */
    public String getContDesc() {
        return ContDesc;
    }

    /**
     * @param ContDesc the ContDesc to set
     */
    public void setContDesc(String ContDesc) {
        this.ContDesc = ContDesc;
    }

    private String name;
    private byte[] data;
    private String AppRefOID;
    private String ContDesc;

    public Attachment() {
    }

    public Attachment(String name, byte[] bytes,String s1, String s2) {
        this.name = name;
        this.data = bytes;
    }
    
    public InboxAttachment getInboxAttachment() {
        return new InboxAttachment(name, data);
    }
    
    public SentAttachment getSentAttachment() {
        return new SentAttachment(name, data);
    }

    //<editor-fold defaultstate="collapsed" desc="Class property methods">
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the data
     */
    public byte[] getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(byte[] data) {
        this.data = data;
    }

    //</editor-fold>


}
