package cn.blazeh.achat.client;

import cn.blazeh.achat.client.cli.InputHandler;
import cn.blazeh.achat.client.manager.ConnectionManager;
import cn.blazeh.achat.client.manager.SessionManager;
import cn.blazeh.achat.client.model.Session;
import cn.blazeh.achat.client.service.AuthService;
import cn.blazeh.achat.client.service.ChatService;
import io.netty.channel.ChannelFuture;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.Scanner;

public class AChatClient {

    private static final Logger LOGGER = LogManager.getLogger(AChatClient.class);

    private final AuthService auth = new AuthService();
    private final ChatService chat = new ChatService();

    public Optional<ChannelFuture> start(String host, int port) throws Exception {
        return ConnectionManager.INSTANCE.connect(host, port);
    }

    public void stop() {
        ConnectionManager.INSTANCE.disconnect();
    }

    public void processInput() {
        new InputHandler(chat, System.in).run();
    }

    public void authenticate(String userId, String password) {
        auth.sendAuthRequest(userId, password, false);
    }

    public static void main(String[] args) throws Exception {
        final AChatClient client = new AChatClient();
        client.start("localhost", 8080);
        Scanner scanner = new Scanner(System.in);
        LOGGER.info("正在等待用户登录……");
        while(!SessionManager.INSTANCE.getSession().getAuthState().equals(Session.AuthState.DONE)) {
            if(SessionManager.INSTANCE.getSession().getAuthState().equals(Session.AuthState.READY)) {
                String userId = scanner.nextLine();
                String password = scanner.nextLine();
                client.authenticate(userId, password);
            }
            Thread.sleep(3000);
        }
        client.processInput();
        scanner.close();
        client.stop();
    }

}
