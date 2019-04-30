package com.kczy.client;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author shirui
 */
public class ClientMsgSender {
    private static ClientMsgSender instance = null;

    private ClientMsgSender() {
    }

    public static ClientMsgSender getInstance() {
        if (null == instance) {
            instance = new ClientMsgSender();
        }
        return instance;
    }

    /**
     * 客户端发送消息到服务器，并取得服务器的返回消息。
     *
     * @param msg        要发送的消息数组
     * @param serverIp   服务器IP
     * @param serverPort 服务器端口
     * @return 服务器的返回信息。若出现错误，则返回消息字节数组的长度为0。
     */
    public byte[] sendRequest(byte[] msg, String serverIp, int serverPort) {
        Socket client;
        ByteBuffer respBuf = null;
        try {
            //1、创建客户端
            client = new Socket(serverIp, serverPort);

            //2、创建用于向服务器发送信息的输出流
            OutputStream os = client.getOutputStream();
            os.write(msg);
            os.flush();
            client.shutdownOutput();

            //3、获取输入流，并读取服务器端的响应信息
            InputStream is = client.getInputStream();
            int bytesRead = is.read();
            List<Byte> respArray = new ArrayList<>();
            while (bytesRead != -1) {
                respArray.add((byte) bytesRead);
                bytesRead = is.read();
            }
            respBuf = ByteBuffer.allocate(respArray.size()).order(ByteOrder.BIG_ENDIAN);
            for (int i = 0; i < respArray.size(); i++) {
                respBuf.put(respArray.get(i));
            }

            //4、关闭资源
            is.close();
            os.close();
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return respBuf == null ? new byte[0] : respBuf.array();
    }
}
