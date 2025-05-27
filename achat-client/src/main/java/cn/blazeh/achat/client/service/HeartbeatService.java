package cn.blazeh.achat.client.service;

import cn.blazeh.achat.common.proto.MessageProto.*;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class HeartbeatService extends ClientService {

    private void startHeartbeat() {
        Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(this::sendHeartbeat, 0, 30, TimeUnit.SECONDS);
    }

    public void sendHeartbeat() {
        writeAndFlush(getAChatEnvelopeBuilder()
                .setType(AChatType.HEARTBEAT)
                .setHeartbeat(AChatHeartbeat.getDefaultInstance())
        );
    }

}
