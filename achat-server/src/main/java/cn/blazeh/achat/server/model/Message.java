package cn.blazeh.achat.server.model;

import cn.blazeh.achat.common.proto.MessageProto.*;
import cn.blazeh.achat.server.util.IdGenerator;

public class Message {

    private final long messageId, timestamp;
    private final String sender, receiver;
    private final MessageType type;
    private final String content;

    private Message(MessageBuilder builder) {
        this.messageId = builder.messageId;
        this.sender = builder.sender;
        this.receiver = builder.receiver;
        this.timestamp = builder.timestamp;
        this.type = builder.type;
        this.content = builder.content;
    }

    public long getMessageId() {
        return messageId;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public MessageType getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public AChatChat toAChatChat() {
        return AChatChat.newBuilder()
                .setMessageId(getMessageId())
                .setTimestamp(getTimestamp())
                .setSenderId(getSender())
                .setReceiverId(getReceiver())
                .setType(getType())
                .setContent(getContent())
                .build();
    }

    public static class MessageBuilder {

        private long messageId, timestamp;
        private String sender, receiver;
        private MessageType type;
        private String content;

        public MessageBuilder() {
            messageId = -1;
            this.timestamp = System.currentTimeMillis();
        }

        public MessageBuilder setMessageId(long messageId) {
            this.messageId = messageId;
            return this;
        }

        public MessageBuilder setSender(String sender) {
            this.sender = sender;
            return this;
        }

        public MessageBuilder setReceiver(String receiver) {
            this.receiver = receiver;
            return this;
        }

        public MessageBuilder setTimestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public MessageBuilder setType(MessageType type) {
            this.type = type;
            return this;
        }

        public MessageBuilder setContent(String content) {
            this.content = content;
            return this;
        }

        public String getSender() {
            return sender;
        }

        public String getReceiver() {
            return receiver;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public MessageType getType() {
            return type;
        }

        public String getContent() {
            return content;
        }

        public Message build() {
            if(this.messageId < 0)
                this.messageId = IdGenerator.nextId();
            return new Message(this);
        }

    }

    public static MessageBuilder newBuilder() {
        return new MessageBuilder();
    }

}
