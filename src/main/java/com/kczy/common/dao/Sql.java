package com.kczy.common.dao;

public interface Sql {

    /**
     * 查询图片服务器ip和端口
     */
    String QUERY_IMG_SERVERS = "select IP_ADDR, LISTEN_PORT, PATH, `DESC` from ch_server where SERVER_NAME like 'image%';";
}
