package cn.blazeh.achat.client.manager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

public enum DatabaseManager {

    INSTANCE;

    private static final Logger LOGGER = LogManager.getLogger(DatabaseManager.class);

    private static final String DB_URL = "jdbc:sqlite:achat.db";
    private Connection connection;

    DatabaseManager() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DB_URL);
            createTables();
        } catch(ClassNotFoundException | SQLException e) {
            throw new RuntimeException("数据库初始化失败", e);
        }
    }

    private void createTables() throws SQLException {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS messages (
                message_id INTEGER PRIMARY KEY,
                sender TEXT NOT NULL,
                receiver TEXT NOT NULL,
                timestamp INTEGER NOT NULL,
                type INTEGER NOT NULL,
                content TEXT
            )
        """;

        try(Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
        }
    }

    public Connection getConnection() {
        try {
            if(connection == null || connection.isClosed())
                connection = DriverManager.getConnection(DB_URL);
            return connection;
        } catch(SQLException e) {
            LOGGER.error("数据库连接获取失败", e);
            throw new RuntimeException("数据库连接获取失败", e);
        }
    }

    public void close() {
        try {
            if(connection != null && !connection.isClosed())
                connection.close();
        } catch(SQLException e) {
            LOGGER.error("数据库连接异常关闭", e);
        }
    }

}
