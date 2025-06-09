package cn.blazeh.achat.server.manager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 信箱Manager，负责离线消息的处理与维护
 */
public class InboxManager {

    private final Map<String, ConcurrentLinkedQueue<Long>> messages = new ConcurrentHashMap<>();

    /**
     * 为receiver添加一条离线消息
     * @param receiver 接收方
     * @param messageId 消息ID
     */
    public void addMessage(String receiver, long messageId) {
        messages.computeIfAbsent(receiver, ignored -> new ConcurrentLinkedQueue<>()).add(messageId);
    }

    /**
     * 获取用户所有的离线消息
     * @param userId 用户ID
     * @return 对应的所有消息ID
     */
    public List<Long> getMessages(String userId) {
        ConcurrentLinkedQueue<Long> queue = messages.get(userId);
        if(queue == null)
            return Collections.emptyList();
        return messages.get(userId).stream().toList();
    }

    /**
     * 清空指定用户的信箱
     * @param userId 用户ID
     */
    public void clearMessages(String userId) {
        ConcurrentLinkedQueue<Long> queue = messages.get(userId);
        if(queue != null)
            queue.clear();
    }

}
