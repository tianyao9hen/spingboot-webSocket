package com.study.springboot.config;

import com.study.springboot.intercepter.WebSocketShakeIntercepter;
import com.study.springboot.webSocket.WebSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * webSocket endpoint的配置类
 * WebSocketConfiguration
 *
 * @author pxf
 * @version v1.0
 * @Date 2020-10-17
 */
@Configuration
@EnableWebSocket //启动spring webSocket
public class WebSocketConfiguration implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        //配置webSocket处理器
        registry.addHandler(this.webSocketHandler(),"/")
                .addInterceptors(new WebSocketShakeIntercepter()) //配置拦截器
                .setAllowedOrigins("*"); //允许跨域
    }

    @Bean
    public WebSocketHandler webSocketHandler(){
        return new WebSocketHandler();
    }

    @Bean
    public WebSocketShakeIntercepter webSocketShakeIntercepter(){
        return new WebSocketShakeIntercepter();
    }
}