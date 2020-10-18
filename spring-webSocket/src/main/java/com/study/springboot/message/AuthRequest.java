package com.study.springboot.message;

import lombok.Data;

/**
 * 用户认证请求
 * AuthRequest
 *
 * @author pxf
 * @version v1.0
 * @Date 2020-10-17
 */
@Data
public class AuthRequest implements Message {

    public static final String TYPE = "AUTH_REQUEST";

    private String accessToken;
}