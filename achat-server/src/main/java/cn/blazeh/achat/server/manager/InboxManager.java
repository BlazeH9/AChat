package cn.blazeh.achat.server.manager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class InboxManager {

    private final Map<String, Set<Long>> messages = new ConcurrentHashMap<>();

    public void addMessage(String receiver, long messageId) {
        messages.computeIfAbsent(receiver, ignored -> new ConcurrentSkipListSet<>()).add(messageId);
    }

    public Set<Long> getMessages(String userId) {
        return messages.getOrDefault(userId, Collections.emptySet());
    }

    public void clearMessages(String userId) {
        messages.getOrDefault(userId, Collections.emptySet()).clear();
    }

}
