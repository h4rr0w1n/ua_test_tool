/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.attech.amhs.ua.isode.enums;

/**
 *
 * @author ANDH
 */
public enum ConnectionType {
    P7(0), P3(1);

    private final int value;

    ConnectionType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
