package cn.blazeh.achat.client.service;

import cn.blazeh.achat.client.model.Session;
import cn.blazeh.achat.common.proto.MessageProto.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AuthService extends ClientService {

    private static final Logger LOGGER = LogManager.getLogger(AuthService.class);

    public void sendAuthRequest(String userId, String password, boolean register) {
        switch(getSession().getAuthState()) {
            case PREPARING -> LOGGER.info("尚未连接服务器，请稍后再试");
            case PENDING -> LOGGER.info("正在等待服务器验证响应，请勿重复发送验证请求");
            case DONE -> LOGGER.info("已登录成功，请勿重登录");
        }
        if(!getSession().getAuthState().equals(Session.AuthState.READY))
            return;
        writeAndFlush(getAChatEnvelopeBuilder()
                .setType(AChatType.AUTH)
                .setAuth(AChatAuth.newBuilder()
                        .setFlag(register)
                        .setFirst(userId)
                        .setSecond(password)
                )
        );
        getSession().setAuthState(Session.AuthState.PENDING);
        getSession().setUserId(userId);
        LOGGER.debug("已发送验证请求");
    }

}
