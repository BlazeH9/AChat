package cn.blazeh.achat.client.service;

import cn.blazeh.achat.client.manager.MessageManager;
import cn.blazeh.achat.client.model.MessageFactory;
import cn.blazeh.achat.common.model.Message;
import cn.blazeh.achat.common.proto.MessageProto.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MessageService extends ClientService {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final Logger LOGGER = LogManager.getLogger(MessageService.class);
    private final Map<Long, Message> tempMessages = new ConcurrentHashMap<>();

    private static final MessageService INSTANCE = new MessageService();

    private MessageService() {}

    public static MessageService getInstance() {
        return INSTANCE;
    }

    public Message newTempMessage(String receiverId, String content) {
        Message message = MessageFactory.newBuilder()
                .setTimestamp(System.currentTimeMillis())
                .setSender(getSession().getUserId())
                .setReceiver(receiverId)
                .setType(MessageType.TEXT)
                .setContent(content)
                .build();
        addTempMessage(message);
        return message;
    }

    public void addTempMessage(Message message) {
        tempMessages.put(message.getMessageId(), message);
    }

    public void removeTempMessage(long messageId) {
        tempMessages.remove(messageId);
    }

    public void saveTempMessage(long orgMsgId, long newMsgId) {
        Message msg = tempMessages.get(orgMsgId);
        if(msg == null)
            return;
        boolean result = MessageManager.INSTANCE.saveMessage(MessageFactory.newBuilder()
                .setMessageId(newMsgId)
                .setTimestamp(msg.getTimestamp())
                .setSender(msg.getSender())
                .setReceiver(msg.getReceiver())
                .setType(msg.getType())
                .setContent(msg.getContent())
                .build()
        );
        if(result) {
            tempMessages.remove(orgMsgId);
            LOGGER.debug("消息已保存，原ID：{}，新ID：{}", orgMsgId, newMsgId);
        } else {
            LOGGER.warn("消息保存失败，ID：{}", orgMsgId);
        }
    }

    public static String parseTimestamp(long timestamp) {
        return DATE_FORMAT.format(new Date(timestamp));
    }

}
