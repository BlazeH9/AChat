package cn.blazeh.achat.server.manager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class InboxManager {

    private final Map<String, ConcurrentLinkedQueue<Long>> messages = new ConcurrentHashMap<>();

    public void addMessage(String receiver, long messageId) {
        messages.computeIfAbsent(receiver, ignored -> new ConcurrentLinkedQueue<>()).add(messageId);
    }

    public List<Long> getMessages(String userId) {
        ConcurrentLinkedQueue<Long> queue = messages.get(userId);
        if(queue == null)
            return Collections.emptyList();
        return messages.get(userId).stream().toList();
    }

    public void clearMessages(String userId) {
        ConcurrentLinkedQueue<Long> queue = messages.get(userId);
        if(queue != null)
            queue.clear();
    }

}
