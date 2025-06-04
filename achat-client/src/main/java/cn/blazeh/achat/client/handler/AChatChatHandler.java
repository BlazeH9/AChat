package cn.blazeh.achat.client.handler;

import cn.blazeh.achat.common.handler.AChatHandler;
import cn.blazeh.achat.common.proto.MessageProto.*;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AChatChatHandler implements AChatHandler {

    private static final Logger LOGGER = LogManager.getLogger(AChatChatHandler.class);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void handle(ChannelHandlerContext ctx, AChatEnvelope envelope) {
        AChatChat msg = envelope.getChat();
        LOGGER.info("[{}]<{}> {}", DATE_FORMAT.format(new Date(msg.getTimestamp())), msg.getSenderId(), msg.getContent());
    }
}
