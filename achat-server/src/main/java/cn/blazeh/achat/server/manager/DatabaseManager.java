package cn.blazeh.achat.server.manager;

import cn.blazeh.achat.server.util.ServerConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 数据库连接管理器（单例模式），负责数据库连接的创建、维护和关闭
 */
public enum DatabaseManager {

    INSTANCE;

    private static final Logger LOGGER = LogManager.getLogger(DatabaseManager.class);

    private final String URL;
    private final String USERNAME;
    private final String PASSWORD;

    private Connection connection;

    DatabaseManager() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            URL = ServerConfig.getUrl();
            USERNAME = ServerConfig.getUsername();
            PASSWORD = ServerConfig.getPassword();
        } catch(ClassNotFoundException e) {
            throw new RuntimeException("数据库管理器初始化时出现异常", e);
        }
    }

    /**
     * 获取数据库连接实例（自动重连）
     * @return 可用的数据库连接对象
     * @throws RuntimeException
     */
    public Connection getConnection() {
        try {
            if(connection == null || connection.isClosed())
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            return connection;
        } catch(SQLException e) {
            LOGGER.error("数据库连接获取失败", e);
            throw new RuntimeException("数据库连接获取失败", e);
        }
    }

    /**
     * 关闭数据库连接
     */
    public void close() {
        try {
            if(connection != null && !connection.isClosed())
                connection.close();
        } catch(SQLException e) {
            LOGGER.error("数据库连接异常关闭", e);
        }
    }

    /**
     * 获取配置的数据库连接URL
     */
    public String getUrl() {
        return URL;
    }

    /**
     * 获取配置的数据库用户名
     */
    public String getUsername() {
        return USERNAME;
    }

}