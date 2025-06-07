package cn.blazeh.achat.server.dao;

import cn.blazeh.achat.common.model.Message;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class MessageDao {

    public final Map<Long, Message> messages = new ConcurrentHashMap<>();

    public void insertMessage(Message message) {
        messages.put(message.getMessageId(), message);
    }

    public Optional<Message> selectMessage(long id) {
        return Optional.ofNullable(messages.get(id));
    }

}
