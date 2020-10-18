package com.study.springboot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
// @EnableWebSocket // 无需添加该注解，因为我们并不是使用 Spring WebSocket
public class WebSocketConfiguration {

    //创建 ServerEndpointExporter Bean 。该 Bean 的作用，是扫描添加有 @ServerEndpoint 注解的 Bean 。
    @Bean
    public ServerEndpointExporter serverEndpointExporter(){
        return new ServerEndpointExporter();
    }
}