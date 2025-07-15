package com.sanwenyukaochi.security.security.config;

import com.sanwenyukaochi.security.vo.Result;
import lombok.NonNull;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class ResultStatusAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(@NonNull MethodParameter returnType, @NonNull Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, @NonNull MethodParameter returnType, @NonNull MediaType selectedContentType,
                                  @NonNull Class selectedConverterType, @NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response) {
        if (body instanceof Result<?> result) {
            int code = result.getCode() != null ? result.getCode() : 200;
            // 只在非ResponseEntity包装时生效
            try {
                response.setStatusCode(HttpStatus.valueOf(code));
            } catch (Exception ignored) {
                response.setStatusCode(HttpStatus.OK);
            }
        }
        return body;
    }
}