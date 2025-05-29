package cn.blazeh.achat.client.handler;

import cn.blazeh.achat.common.handler.AChatHandler;
import cn.blazeh.achat.common.proto.MessageProto.*;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AChatChatHandler implements AChatHandler {

    private static final Logger LOGGER = LogManager.getLogger(AChatChatHandler.class);

    @Override
    public void handle(ChannelHandlerContext ctx, AChatEnvelope envelope) {
        AChatChat msg = envelope.getChat();
        LOGGER.info("<{}> {}", msg.getSenderId(), msg.getContent());
    }
}
