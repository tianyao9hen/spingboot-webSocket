package com.study.springboot.message;

import lombok.Data;

/**
 * 消息发送结果响应
 * SendResponse
 *
 * @author pxf
 * @version v1.0
 * @Date 2020-10-17
 */
@Data
public class SendResponse implements Message {

    public static final String TYPE = "SEND_RESPONSE";

    /**
     * 消息编号
     */
    private String msgId;
    /**
     * 响应状态码
     */
    private Integer code;
    /**
     * 响应提示
     */
    private String message;
}