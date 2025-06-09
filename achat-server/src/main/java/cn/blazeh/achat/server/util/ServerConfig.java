package cn.blazeh.achat.server.util;

import java.io.*;
import java.util.Properties;

public final class ServerConfig {

    private static final String URL;
    private static final String USERNAME;
    private static final String PASSWORD;

    private static final String HOST;
    private static final int PORT;

    static {
        Properties prop = new Properties();
        try {
            if(new File("./server.properties").exists()) {
                try(InputStream input = new FileInputStream("./server.properties")) {
                    prop.load(input);
                }
            } else {
                prop.load(ServerConfig.class.getResourceAsStream("/server.properties"));
                try(OutputStream output = new FileOutputStream("./server.properties")) {
                    prop.store(output, "AChat Server Configurations");
                }
            }
            URL = prop.getProperty("db.url");
            USERNAME = prop.getProperty("db.username");
            PASSWORD = prop.getProperty("db.password");
            HOST = prop.getProperty("server.host");
            PORT = Integer.parseInt(prop.getProperty("server.port"));
        } catch (Exception e) {
            throw new RuntimeException("加载配置文件server.properties失败", e);
        }
    }

    private ServerConfig() {}

    public static String getUrl() {
        return URL;
    }

    public static String getUsername() {
        return USERNAME;
    }

    public static String getPassword() {
        return PASSWORD;
    }

    public static String getHost() {
        return HOST;
    }

    public static int getPort() {
        return PORT;
    }

}
