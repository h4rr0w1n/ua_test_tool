/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.attech.amhs.ua.isode;

/**
 *
 * @author hong
 */
public class DeliveryAddress extends Recipient{
    private Integer reportRequest;
    private String delviveryTime;
    private Integer userType;
    private Integer nonDeliveryCode;
    private Integer nonDeliveryDiagnosticCode;
    

    /**
     * @return the reportRequest
     */
    @Override
    public Integer getReportRequest() {
        return reportRequest;
    }

    /**
     * @param reportRequest the reportRequest to set
     */
    @Override
    public void setReportRequest(Integer reportRequest) {
        this.reportRequest = reportRequest;
    }

    /**
     * @return the delviveryTime
     */
    public String getDelviveryTime() {
        return delviveryTime;
    }

    /**
     * @param delviveryTime the delviveryTime to set
     */
    public void setDelviveryTime(String delviveryTime) {
        this.delviveryTime = delviveryTime;
    }

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

    /**
     * @return the nonDeliveryCode
     */
    public Integer getNonDeliveryCode() {
        return nonDeliveryCode;
    }

    /**
     * @param nonDeliveryCode the nonDeliveryCode to set
     */
    public void setNonDeliveryCode(Integer nonDeliveryCode) {
        this.nonDeliveryCode = nonDeliveryCode;
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
}
