package com.kczy.common.util;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * @author shirui
 */
public class SendMailUtil {

    /**
     * smtp服务器
     */
    private static String HOST = "";
    /**
     * 用户名
     */
    private static String USER = "";
    /**
     * 密码
     */
    private static String PWD = "";
    /**
     * 邮件标题
     */
    private static MailAuthenticator authenticator;
    private static Properties mailProps = new Properties();
    private MimeMessage message;
    private Session session;
    private Transport transport;

    static {
        HOST = "x";
        USER = "x";
        PWD = "x";

        mailProps.put("mail.smtp.host", HOST);
        mailProps.put("mail.smtp.ssl.trust", HOST);
        mailProps.put("mail.smtp.auth", "true");
        mailProps.put("mail.transport.protocol", "smtp");
        mailProps.put("mail.smtp.starttls.enable", "true");
        mailProps.put("mail.smtp.ssl.checkserveridentity", "false");
    }


    /**
     * 供外界调用的发送邮件接口
     */
    public boolean sendEmail(String title, String content, String[] recivers, List<File> fileList) {
        boolean isSuccess = true;
        try {
            // 初始化smtp发送邮件所需参数
            initSmtpParams();
            // 发送邮件
            doSendHtmlEmail(title, content, Arrays.asList(recivers), fileList);
        } catch (Exception e) {
            e.printStackTrace();
            isSuccess = false;
        }
        return isSuccess;
    }

    /**
     * 初始化smtp发送邮件所需参数
     */
    private boolean initSmtpParams() {
        authenticator = new MailAuthenticator(USER, PWD);
        session = Session.getInstance(mailProps, authenticator);
        // 开启后有调试信息
        session.setDebug(false);
        message = new MimeMessage(session);

        return true;
    }

    /**
     * 发送邮件
     */
    private boolean doSendHtmlEmail(String title, String htmlContent, List<String> receivers, List<File> fileList) {
        try {
            // 发件人
            InternetAddress from = new InternetAddress(USER);
            message.setFrom(from);

            // 收件人(多个)
            InternetAddress[] sendTo = new InternetAddress[receivers.size()];
            for (int i = 0; i < receivers.size(); i++) {
                sendTo[i] = new InternetAddress(receivers.get(i));
            }
            message.setRecipients(MimeMessage.RecipientType.TO, sendTo);

            // 邮件主题
            message.setSubject(title);

            // 添加邮件的各个部分内容，包括文本内容和附件
            Multipart multipart = new MimeMultipart();

            // 添加邮件正文
            BodyPart contentPart = new MimeBodyPart();
            contentPart.setContent(htmlContent, "text/html;charset=UTF-8");
            multipart.addBodyPart(contentPart);

            // 遍历添加附件
            if (fileList != null && fileList.size() > 0) {
                for (File file : fileList) {
                    BodyPart attachmentBodyPart = new MimeBodyPart();
                    DataSource source = new FileDataSource(file);
                    attachmentBodyPart.setDataHandler(new DataHandler(source));
                    attachmentBodyPart.setFileName(file.getName());
                    multipart.addBodyPart(attachmentBodyPart);
                }
            }

            // 将多媒体对象放到message中
            message.setContent(multipart);

            // 保存邮件
            message.saveChanges();

            // SMTP验证，就是你用来发邮件的邮箱用户名密码
            transport = session.getTransport("smtp");
            transport.connect(HOST, USER, PWD);

            // 发送邮件
            transport.sendMessage(message, message.getAllRecipients());

            System.out.println(title + " Email send success!");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (transport != null) {
                try {
                    transport.close();
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    private class MailAuthenticator extends Authenticator {
        private String userName;
        private String password;

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(userName, password);
        }

        public MailAuthenticator(String username, String password) {
            this.userName = username;
            this.password = password;
        }
    }
}
