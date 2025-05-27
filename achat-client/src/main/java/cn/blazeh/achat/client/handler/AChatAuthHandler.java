package cn.blazeh.achat.client.handler;

import cn.blazeh.achat.client.manager.SessionManager;
import cn.blazeh.achat.client.model.Session;
import cn.blazeh.achat.common.handler.AChatHandler;
import cn.blazeh.achat.common.proto.MessageProto.*;
import io.netty.channel.ChannelHandlerContext;

import java.util.UUID;

public class AChatAuthHandler implements AChatHandler {

    @Override
    public void handle(ChannelHandlerContext ctx, AChatEnvelope envelope) {
        System.out.println("接收到验证响应");
        AChatAuth auth = envelope.getAuth();
        if(auth.getFlag()) {
            System.out.println("登录成功：" + auth.getFirst());
            SessionManager.INSTANCE.getSession().setAuthState(Session.AuthState.DONE);
            SessionManager.INSTANCE.getSession().setSessionId(UUID.fromString(auth.getSecond()));
        } else {
            System.out.println("登录失败：" + auth.getFirst());
            SessionManager.INSTANCE.getSession().setAuthState(Session.AuthState.READY);
        }
    }
}
