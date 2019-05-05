package com.kczy.message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * @author shirui
 */
public class QryDiskMsgHandler implements IHandler {

    @Override
    public byte[] processMessage(byte[] messageBytes) {
        System.out.println("Start to process request use QryDiskMsgHandler->processMessage().");
        Runtime runtime = Runtime.getRuntime();
        StringBuffer resultMsg = new StringBuffer();
        StringBuffer errorMsg = new StringBuffer();
        try {
            String cmd = "df -m|awk -F \" \" '{print $5\":\"$6\"#\"}'";
            System.out.println("Execute native command :" + cmd);
            Process process = runtime.exec(new String[]{"/bin/sh", "-c", cmd});

            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line;
            while (null != (line = in.readLine())) {
                resultMsg.append(line);
            }
            while (null != (line = err.readLine()) && !"".equals(line)) {
                errorMsg.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String result = "MESSAGE:" + resultMsg.toString() + ". ERROR MESSAGE:" + errorMsg.toString();
        System.out.println("Result message is : " + result);
        return result.getBytes(Charset.forName("utf-8"));
    }

    @Override
    public int getMsgId() {
        return QryDiskMsgBuilder.MSGID_QRY_DISK;
    }

    @Override
    public int getServiceType() {
        return QryDiskMsgBuilder.SERVICE_TYPE;
    }
}
