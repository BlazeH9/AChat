package cn.blazeh.achat.server.dao;

import cn.blazeh.achat.common.model.Message;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class MessageDao {

    public static final Map<Long, Message> MESSAGES = new ConcurrentHashMap<>();

    public static void insertMessage(Message message) {
        MESSAGES.put(message.getMessageId(), message);
    }

    public static Optional<Message> selectMessage(long id) {
        return Optional.ofNullable(MESSAGES.get(id));
    }

}
