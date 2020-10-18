package com.study.springboot.message;

import lombok.Data;

/**
 * SendToUserRequest
 * 在服务端接收到发送消息的请求，需要转发消息给对应的人。
 *
 * @author pxf
 * @version v1.0
 * @Date 2020-10-17
 */
@Data
public class SendToUserRequest implements Message{

    public static final String TYPE = "SEND_TO_USER_REQUEST";

    /**
     * 消息编号
     */
    private String msgId;
    /**
     * 内容
     */
    private String content;
}