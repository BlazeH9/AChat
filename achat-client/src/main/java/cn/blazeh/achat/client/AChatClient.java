package cn.blazeh.achat.client;

import cn.blazeh.achat.client.gui.AuthFrame;
import cn.blazeh.achat.client.gui.ChatFrame;
import cn.blazeh.achat.client.manager.ConnectionManager;
import cn.blazeh.achat.client.manager.SessionManager;
import cn.blazeh.achat.client.service.AuthService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;

/**
 * 聊天客户端主入口。
 * 负责初始化GUI界面和管理客户端生命周期
 */
public class AChatClient {

    private static final Logger LOGGER = LogManager.getLogger(AChatClient.class);

    private AuthFrame authFrame;
    private ChatFrame chatFrame;
    private final AuthService authService = new AuthService();

    private static final String BANNER = """

                  ___           ___           ___           ___                \s
                 /  /\\         /  /\\         /__/\\         /  /\\          ___  \s
                /  /::\\       /  /:/         \\  \\:\\       /  /::\\        /  /\\ \s
               /  /:/\\:\\     /  /:/           \\__\\:\\     /  /:/\\:\\      /  /:/ \s
              /  /:/~/::\\   /  /:/  ___   ___ /  /::\\   /  /:/~/::\\    /  /:/  \s
             /__/:/ /:/\\:\\ /__/:/  /  /\\ /__/\\  /:/\\:\\ /__/:/ /:/\\:\\  /  /::\\  \s
             \\  \\:\\/:/__\\/ \\  \\:\\ /  /:/ \\  \\:\\/:/__\\/ \\  \\:\\/:/__\\/ /__/:/\\:\\ \s
              \\  \\::/       \\  \\:\\  /:/   \\  \\::/       \\  \\::/      \\__\\/  \\:\\\s
               \\  \\:\\        \\  \\:\\/:/     \\  \\:\\        \\  \\:\\           \\  \\:\\
                \\  \\:\\        \\  \\::/       \\  \\:\\        \\  \\:\\           \\__\\/
                 \\__\\/         \\__\\/         \\__\\/         \\__\\/               \s
            
                2025 AChat - A Simple Chat Client\s
            
            """;

    /**
     * 启动客户端主程序
     * 初始化GUI界面并建立服务器连接
     *
     * @param host 服务器主机地址
     * @param port 服务器端口号
     */
    public void start(String host, int port) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        authFrame = new AuthFrame(authService, () -> {
            chatFrame = new ChatFrame(SessionManager.INSTANCE.getSession().getUserId(), this::stop);
            LOGGER.debug("聊天页面已创建完成");
            chatFrame.setVisible(true);
            LOGGER.info("聊天页面已显示");
        }, this::stop);
        ConnectionManager.INSTANCE.connect(this, host, port);
        authFrame.setVisible(true);
        LOGGER.info("认证页面已显示");
    }

    /**
     * 停止客户端运行
     * 断开所有服务器连接
     */
    public void stop() {
        ConnectionManager.INSTANCE.disconnect();
    }

    public static void main(String[] args) throws Exception {
        LOGGER.info("客户端启动中");
        System.out.println(BANNER);
        final AChatClient client = new AChatClient();
        client.start("localhost", 8080);
    }

    /**
     * 获取认证窗口实例
     * @return 当前认证窗口对象
     */
    public AuthFrame getAuthFrame() {
        return authFrame;
    }

    /**
     * 获取聊天窗口实例
     * @return 当前聊天窗口对象
     */
    public ChatFrame getChatFrame() {
        return chatFrame;
    }
}