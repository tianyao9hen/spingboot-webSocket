package com.study.springboot.message;

import lombok.Data;

/**
 * SendToAllRequest
 * 发送消息给所有人
 *
 * @author pxf
 * @version v1.0
 * @Date 2020-10-17
 */
@Data
public class SendToAllRequest implements Message{

    public static final String TYPE = "SEND_TO_ALL_REQUEST";

    /**
     * 消息编号
     */
    private String msgId;
    /**
     * 内容
     */
    private String content;
}