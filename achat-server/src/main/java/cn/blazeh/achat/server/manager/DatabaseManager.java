package cn.blazeh.achat.server.manager;

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

    private static final String URL;
    private static final String USERNAME;
    private static final String PASSWORD;
    private Connection connection;

    static {
        Properties prop = new Properties();
        try {
            if(new File("./server.properties").exists()) {
                try(InputStream input = new FileInputStream("./server.properties")) {
                    prop.load(input);
                }
            } else {
                prop.load(DatabaseManager.class.getResourceAsStream("/server.properties"));
                try(OutputStream output = new FileOutputStream("./server.properties")) {
                    prop.store(output, "AChat Server Configurations");
                }
            }
            URL = prop.getProperty("db.url");
            USERNAME = prop.getProperty("db.username");
            PASSWORD = prop.getProperty("db.password");
        } catch (Exception e) {
            throw new RuntimeException("加载配置文件server.properties失败", e);
        }
    }

    DatabaseManager() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
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