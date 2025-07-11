package cn.blazeh.achat.server.handler;

import cn.blazeh.achat.common.handler.AChatHandler;
import cn.blazeh.achat.common.proto.MessageProto.AChatEnvelope;
import io.netty.channel.ChannelHandlerContext;

/**
 * 心跳包处理器
 */
public class AChatHeartbeatHandler implements AChatHandler {

    @Override
    public void handle(ChannelHandlerContext ctx, AChatEnvelope envelope) {}

}
