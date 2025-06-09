package cn.blazeh.achat.common.handler;

import cn.blazeh.achat.common.proto.MessageProto.AChatEnvelope;
import io.netty.channel.ChannelHandlerContext;

/**
 * AChat数据包处理器
 */
public interface AChatHandler {

    /**
     * 处理channel中的数据
     * @param ctx channel处理器上下文
     * @param envelope AChat数据包内容
     */
    void handle(ChannelHandlerContext ctx, AChatEnvelope envelope);

}
