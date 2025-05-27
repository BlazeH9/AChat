package cn.blazeh.achat.common.handler;

import cn.blazeh.achat.common.proto.MessageProto.AChatEnvelope;
import io.netty.channel.ChannelHandlerContext;

public interface AChatHandler {

    void handle(ChannelHandlerContext ctx, AChatEnvelope envelope);

}
