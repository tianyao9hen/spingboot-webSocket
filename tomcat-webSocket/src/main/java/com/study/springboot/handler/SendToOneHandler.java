package com.study.springboot.handler;

import com.study.springboot.message.SendResponse;
import com.study.springboot.message.SendToOneRequest;
import com.study.springboot.message.SendToUserRequest;
import com.study.springboot.util.WebSocketUtil;
import org.springframework.stereotype.Component;

import javax.websocket.Session;

/**
 * 处理SendToOneRequest
 * SendToOneHandler
 *
 * @author pxf
 * @version v1.0
 * @Date 2020-10-17
 */
@Component
public class SendToOneHandler implements MessageHandler<SendToOneRequest> {

    @Override
    public void execute(Session session, SendToOneRequest message) {
        // 这里，假装直接成功
        SendResponse sendResponse = new SendResponse();
        sendResponse.setMsgId(message.getMsgId());
        sendResponse.setCode(0);
        WebSocketUtil.send(session, SendResponse.TYPE, sendResponse);

        // 创建转发的消息
        SendToUserRequest sendToUserRequest = new SendToUserRequest();
        sendToUserRequest.setMsgId(message.getMsgId());
        sendToUserRequest.setContent(message.getContent());
        // 广播发送
        WebSocketUtil.send(message.getToUser(), SendToUserRequest.TYPE, sendToUserRequest);
    }

    @Override
    public String getType() {
        return SendToOneRequest.TYPE;
    }
}