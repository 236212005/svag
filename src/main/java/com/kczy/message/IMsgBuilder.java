package com.kczy.message;

/**
 * @author shirui
 */
public interface IMsgBuilder {
    /**
     * 构造并返回消息字节数组
     * @return
     */
    byte[] buildMsg();
}
