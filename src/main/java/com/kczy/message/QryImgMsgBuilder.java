package com.kczy.message;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * @author shirui
 */
public class QryImgMsgBuilder extends AbsMsgBuilder {

    public byte[] buildMsg(File curFile) {
        ByteBuffer msgBuf = ByteBuffer.wrap(super.buildMsgHead(AbsMsgBuilder.SERVICE_TYPE, AbsMsgBuilder.MSGID_QRY_IMG));
        byte[] fileAbsolutePath = curFile.getAbsolutePath().getBytes(Charset.forName("utf-8"));
        int fileAbsPathLen = fileAbsolutePath.length;
        long fileLen = curFile.length();
        msgBuf.putInt(fileAbsPathLen);
        msgBuf.put(fileAbsolutePath);
        msgBuf.putLong(fileLen);
        return msgBuf.array();
    }
}
