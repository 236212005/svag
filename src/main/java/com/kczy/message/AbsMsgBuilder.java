package com.kczy.message;

import com.kczy.common.util.Global;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @author shirui
 */
public abstract class AbsMsgBuilder {

    /**
     * 消息ID：客户端请求查询服务器磁盘空间
     */
    public static final int MSGID_QRY_DISK = 0x1;

    /**
     * 消息ID：客户端查询指定图片是否在服务器上存在
     */
    public static final int MSGID_QRY_IMG = 0x2;

    /**
     * 消息ID：客户端查询指定图片是否在服务器上存在
     */
    public static final int MSGID_SEND_IMG = 0x3;

    /**
     * 服务类型：图片运维
     */
    public static final int SERVICE_TYPE = 0x1;

    /**
     * 构造并返回消息字节数组
     *
     * @return
     */
    protected byte[] buildMsgHead(int serviceType, int msgId) {
        ByteBuffer msgBuf = ByteBuffer.allocate(Global.MSG_HEAD_LEN).order(ByteOrder.BIG_ENDIAN);
        byte versionAndHeadLen = ((0x0f & Global.VERSION) << 4) | (0x0f & Global.HEAD_LEN);
        msgBuf.put(versionAndHeadLen);
        msgBuf.put((byte) (0x0ff & serviceType));
        msgBuf.put((byte) 0x0);
        msgBuf.put((byte) Global.MSG_HEAD_LEN);
        msgBuf.putInt(Global.UNKNOWN_IP_ADDR);
        msgBuf.putInt(Global.UNKNOWN_IP_ADDR);
        msgBuf.putInt(Global.UNKNOWN_PORT);
        msgBuf.put((byte) 0x0);
        msgBuf.put((byte) (0x0ff & msgId));
        msgBuf.put((byte) 0x0);
        msgBuf.put((byte) 0x0);
        return msgBuf.array();
    }
}
