/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.attech.amhs.ua.isode;

import com.attech.amhs.ua.isode.enums.ConnectionStatus;

/**
 *
 * @author Saitama
 */
public interface ConnectionStatusChangedEventHandler {
    
    void onConnectionStatusChanged(ConnectionStatus status);
    
}
