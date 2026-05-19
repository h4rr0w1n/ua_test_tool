/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.attech.amhs.ua.isode;

import com.isode.x400api.Recip;
import com.isode.x400api.X400_att;
import java.util.Objects;

/**
 *
 * @author ANDH
 */
public class Recipient {

    private String address;
    private Integer notificationRequest;
    private Integer reportRequest;

    public Recipient() {
    }

    public Recipient(String fullAddress) {
        this.address = fullAddress;
    }

    public Recipient(Recip recip) {
        this.address = getStrParam(recip, X400_att.X400_S_OR_ADDRESS);
        this.notificationRequest = getIntParam(recip, X400_att.X400_N_NOTIFICATION_REQUEST);
        this.reportRequest = getIntParam(recip, X400_att.X400_N_REPORT_REQUEST);
    }

    private static Integer getIntParam(Recip recip, int attribute) {

        final int code = com.isode.x400api.X400ms.x400_ms_recipgetintparam(recip, attribute);
        if (code != X400_att.X400_E_NOERROR) {
            return null;
        }
        return recip.GetIntValue();

    }

    private static String getStrParam(Recip recip, int attribute) {
        final StringBuffer buffer = new StringBuffer();
        final int code = com.isode.x400api.X400ms.x400_ms_recipgetstrparam(recip, attribute, buffer);
        if (code != X400_att.X400_E_NOERROR) {
            return null;
        }
        return buffer.toString();
    }

    @Override
    public int hashCode() {
        return address.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Recipient other = (Recipient) obj;
        if (!Objects.equals(this.address, other.address)) {
            return false;
        }
        return true;
    }

    /**
     * @return the notificationRequest
     */
    public Integer getNotificationRequest() {
        return notificationRequest;
    }

    /**
     * @param notificationRequest the notificationRequest to set
     */
    public void setNotificationRequest(Integer notificationRequest) {
        this.notificationRequest = notificationRequest;
    }

    /**
     * @return the reportRequest
     */
    public Integer getReportRequest() {
        return reportRequest;
    }

    /**
     * @param reportRequest the reportRequest to set
     */
    public void setReportRequest(Integer reportRequest) {
        this.reportRequest = reportRequest;
    }

    /**
     * @return the fullAddress
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param fullAddress the fullAddress to set
     */
    public void setAddress(String fullAddress) {
        this.address = fullAddress;
    }
}
