package com.study.springboot.handler;

import com.study.springboot.message.SendResponse;
import com.study.springboot.message.SendToAllRequest;
import com.study.springboot.message.SendToUserRequest;
import com.study.springboot.util.WebSocketUtil;
import org.springframework.stereotype.Component;

import javax.websocket.Session;

/**
 * 处理SendToAllRequest消息
 * SendToAllHandler
 *
 * @author pxf
 * @version v1.0
 * @Date 2020-10-17
 */
@Component
public class SendToAllHandler implements MessageHandler<SendToAllRequest> {

    @Override
    public void execute(Session session, SendToAllRequest message) {
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
        WebSocketUtil.broadcast(SendToUserRequest.TYPE, sendToUserRequest);
    }

    @Override
    public String getType() {
        return SendToAllRequest.TYPE;
    }
}