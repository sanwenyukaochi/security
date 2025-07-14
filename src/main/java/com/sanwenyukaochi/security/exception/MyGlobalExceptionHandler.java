package com.sanwenyukaochi.security.exception;

import cn.hutool.http.HttpStatus;
import com.sanwenyukaochi.security.security.exception.CustomAuthenticationException;
import com.sanwenyukaochi.security.security.filter.RequestCorrelationIdFilter;
import com.sanwenyukaochi.security.vo.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class MyGlobalExceptionHandler {

    // TODO 参数校验异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Object> myMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return Result.error(HttpStatus.HTTP_BAD_REQUEST, "参数验证失败")
                .path(getCurrentRequestPath())
                .requestId(getRequestId());
    }

    // TODO 资源找不到异常
    @ExceptionHandler(ResourceNotFoundException.class)
    public Result<Object> myResourceNotFoundException(ResourceNotFoundException e) {
        String message = e.getMessage();
        return Result.error(HttpStatus.HTTP_NOT_FOUND, message)
                .path(getCurrentRequestPath())
                .requestId(getRequestId());
    }

    // TODO 可以考虑是否删除，如果删除删除对应的 APIException.class
    @ExceptionHandler(APIException.class)
    public Result<Object> myAPIException(APIException e) {
        String message = e.getMessage();
        return Result.error(HttpStatus.HTTP_BAD_REQUEST, message)
                .path(getCurrentRequestPath())
                .requestId(getRequestId());
    }

    @ExceptionHandler(CustomAuthenticationException.class)
    public Result<Object> handleCustomAuthenticationException(CustomAuthenticationException e) {
        return Result.error(e.getHttpStatus(), switch (e.getErrorCode()) {
            case "USER_NOT_FOUND" -> "用户不存在";
            case "INVALID_PASSWORD" -> "密码错误";
            case "DISABLED" -> "账户已被禁用";
            case "LOCKED" -> "账户已被锁定";
            case "EXPIRED" -> "账户已过期";
            case "CREDENTIALS_EXPIRED" -> "凭据已过期";
            default -> "认证失败";
        })
                .path(getCurrentRequestPath())
                .requestId(getRequestId());
    }

    // TODO 项目交给security处理了，上面有一个自定义的 CustomAuthenticationException.class 这个403处理可以考虑是否删除
    @ExceptionHandler(AccessDeniedException.class)
    public Result<Object> handleAccessDeniedException(AccessDeniedException e) {
        return Result.error(HttpStatus.HTTP_FORBIDDEN, "权限不足")
                .path(getCurrentRequestPath())
                .requestId(getRequestId());
    }

    @ExceptionHandler(RequestRejectedException.class)
    public Result<Object> handleRequestRejectedException(RequestRejectedException e) {
        return Result.error(HttpStatus.HTTP_BAD_REQUEST, "非法请求路径")
                .path(getCurrentRequestPath())
                .requestId(getRequestId());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public Result<Object> handleNoHandlerFoundException(NoHandlerFoundException e) {
        return Result.error(HttpStatus.HTTP_NOT_FOUND, "接口不存在")
                .path(getCurrentRequestPath())
                .requestId(getRequestId());
    }


    // TODO 或许这里可以优化，如果能保证从RequestCorrelationIdFilter一定能获取到，try chat 可以删除
    private String getRequestId() {
        String requestId = RequestCorrelationIdFilter.getCurrentRequestId();
        if (requestId != null && !requestId.isBlank()) {
            return requestId;
        }
        
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                requestId = request.getHeader("X-Request-ID");
                if (requestId != null && !requestId.isBlank()) {
                    return requestId;
                }
            }
        } catch (Exception e) {
            // 忽略异常，避免影响正常业务
        }
        return "req-" + System.currentTimeMillis() + "-" + java.util.UUID.randomUUID().toString().substring(0, 8);
    }

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
