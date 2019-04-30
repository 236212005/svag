package com.kczy;

import com.kczy.client.MvPictureTask;
import com.kczy.server.ServerMsgProcessor;
import com.kczy.message.QryDiskMsgHandler;
import com.kczy.server.SvagServer;

import java.util.Timer;

/**
 * SVAG-Application
 * sv=server
 * ag=agent
 *
 * @author shirui
 */
public class SvagApp {
    public static void main(String[] args) {
        if (args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                if ("-s".equals(args[i])) {
                    startServer();
                } else if ("-c".equals(args[i])) {
                    startClient();
                } else if ("-h".equals(args[i])) {
                    printCommandsHelp();
                } else {
                    System.out.println("参数错误!");
                    printCommandsHelp();
                }
            }
        } else {
            System.out.println("参数错误!");
            printCommandsHelp();
        }
    }

    /**
     * 打印参数列表
     */
    private static void printCommandsHelp() {
        System.out.println("==============帮助文档================");
        System.out.println("java -jar [option] <target file name>");
        System.out.println("<target file name> : 程序jar包名称");
        System.out.println("options:");
        System.out.println("\t-s : 启动服务器");
        System.out.println("\t-c : 启动客户端");
        System.out.println("\t-h : 查看帮助");
    }

    /**
     * 启动服务器线程，监听请求
     */
    private static void startServer() {
        ServerMsgProcessor.getInstance().registerHandler(new QryDiskMsgHandler());
        SvagServer server = new SvagServer();
        Thread serverThread = new Thread(server.getThread());
        serverThread.start();
    }

    /**
     * 启动客户端
     */
    private static void startClient() {
//        ClientMsgSender client = new ClientMsgSender();
//        client.queryServers();
        Timer timer = new Timer();
        timer.schedule(new MvPictureTask(), 0, 1000 * 5);
    }
}
