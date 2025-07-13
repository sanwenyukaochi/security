package com.sanwenyukaochi.security.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanwenyukaochi.security.vo.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class AccessDeniedHandlerJwt implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException {
        log.error("权限不足: {}", accessDeniedException.getMessage());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        // 获取请求ID，优先从请求头获取，否则生成新的
        String requestId = request.getHeader("X-Request-ID");
        if (requestId == null || requestId.trim().isEmpty()) {
            requestId = "req-" + System.currentTimeMillis() + "-" + java.util.UUID.randomUUID().toString().substring(0, 8);
        }

        // 使用统一的Result格式
        Result<String> result = Result.error(403, "权限不足", "")
                .path(request.getRequestURI())
                .requestId(requestId);

        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), result);
    }
} 