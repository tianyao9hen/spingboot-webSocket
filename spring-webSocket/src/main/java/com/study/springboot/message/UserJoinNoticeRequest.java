package com.study.springboot.message;

import lombok.Data;

/**
 * 广播用户加入群聊通知
 * UserJoinNoticeRequest
 *
 * @author pxf
 * @version v1.0
 * @Date 2020-10-17
 */
@Data
public class UserJoinNoticeRequest implements Message {

    public static final String TYPE = "USER_JOIN_NOTICE_REQUEST";

    /**
     * 昵称
     */
    private String nickname;
}