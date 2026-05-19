/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.attech.amhs.ua.common;

/**
 *
 * @author andh
 */
public final class MString {
    
    private final StringBuilder internalBuilder;
    
    public MString(){
        internalBuilder = new StringBuilder();
    }
    
    public MString(String str) {
        internalBuilder = new StringBuilder(str);
    }
    
    public MString append(String str) {
        internalBuilder.append(str);
        return this;
    }
    
    public MString appendLine(String str) {
        internalBuilder.append(str).append("\n");
        return this;
    }
    
    public MString appendLine() {
        internalBuilder.append("\n");
        return this;
    }
    
    public MString append(String str, Object... value) {
        internalBuilder.append(String.format(str, value));
        return this;
    }
    
    @Override
    public String toString() {
        return internalBuilder.toString();
    }
}
