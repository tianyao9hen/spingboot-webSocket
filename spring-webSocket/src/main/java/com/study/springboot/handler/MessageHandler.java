package com.study.springboot.handler;

import com.study.springboot.message.Message;
import org.springframework.web.socket.WebSocketSession;

import javax.websocket.Session;

/**
 * MessageHandler
 * 消息处理器接口
 *
 * @author pxf
 * @version v1.0
 * @Date 2020-10-17
 */
public interface MessageHandler<T extends Message> {

    /**
     * @Description 执行处理消息
     * @Param session 会话
     * @Param message 消息
     * @Return void
     */
    void execute(WebSocketSession session, T message);

    /**
     * @Description 消息类型，即每个Message实现类上的Type静态字段
     * @Return java.lang.String
     */
    String getType();
}