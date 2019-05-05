package com.kczy.message;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author shirui
 */
public class SendImgMsgBuilder extends AbsMsgBuilder {

    public byte[] buildMsg(File curFile) throws IOException {
        ByteBuffer msgBuf = ByteBuffer.wrap(super.buildMsgHead(AbsMsgBuilder.SERVICE_TYPE, AbsMsgBuilder.MSGID_SEND_IMG));
        FileInputStream ins = new FileInputStream(curFile);
        int fileLen = ins.available();
        byte[] fileBytes = new byte[fileLen];
        ins.read(fileBytes);
        ins.close();
        msgBuf.putInt(fileLen);
        msgBuf.put(fileBytes);
        return msgBuf.array();
    }
}
