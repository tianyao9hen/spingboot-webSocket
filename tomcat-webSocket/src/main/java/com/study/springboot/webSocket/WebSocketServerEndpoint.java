package com.study.springboot.webSocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.study.springboot.handler.MessageHandler;
import com.study.springboot.message.AuthRequest;
import com.study.springboot.message.Message;
import com.study.springboot.util.WebSocketUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;


/**
 * WebSocketServerEndpoint
 * 定义webSocket服务的端点
 * @author pxf
 * @version v1.0
 * @Date 2020-10-17
 */

@Controller
@ServerEndpoint("/") //标记为一个webSocket的端点，路径为/
public class WebSocketServerEndpoint implements InitializingBean {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * @Description 重新实现 #onOpen(Session session, EndpointConfig config) 方法，
     * 实现连接时，使用 accessToken 参数进行用户认证。代码如下
     *
     * 握手成功后调用
     * @Param session
     * @Param config
     * @Return void
     */
    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        logger.info("[onOpen][session({}) 接入]", session);
        // 解析 accessToken
        List<String> accessTokenValues = session.getRequestParameterMap().get("accessToken");
        String accessToken = !CollectionUtils.isEmpty(accessTokenValues)?accessTokenValues.get(0) : null;
        // 创建 AuthRequest 消息类型
        AuthRequest authRequest = new AuthRequest();
        authRequest.setAccessToken(accessToken);
        // 获取消息处理器
        MessageHandler<AuthRequest> messageHandler = HANDLERS.get(AuthRequest.TYPE);
        if(messageHandler == null){
            logger.error("[onOpen][认证消息类型，不存在消息处理器]");
            return;
        }
        messageHandler.execute(session,authRequest);
    }

    /**
     * @Description 获得推送到服务端的消息后调用
     * @Param session
     * @Param message
     * @Return void
     */
    @OnMessage
    public void onMessage(Session session, String message) {
        logger.info("[onOpen][session({}) 接收到一条消息({})]", session, message); // 生产环境下，请设置成 debug 级别
        try{
            //获得消息类型
            JSONObject jsonMessage = JSON.parseObject(message);
            String messageType = jsonMessage.getString("type");
            //获得消息处理器
            MessageHandler messageHandler = HANDLERS.get(messageType);
            if(messageHandler == null){
                logger.error("[onMessage][消息类型({}) 不存在消息处理器]", messageType);
                return;
            }
            //解析消息
            Class<? extends Message> messageClass = this.getMessageClass(messageHandler);
            //处理消息
            Message messageObj = JSON.parseObject(jsonMessage.getString("body"), messageClass);
            messageHandler.execute(session,messageObj);
        }catch(Exception e){
            logger.info("[onMessage][session({}) message({}) 发生异常]", session, e);
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        logger.info("[onClose][session({}) 连接关闭。关闭原因是({})}]", session, closeReason);
        WebSocketUtil.removeSession(session);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        logger.info("[onClose][session({}) 发生异常]", session, throwable);
    }

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * @Description 消息类型与MessageHandler的映射
     *      注意：这里设置成静态变量，虽然说WebSocketServerEndpoint是单例，但是SpringBoot还是会为每一个WebSocket创建一个WebSocketServerEndPoint Bean
     * @Param null
     * @Return
     */
    private static final Map<String,MessageHandler> HANDLERS = new HashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        //通过ApplicationCOntext获得所有的MessageHandler Bean
        //获得MessageHandler Bean
        Collection<MessageHandler> messageHandlerList = applicationContext.getBeansOfType(MessageHandler.class).values(); // 获得所有 MessageHandler Bean
        messageHandlerList.forEach(messageHandler -> HANDLERS.put(messageHandler.getType(),messageHandler));         messageHandlerList.forEach(messageHandler -> HANDLERS.put(messageHandler.getType(),messageHandler));
        logger.info("[afterPropertiesSet][消息处理器数量：{}]", HANDLERS.size());
    }

    private Class<? extends Message> getMessageClass(MessageHandler handler){
        // 获得Bean对应的class类名，因为可能被aop代理过
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(handler);
        // 获得接口的Type数组
        Type[] interfaces = targetClass.getGenericInterfaces();
        Class<?> superclass = targetClass.getSuperclass();
        while((Objects.isNull(interfaces) || 0 == interfaces.length) && Objects.nonNull(superclass)){// 此处，是以父类的接口为准
            interfaces = superclass.getGenericInterfaces();
            superclass = targetClass.getSuperclass();
        }
        if(Objects.nonNull(interfaces)){
            //遍历interfaces 数组
            for(Type type : interfaces){
                //要求 type是泛型参数
                if(type instanceof ParameterizedType){
                    ParameterizedType parameterizedType = (ParameterizedType) type;
                    //要求是MessageHandler接口
                    if(Objects.equals(parameterizedType.getRawType(),MessageHandler.class)){
                        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                        //取首个元素
                        if(Objects.nonNull(actualTypeArguments) && actualTypeArguments.length > 0){
                            return (Class<Message>)actualTypeArguments[0];
                        }else{
                            throw new IllegalStateException(String.format("类型(%s) 获得不到消息类型", handler));
                        }
                    }
                }
            }
        }
        throw new IllegalStateException(String.format("类型(%s) 获得不到消息类型", handler));
    }
}