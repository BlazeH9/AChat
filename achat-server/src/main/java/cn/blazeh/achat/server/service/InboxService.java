package cn.blazeh.achat.server.service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public enum InboxService {

    INSTANCE;

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
