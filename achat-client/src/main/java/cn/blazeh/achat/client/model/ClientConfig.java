package cn.blazeh.achat.client.model;

import java.io.*;
import java.util.Properties;

public final class ClientConfig {

    private static final String URL;

    private static final String HOST;
    private static final int PORT;

    static {
        Properties prop = new Properties();
        try {
            if(new File("./client.properties").exists()) {
                try(InputStream input = new FileInputStream("./client.properties")) {
                    prop.load(input);
                }
            } else {
                prop.load(ClientConfig.class.getResourceAsStream("/client.properties"));
                try(OutputStream output = new FileOutputStream("./client.properties")) {
                    prop.store(output, "AChat Client Configurations");
                }
            }
            URL = prop.getProperty("db.url");
            HOST = prop.getProperty("server.host");
            PORT = Integer.parseInt(prop.getProperty("server.port"));
        } catch (Exception e) {
            throw new RuntimeException("加载配置文件client.properties失败", e);
        }
    }

    private ClientConfig() {}

    public static String getUrl() {
        return URL;
    }

    public static String getHost() {
        return HOST;
    }

    public static int getPort() {
        return PORT;
    }

}
