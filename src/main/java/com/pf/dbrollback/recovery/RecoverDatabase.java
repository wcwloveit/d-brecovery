package com.pf.dbrollback.recovery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Author:王春伟
 * Date:2020/11/19
 * 佛系码农,热爱生活！
 **/
public class RecoverDatabase {

    private final static Logger LOGGER = LoggerFactory.getLogger(RecoverDatabase.class);

    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/db_bak?useUnicode=true&characterEncoding=utf8";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "123456";
    public static Connection connection = null;

    static {
        try {
            Class.forName(DRIVER);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (Exception e) {
            LOGGER.error("===========出现异常===========", e);
        }
    }

    /**
     * 获取数据库连接
     */
    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            LOGGER.error("get connection failure", e);
        }
        return conn;
    }

    /**
     * 关闭数据库连接
     */
    public static void closeConnection(Connection conn) {
        if(conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                LOGGER.error("close connection failure", e);
            }
        }
    }


}
