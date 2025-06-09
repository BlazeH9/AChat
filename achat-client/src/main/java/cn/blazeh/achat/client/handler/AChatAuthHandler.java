package cn.blazeh.achat.client.handler;

import cn.blazeh.achat.client.AChatClient;
import cn.blazeh.achat.client.manager.SessionManager;
import cn.blazeh.achat.client.model.Session;
import cn.blazeh.achat.common.handler.AChatHandler;
import cn.blazeh.achat.common.proto.MessageProto.*;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

/**
 * 用户认证处理器
 */
public class AChatAuthHandler implements AChatHandler {

    private static final Logger LOGGER = LogManager.getLogger(AChatAuthHandler.class);

    private final AChatClient client;

    public AChatAuthHandler(AChatClient client) {
        this.client = client;
    }

    @Override
    public void handle(ChannelHandlerContext ctx, AChatEnvelope envelope) {
        LOGGER.debug("接收到验证响应");
        AChatAuth auth = envelope.getAuth();
        if(auth.getFlag()) {
            LOGGER.info("登录成功：{}", auth.getFirst());
            SessionManager.INSTANCE.getSession().setAuthState(Session.AuthState.DONE);
            SessionManager.INSTANCE.getSession().setSessionId(UUID.fromString(auth.getSecond()));
            client.getAuthFrame().authSuccess(auth.getFirst());
        } else {
            LOGGER.info("登录失败：{}", auth.getFirst());
            SessionManager.INSTANCE.getSession().setAuthState(Session.AuthState.READY);
            client.getAuthFrame().authFailed(auth.getFirst());
        }
    }
}
