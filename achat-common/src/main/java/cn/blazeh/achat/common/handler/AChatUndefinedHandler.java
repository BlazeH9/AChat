package cn.blazeh.achat.common.handler;

import cn.blazeh.achat.common.proto.MessageProto.AChatEnvelope;
import io.netty.channel.ChannelHandlerContext;

public class AChatUndefinedHandler implements AChatHandler {

    @Override
    public void handle(ChannelHandlerContext ctx, AChatEnvelope envelope) {
        System.out.println("接收到未定义数据包，已忽略");
    }

}
