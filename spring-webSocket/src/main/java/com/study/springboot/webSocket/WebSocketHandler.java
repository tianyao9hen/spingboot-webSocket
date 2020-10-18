package com.study.springboot.webSocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.study.springboot.handler.MessageHandler;
import com.study.springboot.message.AuthRequest;
import com.study.springboot.message.Message;
import com.study.springboot.util.WebSocketUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * WebSocketHandler
 *
 * @author pxf
 * @version v1.0
 * @Date 2020-10-18
 */
public class WebSocketHandler extends TextWebSocketHandler implements InitializingBean {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * @Description 对应open 事件
     * @Param session
     * @Return void
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("[afterConnectionEstablished][session({}) 接入]", session);
        // 解析 accessToken
        String accessToken = (String) session.getAttributes().get("accessToken");
        // 创建 AuthRequest 消息类型
        AuthRequest authRequest = new AuthRequest();
        authRequest.setAccessToken(accessToken);
        // 获得消息处理器
        MessageHandler<AuthRequest> messageHandler = HANDLERS.get(AuthRequest.TYPE);
        if (messageHandler == null) {
            logger.error("[onOpen][认证消息类型，不存在消息处理器]");
            return;
        }
        messageHandler.execute(session, authRequest);
    }

    /**
     * @Description 对应message事件
     * @Param session
     * @Param message
     * @Return void
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {
        logger.info("[handleMessage][session({}) 接收到一条消息({})]", session, textMessage); // 生产环境下，请设置成 debug 级别
        try {
            // 获得消息类型
            JSONObject jsonMessage = JSON.parseObject(textMessage.getPayload());
            String messageType = jsonMessage.getString("type");
            // 获得消息处理器
            MessageHandler messageHandler = HANDLERS.get(messageType);
            if (messageHandler == null) {
                logger.error("[onMessage][消息类型({}) 不存在消息处理器]", messageType);
                return;
            }
            // 解析消息
            Class<? extends Message> messageClass = this.getMessageClass(messageHandler);
            // 处理消息
            Message messageObj = JSON.parseObject(jsonMessage.getString("body"), messageClass);
            messageHandler.execute(session, messageObj);
        } catch (Throwable throwable) {
            logger.info("[onMessage][session({}) message({}) 发生异常]", session, throwable);
        }
    }

    /**
     * @Description 对应close事件
     * @Param session
     * @Param status
     * @Return void
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        logger.info("[afterConnectionClosed][session({}) 连接关闭。关闭原因是({})}]", session, status);
        WebSocketUtil.removeSession(session);
    }

    /**
     * @Description 对应error事件
     * @Param session
     * @Param exception
     * @Return void
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        super.handleTransportError(session, exception);
    }


    /**
     * 消息类型与 MessageHandler 的映射
     *
     * 无需设置成静态变量
     */
    private final Map<String, MessageHandler> HANDLERS = new HashMap<>();

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        //通过ApplicationCOntext获得所有的MessageHandler Bean
        //获得MessageHandler Bean
        Collection<MessageHandler> messageHandlerList = applicationContext.getBeansOfType(MessageHandler.class).values(); // 获得所有 MessageHandler Bean
        messageHandlerList.forEach(messageHandler -> HANDLERS.put(messageHandler.getType(),messageHandler));
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