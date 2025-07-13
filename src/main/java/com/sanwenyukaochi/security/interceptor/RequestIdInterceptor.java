package com.sanwenyukaochi.security.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

/**
 * 请求ID拦截器
 * 自动生成请求ID并设置到ThreadLocal中
 */
@Component
public class RequestIdInterceptor implements HandlerInterceptor {

    private static final ThreadLocal<String> REQUEST_ID = new ThreadLocal<>();
    private static final String REQUEST_ID_HEADER = "X-Request-ID";

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        // 优先从请求头获取请求ID，如果没有则生成新的
        String requestId = request.getHeader(REQUEST_ID_HEADER);
        if (requestId == null || requestId.trim().isEmpty()) {
            requestId = generateRequestId();
        }
        
        REQUEST_ID.set(requestId);
        
        // 将请求ID设置到响应头中，便于前端获取
        response.setHeader(REQUEST_ID_HEADER, requestId);
        
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex) {
        // 清理ThreadLocal，避免内存泄漏
        REQUEST_ID.remove();
    }

    /**
     * 生成请求ID
     */
    private String generateRequestId() {
        return "req-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * 获取当前请求ID
     */
    public static String getCurrentRequestId() {
        return REQUEST_ID.get();
    }
} 