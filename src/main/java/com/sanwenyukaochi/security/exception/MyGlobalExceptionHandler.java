package com.sanwenyukaochi.security.exception;

import com.sanwenyukaochi.security.interceptor.RequestIdInterceptor;
import com.sanwenyukaochi.security.vo.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class MyGlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Object> myMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(err -> {
            String fieldName = ((FieldError) err).getField();
            String message = err.getDefaultMessage();
            errors.put(fieldName, message);
        });
        return Result.error(400, "参数验证失败")
                .path(getCurrentRequestPath())
                .requestId(RequestIdInterceptor.getCurrentRequestId());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public Result<Object> myResourceNotFoundException(ResourceNotFoundException e) {
        String message = e.getMessage();
        return Result.error(404, message)
                .path(getCurrentRequestPath())
                .requestId(RequestIdInterceptor.getCurrentRequestId());
    }

    @ExceptionHandler(APIException.class)
    public Result<Object> myAPIException(APIException e) {
        String message = e.getMessage();
        return Result.error(400, message)
                .path(getCurrentRequestPath())
                .requestId(RequestIdInterceptor.getCurrentRequestId());
    }

    @ExceptionHandler(RequestRejectedException.class)
    public Result<?> handleRequestRejectedException(RequestRejectedException ex, HttpServletRequest request) {
        return Result.error(400, "非法请求路径: " + request.getRequestURI())
                .requestId(RequestIdInterceptor.getCurrentRequestId());
    }


    /**
     * 获取当前请求路径
     */
    private String getCurrentRequestPath() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                jakarta.servlet.http.HttpServletRequest request = attributes.getRequest();
                return request.getRequestURI();
            }
        } catch (Exception e) {
            // 忽略异常，避免影响正常业务
        }
        return null;
    }
}
