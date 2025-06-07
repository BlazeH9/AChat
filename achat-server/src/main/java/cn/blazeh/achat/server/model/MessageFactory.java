package cn.blazeh.achat.server.model;

import cn.blazeh.achat.common.model.Message;
import cn.blazeh.achat.common.proto.MessageProto;
import cn.blazeh.achat.server.util.IdGenerator;

public final class MessageFactory {

    public static Message.MessageBuilder newBuilder() {
        return Message.newBuilder(IdGenerator::nextId);
    }

    public static MessageProto.MessageType getType(int type) {
        return MessageProto.MessageType.forNumber(type);
    }

}
