package com.kczy.server;

import com.kczy.message.IHandler;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Hashtable;

/**
 * @author shirui
 */
public class ServerMsgProcessor {
    private static ServerMsgProcessor instance;
    private Hashtable<Integer, Hashtable<Integer, IHandler>> handlers = new Hashtable<>();

    private ServerMsgProcessor() {
    }

    /**
     * 获取单实例
     *
     * @return
     */
    public static ServerMsgProcessor getInstance() {
        if (null == instance) {
            instance = new ServerMsgProcessor();
        }
        return instance;
    }

    /**
     * 注册消息处理器
     *
     * @param handler
     */
    public void registerHandler(IHandler handler) {
        //服务类型下有链表
        if (null != handler && handlers.contains(handler.getServiceType())) {
            Hashtable<Integer, IHandler> hashtable = handlers.get(handler.getServiceType());
            //消息类型下没处理器
            if (!hashtable.contains(handler.getMsgId())) {
                hashtable.put(handler.getMsgId(), handler);
            } else {
                System.out.println("该类型的处理器已注册，请勿重复注册！处理器服务类型=" +
                        handler.getServiceType() + "，消息ID=" + handler.getMsgId());
            }
        } else {
            //服务类型下无链表（更没有处理器）
            Hashtable<Integer, IHandler> hashtable = new Hashtable<>();
            hashtable.put(handler.getMsgId(), handler);
            handlers.put(handler.getServiceType(), hashtable);
        }
    }

    /**
     * 解析服务器收到的消息，并交给相应的handler进行业务处理。
     *
     * @param msgBytes
     * @return
     */
    public byte[] processMessage(byte[] msgBytes) {
        byte[] resultBytes;
        StringBuffer log = new StringBuffer();
        try {
            ByteBuffer msgBuf = ByteBuffer.wrap(msgBytes).order(ByteOrder.BIG_ENDIAN);
            byte tmp = msgBuf.get();
            //版本号
            int versionNo = (0xf0 & tmp) >> 4;
            log.append("RECV: 版本号=" + versionNo + ", ");

            //首部长度
            int headLen = 0xf & tmp;
            log.append("首部长度=" + headLen + ", ");

            //服务类型
            int svcType = msgBuf.get();
            log.append("服务类型=" + svcType + ", ");

            //报文总长度
            int totalLen1 = msgBuf.get() & 0x0FF;
            int totalLen2 = msgBuf.get() & 0x0FF;
            int totalLen = totalLen1 << 8 | totalLen2;
            log.append("报文总长度=" + totalLen + ", ");

            //源IP
            String originIp = parseIp(msgBuf);
            log.append("源IP=" + originIp + ", ");

            //目的IP
            String terminalIp = parseIp(msgBuf);
            log.append("目的IP=" + terminalIp + ", ");

            //源地址端口号
            int oport1 = msgBuf.get() & 0x0FF;
            int oport2 = msgBuf.get() & 0x0FF;
            int originPort = oport1 << 8 | oport2;
            log.append("源地址端口号=" + originPort + ", ");

            //目的地址端口号
            int tport1 = msgBuf.get() & 0x0FF;
            int tport2 = msgBuf.get() & 0x0FF;
            int terminalPort = tport1 << 8 | tport2;
            log.append("目的地址端口号=" + terminalPort + ", ");

            //消息ID
            int id1 = msgBuf.get() & 0x0FF;
            int id2 = msgBuf.get() & 0x0FF;
            int id = id1 << 8 | id2;
            log.append("消息ID=" + id + ", ");

            //16位预留
            msgBuf.get();
            msgBuf.get();

            byte[] msgBodyByteArray = new byte[totalLen - 32 * 5];
            msgBuf.get(msgBodyByteArray);
            log.append("消息体长度=" + msgBodyByteArray.length);
            System.out.println(log.toString());

            resultBytes = handlers.get(svcType).get(id).processMessage(msgBodyByteArray);
        } catch (Exception e) {
            e.printStackTrace();
            resultBytes = new byte[0];
        }
        return resultBytes;
    }

    /**
     * 解析IP地址
     *
     * @param msgBuf
     * @return
     */
    private String parseIp(ByteBuffer msgBuf) {
        int oip1 = msgBuf.get() & 0xFF;
        int oip2 = msgBuf.get() & 0xFF;
        int oip3 = msgBuf.get() & 0xFF;
        int oip4 = msgBuf.get() & 0xFF;
        return oip1 + "." + oip2 + "." + oip3 + "." + oip4;
    }

//    /**
//     * 测试方法
//     * @param args
//     */
//    public static void main(String[] args) {
//        byte[] msgBody = "hello world!".getBytes(Charset.forName("utf-8"));
//        int totalLen = 32 * 5 + msgBody.length;
//        byte tlenHigh8Bit = (byte) ((totalLen >> 8) & 0x0ff);
//        byte tlenLow8Bit = (byte) (totalLen & 0x0ff);
//        byte[] msgHead = new byte[]{
//                0x15,
//                0x1,
//                tlenHigh8Bit,
//                tlenLow8Bit,
//                (byte) 0xc0,
//                (byte) 0xa8,
//                (byte) 0xd4,
//                0x6e,
//                0x6e,
//                (byte) 0xb9,
//                (byte) 0xb8,
//                (byte) 0x98,
//                (18086 & 0x0ff00) >> 8,
//                (byte) (18086 & 0x000ff),
//                (18086 & 0x0ff00) >> 8,
//                (byte) (18086 & 0x000ff),
//                0x0,
//                0x1,
//                0,
//                0
//        };
//        ByteBuffer msgBuf = ByteBuffer.allocate(msgHead.length + msgBody.length).order(ByteOrder.BIG_ENDIAN);
//        msgBuf.put(msgHead);
//        msgBuf.put(msgBody);
//        ServerMsgProcessor.getInstance().processMessage(msgBuf.array());
//    }
}
