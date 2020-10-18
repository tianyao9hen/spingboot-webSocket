package com.study.springboot.message;

import lombok.Data;

/**
 * SendToOneRequest
 * 发送消息给指定人
 *
 * @author pxf
 * @version v1.0
 * @Date 2020-10-17
 */
@Data
public class SendToOneRequest implements Message {

    public static final String TYPE = "SEND_TO_ONE_REQUEST";

    /**
     * 发送给的用户
     */
    private String toUser;
    /**
     * 消息编号
     */
    private String msgId;
    /**
     * 内容
     */
    private String content;
}