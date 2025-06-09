package cn.blazeh.achat.server.model;

import cn.blazeh.achat.common.model.Message;
import cn.blazeh.achat.common.proto.MessageProto;
import cn.blazeh.achat.server.util.IdGenerator;

/**
 * 消息对象工厂类，提供消息构建和类型转换工具方法
 */
public final class MessageFactory {

    /**
     * 创建新的消息构建器实例
     * @return 消息构建器
     */
    public static Message.MessageBuilder newBuilder() {
        return Message.newBuilder(IdGenerator::nextId);
    }

    /**
     * 将数字类型转换为消息类型枚举
     * @param type 消息类型数字值
     * @return 对应的消息类型枚举对象
     */
    public static MessageProto.MessageType getType(int type) {
        return MessageProto.MessageType.forNumber(type);
    }
}