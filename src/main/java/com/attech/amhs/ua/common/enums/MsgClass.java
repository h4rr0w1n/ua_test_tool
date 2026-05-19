/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.attech.amhs.ua.common.enums;

/**
 *
 * @author andh
 */
public enum MsgClass {
    
    
    STORED_MSG(0), SUBMITTED_MSG(1);
    
    
    private final int code;

    MsgClass(int value) {
        this.code = value;
    }

    public int getCode() {
        return code;
    }

}
