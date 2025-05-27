package cn.blazeh.achat.client.service;

import cn.blazeh.achat.client.model.Session;
import cn.blazeh.achat.common.proto.MessageProto.*;

public class AuthService extends ClientService {

    public void sendAuthRequest(String userId, String password, boolean register) {
        switch(getSession().getAuthState()) {
            case PREPARING -> System.out.println("尚未连接服务器，请稍后再试");
            case PENDING -> System.out.println("正在等待服务器验证响应，请勿重复发送验证请求");
            case DONE -> System.out.println("已登录成功，请勿重登录");
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
        System.out.println("已发送验证请求");
    }

}
