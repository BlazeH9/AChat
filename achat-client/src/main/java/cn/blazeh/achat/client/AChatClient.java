package cn.blazeh.achat.client;

import cn.blazeh.achat.client.gui.AuthFrame;
import cn.blazeh.achat.client.gui.ChatFrame;
import cn.blazeh.achat.client.manager.ConnectionManager;
import cn.blazeh.achat.client.manager.SessionManager;
import cn.blazeh.achat.client.service.AuthService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class AChatClient {

    private static final Logger LOGGER = LogManager.getLogger(AChatClient.class);

    private AuthFrame authFrame;
    private ChatFrame chatFrame;
    private final AuthService authService = new AuthService();

    public void start(String host, int port) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        authFrame = new AuthFrame(authService, () -> {
            chatFrame = new ChatFrame(SessionManager.INSTANCE.getSession().getUserId());
            chatFrame.setVisible(true);
            chatFrame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    stop();
                }
            });
        });
        authFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                stop();
            }
        });
        ConnectionManager.INSTANCE.connect(this, host, port);
        authFrame.setVisible(true);
    }

    public void stop() {
        ConnectionManager.INSTANCE.disconnect();
    }

    public static void main(String[] args) throws Exception {
        final AChatClient client = new AChatClient();
        client.start("localhost", 8080);
    }

    public AuthFrame getAuthFrame() {
        return authFrame;
    }

    public ChatFrame getChatFrame() {
        return chatFrame;
    }
}
