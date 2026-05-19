/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.attech.amhs.ua.isode;

import com.attech.amhs.ua.db.tables.ReportDetail;
import com.isode.x400api.Recip;
import com.isode.x400api.X400_att;
import com.isode.x400api.X400ms;

/**
 *
 * @author andh
 */
public class ReportRecipient {

    private String address;
    private String suplementInfo;
    private String deliveryTime;
    private Integer nonDeliveryReason;
    private Integer nonDeliveryDiagnosticCode;
    private Integer userType;

    public ReportRecipient() {
    }

    public ReportRecipient(String address, String deliveryTime) {
        this.address = address;
        this.deliveryTime = deliveryTime;
    }

    public ReportRecipient(String address, Integer nonDeliveryReason, Integer nonDeliveryDiagnosticCode) {
        this.address = address;
        this.nonDeliveryDiagnosticCode = nonDeliveryDiagnosticCode;
        this.nonDeliveryReason = nonDeliveryReason;
    }

    public ReportRecipient(Recip recip_obj) {

        this.address = getStr(recip_obj, X400_att.X400_S_OR_ADDRESS);
        this.deliveryTime = getStr(recip_obj, X400_att.X400_S_MESSAGE_DELIVERY_TIME);
        this.nonDeliveryReason = getInt(recip_obj, X400_att.X400_N_NON_DELIVERY_REASON);
        this.nonDeliveryDiagnosticCode = getInt(recip_obj, X400_att.X400_N_NON_DELIVERY_DIAGNOSTIC);
        this.userType = MSUtil.getInt(recip_obj, X400_att.X400_N_TYPE_OF_USER);
        this.suplementInfo = MSUtil.getStr(recip_obj, X400_att.X400_S_SUPPLEMENTARY_INFO);

    }

/*
    
DUC    
    
*/    
    
    public ReportDetail getReportDetail() {
        ReportDetail reportDetail = new ReportDetail();
        reportDetail.setAddress(address);
        reportDetail.setDeliveryTime(deliveryTime);
        reportDetail.setNonDeliveryReasonCode(nonDeliveryReason);
        reportDetail.setNonDeliverydiagnosticsCode(nonDeliveryDiagnosticCode);
        reportDetail.setSuplementInfo("Supplementary Information: " + suplementInfo);
        return reportDetail;
    }

    private String getStr(Recip recipObj, int att) {
        StringBuffer value = new StringBuffer();
        int status = X400ms.x400_ms_recipgetstrparam(recipObj, att, value);

        if (status != X400_att.X400_E_NOERROR) {
            return null;
        }
        return value.toString();
    }
    
    private Integer getInt(Recip recipObj, int att) {
        int status = X400ms.x400_ms_recipgetintparam(recipObj, att);
        if (status != X400_att.X400_E_NOERROR) {
            return null;
        }
        return recipObj.GetIntValue();
    }

    /**
     * @return the suplementInfo
     */
    public String getSuplementInfo() {
        return suplementInfo;
    }

    /**
     * @param suplementInfo the suplementInfo to set
     */
    public void setSuplementInfo(String suplementInfo) {
        this.suplementInfo = suplementInfo;
    }

    /**
     * @return the deliveryTime
     */
    public String getDeliveryTime() {
        return deliveryTime;
    }

    /**
     * @param deliveryTime the deliveryTime to set
     */
    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    /**
     * @return the nonDeliveryReason
     */
    public Integer getNonDeliveryReason() {
        return nonDeliveryReason;
    }

    /**
     * @param nonDeliveryReason the nonDeliveryReason to set
     */
    public void setNonDeliveryReason(Integer nonDeliveryReason) {
        this.nonDeliveryReason = nonDeliveryReason;
    }

    /**
     * @return the nonDeliveryDiagnosticCode
     */
    public Integer getNonDeliveryDiagnosticCode() {
        return nonDeliveryDiagnosticCode;
    }

    /**
     * @param nonDeliveryDiagnosticCode the nonDeliveryDiagnosticCode to set
     */
    public void setNonDeliveryDiagnosticCode(Integer nonDeliveryDiagnosticCode) {
        this.nonDeliveryDiagnosticCode = nonDeliveryDiagnosticCode;
    }

    /**
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

//    public void setAddress(String address) {
//        this.address = new Address(null, address);
//    }
    
    /**
     * @return the userType
     */
    public Integer getUserType() {
        return userType;
    }

    /**
     * @param userType the userType to set
     */
    public void setUserType(Integer userType) {
        this.userType = userType;
    }
}
