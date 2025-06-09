package cn.blazeh.achat.common.handler;

import cn.blazeh.achat.common.proto.MessageProto.AChatEnvelope;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 未定义数据包处理器
 */
public class AChatUndefinedHandler implements AChatHandler {

    private static final Logger LOGGER = LogManager.getLogger(AChatUndefinedHandler.class);

    @Override
    public void handle(ChannelHandlerContext ctx, AChatEnvelope envelope) {
        LOGGER.warn("接收到未定义数据包，已忽略");
    }

}
