package com.study.springboot.intercepter;

import org.apache.catalina.Server;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.Map;

/**
 * 拦截器拦截ws请求中的param
 * WebSocketShakeIntercepter
 * 自定义 HttpSessionHandshakeInterceptor 拦截器
 * 因为 WebSocketSession 无法获得 ws 地址上的请求参数，所以只好通过该拦截器，获得 accessToken 请求参数，设置到 attributes 中
 *
 * @author pxf
 * @version v1.0
 * @Date 2020-10-18
 */
public class WebSocketShakeIntercepter extends HttpSessionHandshakeInterceptor{

    /**
     * @Description 拦截握手事件
     * @Param request
     * @Param response
     * @Param wsHandler
     * @Param attributes
     * @Return boolean
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        //获得accessToken
        if(request instanceof ServletServerHttpRequest){
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            attributes.put("accessToken",servletRequest.getServletRequest().getParameter("accessToken"));
        }
        //调用父方法，继续执行逻辑
        return super.beforeHandshake(request,response,wsHandler,attributes);
    }
}