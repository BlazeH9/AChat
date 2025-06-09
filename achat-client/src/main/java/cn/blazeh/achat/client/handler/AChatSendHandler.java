package cn.blazeh.achat.client.handler;

import cn.blazeh.achat.client.AChatClient;
import cn.blazeh.achat.client.service.MessageService;
import cn.blazeh.achat.common.handler.AChatHandler;
import cn.blazeh.achat.common.proto.MessageProto.*;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 聊天消息回执处理器
 */
public class AChatSendHandler implements AChatHandler {

    private static final Logger LOGGER = LogManager.getLogger(AChatSendHandler.class);

    private final AChatClient client;

    public AChatSendHandler(AChatClient client) {
        this.client = client;
    }

    @Override
    public void handle(ChannelHandlerContext ctx, AChatEnvelope envelope) {
        AChatSend send = envelope.getSend();
        if(send.getSuccess()) {
            MessageService.getInstance().saveTempMessage(send.getOrgMsgId(), send.getNewMsgId());
            LOGGER.info("消息发送成功");
        } else {
            MessageService.getInstance().removeTempMessage(send.getOrgMsgId());
            client.getChatFrame().showWarnMessage("消息发送失败："+send.getError());
            LOGGER.warn("消息发送失败：{}", send.getError());
        }
    }
}
