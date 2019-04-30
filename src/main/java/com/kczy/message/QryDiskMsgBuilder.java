package com.kczy.message;

import com.kczy.common.util.Global;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @author shirui
 */
public class QryDiskMsgBuilder implements IMsgBuilder {

    public static final int MSG_ID = 0x1;
    public static final int SERVICE_TYPE = 0x1;

//    public static void main(String[] args) {
//        byte[] a = new QryDiskMsgBuilder().buildMsg();
//        System.out.println();
//    }
    @Override
    public byte[] buildMsg() {
        ByteBuffer msgBuf = ByteBuffer.allocate(Global.MSG_HEAD_LEN).order(ByteOrder.BIG_ENDIAN);
        byte versionAndHeadLen = ((0x0f & Global.VERSION)<<4)|(0x0f & Global.HEAD_LEN);
        msgBuf.put(versionAndHeadLen);
        msgBuf.put((byte) (0x0ff & SERVICE_TYPE));
        msgBuf.put((byte) 0x0);
        msgBuf.put((byte) Global.MSG_HEAD_LEN);
        msgBuf.putInt(Global.UNKNOWN_IP_ADDR);
        msgBuf.putInt(Global.UNKNOWN_IP_ADDR);
        msgBuf.putInt(Global.UNKNOWN_PORT);
        msgBuf.put((byte) 0x0);
        msgBuf.put((byte) (0x0ff & MSG_ID));
        msgBuf.put((byte) 0x0);
        msgBuf.put((byte) 0x0);
        return msgBuf.array();
    }
}
