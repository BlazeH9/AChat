package cn.blazeh.achat.client.service;

import cn.blazeh.achat.common.proto.MessageProto.*;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 心跳服务，定时给服务器发送心跳包
 */
public class HeartbeatService extends ClientService {

    /**
     * 启动心跳包定时发送服务
     */
    private void startHeartbeat() {
        Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(this::sendHeartbeat, 0, 30, TimeUnit.SECONDS);
    }

    /**
     * 发送心跳包
     */
    public void sendHeartbeat() {
        writeAndFlush(getAChatEnvelopeBuilder()
                .setType(AChatType.HEARTBEAT)
                .setHeartbeat(AChatHeartbeat.getDefaultInstance())
        );
    }

}
