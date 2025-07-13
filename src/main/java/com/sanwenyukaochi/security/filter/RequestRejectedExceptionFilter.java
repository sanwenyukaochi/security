package com.sanwenyukaochi.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanwenyukaochi.security.vo.Result;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(1)
public class RequestRejectedExceptionFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestRejectedExceptionFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            logger.info("RequestRejectedExceptionFilter 处理请求: {}", request.getRequestURI());
            filterChain.doFilter(request, response);
        } catch (RequestRejectedException e) {
            logger.warn("请求被拒绝: {}", e.getMessage());
            
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            // 获取请求ID，优先从请求头获取，否则生成新的
            String requestId = request.getHeader("X-Request-ID");
            if (requestId == null || requestId.trim().isEmpty()) {
                requestId = "req-" + System.currentTimeMillis() + "-" + java.util.UUID.randomUUID().toString().substring(0, 8);
            }

            // 使用统一的Result格式
            Result<Object> result = Result.error(400, "非法请求路径")
                    .path(request.getRequestURI())
                    .requestId(requestId);

            final ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(response.getOutputStream(), result);
        } catch (Exception e) {
            logger.error("RequestRejectedExceptionFilter 捕获到未预期的异常: {}", e.getMessage(), e);
            throw e;
        }
    }
} 