package cn.blazeh.achat.server.handler;

import cn.blazeh.achat.common.handler.AChatHandler;
import cn.blazeh.achat.common.proto.MessageProto.*;
import cn.blazeh.achat.server.service.AuthService;
import cn.blazeh.achat.server.service.ConnectionService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AChatAuthHandler implements AChatHandler {

    private static final Logger LOGGER = LogManager.getLogger(AChatAuthHandler.class);

    @Override
    public void handle(ChannelHandlerContext ctx, AChatEnvelope envelope) {
        LOGGER.debug("接收到验证请求：{}", ctx.channel().remoteAddress());
        AChatAuth auth = envelope.getAuth();
        Channel channel = ctx.channel();
        String userId = auth.getFirst(), password = auth.getSecond();
        if(auth.getFlag()) {
            if(AuthService.INSTANCE.registerAuth(userId, password)) {
                String sessionId = ConnectionService.INSTANCE.establish(userId, channel).toString();
                channel.writeAndFlush(AChatServerHandler.getEnvelopeBuilder()
                        .setType(AChatType.AUTH)
                        .setAuth(AChatAuth.newBuilder()
                                .setFlag(true)
                                .setFirst("注册成功")
                                .setSecond(sessionId)
                        )
                );
            } else {
                channel.writeAndFlush(AChatServerHandler.getEnvelopeBuilder()
                        .setType(AChatType.AUTH)
                        .setAuth(AChatAuth.newBuilder()
                                .setFlag(false)
                                .setFirst("注册失败，该用户名已被占用")
                                .setSecond("")
                        )
                        .build()
                );
            }
        } else {
            if(AuthService.INSTANCE.loginAuth(userId, password)) {
                String sessionId = ConnectionService.INSTANCE.establish(userId, channel).toString();
                channel.writeAndFlush(AChatServerHandler.getEnvelopeBuilder()
                        .setType(AChatType.AUTH)
                        .setAuth(AChatAuth.newBuilder()
                                .setFlag(true)
                                .setFirst("登录成功")
                                .setSecond(sessionId)
                        )
                );
            } else {
                channel.writeAndFlush(AChatServerHandler.getEnvelopeBuilder()
                        .setType(AChatType.AUTH)
                        .setAuth(AChatAuth.newBuilder()
                                .setFlag(false)
                                .setFirst("登陆失败，用户名或密码错误")
                                .setSecond("")
                        )
                        .build()
                );
            }
        }
    }

}