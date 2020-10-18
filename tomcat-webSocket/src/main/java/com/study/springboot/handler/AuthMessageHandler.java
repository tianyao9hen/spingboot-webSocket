package com.study.springboot.handler;

import com.study.springboot.message.AuthRequest;
import com.study.springboot.message.AuthResponse;
import com.study.springboot.message.UserJoinNoticeRequest;
import com.study.springboot.util.WebSocketUtil;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.websocket.Session;

/**
 * 处理AuthRequest消息
 * AuthMessageHandler
 *
 * @author pxf
 * @version v1.0
 * @Date 2020-10-17
 */
@Component
public class AuthMessageHandler implements MessageHandler<AuthRequest> {

    @Override
    public void execute(Session session, AuthRequest message) {
        // 如果未传递 accessToken
        if (StringUtils.isEmpty(message.getAccessToken())) {
            AuthResponse authResponse = new AuthResponse();
            authResponse.setCode(1);
            authResponse.setMessage("认证 accessToken 未传入");
            WebSocketUtil.send(session, AuthResponse.TYPE, authResponse);
            return;
        }

        // 添加到 WebSocketUtil 中
        WebSocketUtil.addSession(session, message.getAccessToken()); // 考虑到代码简化，我们先直接使用 accessToken 作为 User

        // 判断是否认证成功。这里，假装直接成功
        AuthResponse authResponse = new AuthResponse();
        authResponse.setCode(0);
        WebSocketUtil.send(session, AuthResponse.TYPE, authResponse);

        // 通知所有人，某个人加入了。这个是可选逻辑，仅仅是为了演示
        UserJoinNoticeRequest userJoinNoticeRequest = new UserJoinNoticeRequest();
        userJoinNoticeRequest.setNickname(message.getAccessToken());
        WebSocketUtil.broadcast(UserJoinNoticeRequest.TYPE, userJoinNoticeRequest); // 考虑到代码简化，我们先直接使用 accessToken 作为 User
    }

    @Override
    public String getType() {
        return AuthRequest.TYPE;
    }
}