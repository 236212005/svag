package com.kczy.message;

/**
 * @author shirui
 */
public class QryDiskMsgBuilder extends AbsMsgBuilder {

    public byte[] buildMsg() {
        return super.buildMsgHead(AbsMsgBuilder.SERVICE_TYPE, AbsMsgBuilder.MSGID_QRY_DISK);
    }
}
