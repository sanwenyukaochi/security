package com.sanwenyukaochi.security.server.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;

@Slf4j
@Configuration
@EnableWebSocketSecurity
public class WebSocketSecurityConfig {

    @Bean
    public ChannelInterceptor csrfChannelInterceptor(){
        //disabling csrf
        return new ChannelInterceptor() {
        };
    }
    
    @Bean
    public AuthorizationManager<Message<?>> messageAuthorizationManager(
            MessageMatcherDelegatingAuthorizationManager.Builder messages) {
        log.info("配置WebSocket消息授权规则");
        messages
                .simpTypeMatchers(SimpMessageType.CONNECT).permitAll()    // 允许所有连接请求
                .simpTypeMatchers(SimpMessageType.DISCONNECT).permitAll() // 允许断开连接
                .simpTypeMatchers(SimpMessageType.HEARTBEAT).permitAll()  // 允许心跳消息
                .simpTypeMatchers(SimpMessageType.SUBSCRIBE).authenticated() // 订阅需要认证
                .simpTypeMatchers(SimpMessageType.UNSUBSCRIBE).authenticated() // 取消订阅需要认证
                .simpDestMatchers("/app/**").authenticated()  // 应用目标需要认证
                .simpDestMatchers("/topic/**").authenticated() // 主题需要认证
                .simpDestMatchers("/user/**").authenticated()  // 用户目标需要认证
                .anyMessage().denyAll();
        log.info("WebSocket消息授权规则配置完成");
        return messages.build();
    }
}
