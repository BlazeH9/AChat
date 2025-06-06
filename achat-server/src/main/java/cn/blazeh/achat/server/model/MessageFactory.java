package cn.blazeh.achat.server.model;

import cn.blazeh.achat.common.model.Message;
import cn.blazeh.achat.server.util.IdGenerator;

public class MessageFactory {

    public static Message.MessageBuilder newBuilder() {
        return Message.newBuilder(IdGenerator::nextId);
    }

}
