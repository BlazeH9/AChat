package cn.blazeh.achat.server.util;

import java.util.concurrent.atomic.AtomicLong;

public final class IdGenerator {

    private static final AtomicLong COUNTER = new AtomicLong(0);

    public static long nextId() {
        return COUNTER.incrementAndGet();
    }

    public static void setCounter(long counter) {
        COUNTER.set(counter);
    }

}
