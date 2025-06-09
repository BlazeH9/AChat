package cn.blazeh.achat.server.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 消息ID生成器
 */
public final class IdGenerator {

    private static final AtomicLong COUNTER = new AtomicLong(0);

    /**
     * 获取下一个消息ID
     * @return 下一个消息ID
     */
    public static long nextId() {
        return COUNTER.incrementAndGet();
    }

    /**
     * 设置消息ID初始值，注意该方法仅由DAO层调用
     * @param counter 计数器初始值
     */
    public static void setCounter(long counter) {
        COUNTER.set(counter);
    }

}
