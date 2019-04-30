package com.kczy.common.dao;

import java.sql.*;

/**
 * @author shirui
 */
public class Connector {

    private static Connection getConn() {
        String driver = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://x:x/x";
        String username = "x";
        String password = "x";
        Connection conn = null;
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public static int executeSql(String sql) {
        Connection conn = getConn();
        int i = 0;
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sql);
            i = pstmt.executeUpdate();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            if (conn != null) {
                conn = null;
            }
            if (pstmt != null) {
                pstmt = null;
            }
        }
        return i;
    }

    public static String[][] querySql(String sql) {
        Connection conn = getConn();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String[][] result = null;
        try {
            pstmt = conn.prepareStatement(sql,
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = pstmt.executeQuery();
            int cols = rs.getMetaData().getColumnCount();
            rs.last();
            int rows = rs.getRow();
            rs.beforeFirst();
            result = new String[rows][cols];
            while (rs.next()) {
                for (int i = 1; i <= cols; i++) {
                    result[rs.getRow() - 1][i - 1] = rs.getString(i);
                }
            }
            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            if (conn != null) {
                conn = null;
            }
            if (rs != null) {
                rs = null;
            }
            if (pstmt != null) {
                pstmt = null;
            }
        }
        if (null == result) {
            result = new String[0][0];
        }
        return result;
    }
}
