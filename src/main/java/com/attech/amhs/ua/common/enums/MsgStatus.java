/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.attech.amhs.ua.common.enums;

/**
 *
 * @author HONG
 */
public enum MsgStatus {

    NEW(0),
    LISTED(1),
    FETCH(2),
    ANY(-1);

    private final int code;

    MsgStatus(int value) {
        this.code = value;
    }

    public int getCode() {
        return code;
    }
}
