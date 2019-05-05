package com.kczy.client;

import com.kczy.common.dao.Connector;
import com.kczy.common.dao.Sql;
import com.kczy.common.util.SendMailUtil;
import com.kczy.message.QryDiskMsgBuilder;
import com.kczy.message.QryImgMsgBuilder;
import com.kczy.message.SendImgMsgBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Calendar;

/**
 * @author shirui
 */
public class MvPictureTask extends AbsClientTask {

    private static final int INDEX_IP = 0;
    private static final int INDEX_PORT = 1;
    private static final int INDEX_PATH = 2;
    private static final int INDEX_DESC = 3;
    private static final String BASE_IMG_FOLDER = "/home/shirui/图片";
    public static final int CODE_FILE_IS_EXIST = 1;

    @Override
    public void run() {
        try {
            queryServerStatus();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void queryServerStatus() {
        // 巡检目标服务器的磁盘使用情况
        String[][] rs = Connector.querySql(Sql.QUERY_IMG_SERVERS);

        for (String[] row : rs) {
            String ip = row[INDEX_IP] != null && !"".equals(row[INDEX_IP]) ? row[INDEX_IP] : "localhost";
            int port = row[INDEX_PORT] != null && !"".equals(row[INDEX_PORT]) ? Integer.parseInt(row[INDEX_PORT]) : 0;
            String path = row[INDEX_PATH];
            String desc = row[INDEX_DESC];
            if (null == path || "".equals(path)) {
                continue;
            }

            byte[] response = ClientMsgSender.getInstance().sendRequest(
                    new QryDiskMsgBuilder().buildMsg(), ip, port
            );
            System.out.println(Calendar.getInstance().getTime() +
                    " : Send message to server " + ip + ":" + port + ". Used" +
                    "QryDiskMsgBuilder().buildMsg()" + ". Received message length = " + response.length);
            boolean isOverLimit = calcRate(path, desc, response);
            if (isOverLimit) {
                //已超过设置的阈值，发送告警邮件
                if (sendEmail()) {
                    System.out.println("告警邮件发送成功。");
                } else {
                    System.out.println("告警邮件发送失败!");
                }
            } else {
                //未超过阈值，则移动图片到服务器
                moveImage(new File(BASE_IMG_FOLDER), ip, port);
            }
        }
    }

    /**
     * 移动图片到服务器
     */
    private void moveImage(File curFile, String ip, int port) {
        if (curFile.isDirectory()) {
            File[] childs = curFile.listFiles();
            for (File nextFile : childs) {
                moveImage(nextFile, ip, port);
            }
        } else {
            //TODO 询问服务器是否存在同样图片
            byte[] qryImgResp = ClientMsgSender.getInstance().sendRequest(
                    new QryImgMsgBuilder().buildMsg(curFile), ip, port
            );
            if (qryImgResp != null & qryImgResp.length > 0 && qryImgResp[0] == CODE_FILE_IS_EXIST) {
                //TODO 若存在，则删除本地图片
                System.out.println(deleteCurFile(curFile));
            } else {
                //TODO 若不存在，则传输本地图片给服务器，并取得服务器返回的处理结果，与本地图片进行校验
                byte[] sndImgResp = new byte[0];
                try {
                    sndImgResp = ClientMsgSender.getInstance().sendRequest(
                            new SendImgMsgBuilder().buildMsg(curFile), ip, port
                    );
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteBuffer resp = ByteBuffer.wrap(sndImgResp);
                long resultCode = resp.getLong();
                if (resultCode == curFile.length()) {
                    //TODO 若校验成功，则删除本地图片
                    System.out.println(deleteCurFile(curFile));
                } else {
                    //TODO 若校验失败，则重新传输，直至成功}
                }
            }
        }
    }

    private String deleteCurFile(File curFile) {
        return "Delete file " + curFile.getAbsolutePath() + (curFile.delete() ? "成功." : "失败.");
    }

    /**
     * 计算磁盘是否超过了设置的阈值
     *
     * @param path
     * @param desc
     * @param response
     * @return true：已超过阈值，需要发送告警信息； false：尚未超过阈值，可以进行图片移动
     */
    private boolean calcRate(String path, String desc, byte[] response) {
        boolean isOverLimit;
        String resStr = new String(response, Charset.forName("utf-8"));
        System.out.println(resStr);
        String[] blocks = resStr.split("#");
        String usedRate = null;
        float usedRateFloat = 0;
        float alramRateFloat = 0;
        for (String block : blocks) {
            if (block.endsWith(path)) {
                usedRate = block.split(":")[0];
            }
        }
        if (null != usedRate) {
            usedRateFloat = Float.parseFloat(usedRate.replaceAll("%", "")) / 100;
        }
        if (null != desc && !"".equals(desc)) {
            alramRateFloat = Float.parseFloat(desc.replaceAll("%", "")) / 100;
        }
        //超过阈值则告警
        if (alramRateFloat > 0 && usedRateFloat > 0 && usedRateFloat >= alramRateFloat) {
            isOverLimit = true;
        } else {
            //未超过阈值则移动图片
            isOverLimit = false;
        }
        return isOverLimit;
    }

    private boolean sendEmail() {
        // 邮件主题
        String title = "【重要】磁盘空间满预警";
        // 邮件正文
        String htmlContent = "磁盘空间使用率已超过预设值，请尽快检查！";
        // 收件人
        String[] receivers = new String[]{"shirui0000@dingtalk.com"};
        return new SendMailUtil().sendEmail(title, htmlContent, receivers, null);
    }
}
