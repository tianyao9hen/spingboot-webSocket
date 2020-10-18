package com.study.springboot.message;

import lombok.Data;

/**
 * 用户认证响应
 * AuthResponse
 *
 * @author pxf
 * @version v1.0
 * @Date 2020-10-17
 */
@Data
public class AuthResponse implements Message {

    public static final String TYPE = "AUTH_RESPONSE";

    /**
     * 响应状态码
     */
    private Integer code;
    /**
     * 响应提示
     */
    private String message;
}