package com.kczy.message;

/**
 * @author shirui
 */
public interface IHandler {
    byte[] processMessage(byte[] messageBytes);

    int getMsgId();
    int getServiceType();
}
