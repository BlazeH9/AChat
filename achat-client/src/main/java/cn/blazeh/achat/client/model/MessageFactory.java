package cn.blazeh.achat.client.model;

import cn.blazeh.achat.common.model.Message;
import cn.blazeh.achat.common.proto.MessageProto;

import java.util.concurrent.atomic.AtomicLong;

public final class MessageFactory {

    private static final AtomicLong COUNTER = new AtomicLong(-1);

    public static Message.MessageBuilder newBuilder() {
        return new Message.MessageBuilder(COUNTER::getAndDecrement);
    }

    public static MessageProto.MessageType getType(int type) {
        return MessageProto.MessageType.forNumber(type);
    }

}
