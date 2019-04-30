package com.kczy.common.util;

/**
 * 常用全局变量
 * @author shirui
 */
public interface Global {

    /**
     * 消息头总长度（32位*5组对齐=160位)
     */
    int MSG_HEAD_LEN = 160;
    /**
     * 协议版本
     */
    int VERSION = 1;
    /**
     * 首部长度
     */
    int HEAD_LEN = 5;
    /**
     * 无IP地址
     */
    int UNKNOWN_IP_ADDR = 0;
    /**
     * 无端口号
     */
    int UNKNOWN_PORT = 0;
}
