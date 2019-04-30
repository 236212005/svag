package com.kczy.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @author shirui
 */
public class SvagServer {

    public final int SERVER_LISTEN_PORT = 18086;

//    public static void main(String[] args) {
//        SvagServer server = new SvagServer();
//        Thread serverThread = new Thread(server.getThread());
//        serverThread.start();
//    }

    public Runnable getThread() {
        return new ServerThread();
    }

    private class ServerThread implements Runnable {
        @Override
        public void run() {
            ServerSocket serverSocket = null;
            //1、创建服务器端
            ByteBuffer bf;
            try {
                serverSocket = new ServerSocket(SERVER_LISTEN_PORT);
                while (null != serverSocket) {
                    try {
                        //2、获得客户端请求
                        System.out.println("========================================================");
                        System.out.println("Start listening at port " + SERVER_LISTEN_PORT + "...");
                        Socket client = serverSocket.accept();
                        System.out.print(Calendar.getInstance().getTime() +
                                " : Got request from " + client.getLocalAddress() + " , ");

                        //3、获取输入流，并读取客户端信息
                        InputStream is = client.getInputStream();
                        int bytesRead = is.read();
                        List<Byte> request = new ArrayList<>();
                        while (bytesRead != -1) {
                            request.add((byte) bytesRead);
                            bytesRead = is.read();
                        }
                        bf = ByteBuffer.allocate(request.size()).order(ByteOrder.BIG_ENDIAN);
                        System.out.println("length = " + bf.array().length + " . ");
                        for (int i = 0; i < request.size(); i++) {
                            bf.put(request.get(i));
                        }

                        //关闭输入流
                        client.shutdownInput();

                        //4、获取输出流，响应客户端的请求
                        byte[] resultBytes = ServerMsgProcessor.getInstance().processMessage(bf.array());
                        OutputStream os = client.getOutputStream();
                        os.write(resultBytes);
                        System.out.println("Send response to client: length = " + resultBytes.length);
                        os.flush();

                        //5、关闭资源
                        os.close();
                        is.close();
                        client.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (null != serverSocket) {
                    try {
                        serverSocket.close();
                    } catch (IOException e) {
                        System.out.println("Exception happened when server socket close.");
                    }
                }
            }
        }
    }
}
