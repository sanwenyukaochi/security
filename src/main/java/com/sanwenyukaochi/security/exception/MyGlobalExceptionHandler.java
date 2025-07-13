package com.sanwenyukaochi.security.exception;

import com.sanwenyukaochi.security.security.exception.CustomAuthenticationException;
import com.sanwenyukaochi.security.security.filter.RequestIdFilter;
import com.sanwenyukaochi.security.vo.Result;
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
                .requestId(getRequestId());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public Result<Object> myResourceNotFoundException(ResourceNotFoundException e) {
        String message = e.getMessage();
        return Result.error(404, message)
                .path(getCurrentRequestPath())
                .requestId(getRequestId());
    }

    @ExceptionHandler(APIException.class)
    public Result<Object> myAPIException(APIException e) {
        String message = e.getMessage();
        return Result.error(400, message)
                .path(getCurrentRequestPath())
                .requestId(getRequestId());
    }

    @ExceptionHandler(CustomAuthenticationException.class)
    public Result<Object> handleCustomAuthenticationException(CustomAuthenticationException e) {
        // 根据异常类型设置不同的主消息
        String mainMessage = switch (e.getErrorCode()) {
            case "INVALID_PASSWORD" -> "密码错误";
            case "USER_NOT_FOUND" -> "用户不存在";
            case "DISABLED" -> "账户已被禁用";
            case "LOCKED" -> "账户已被锁定";
            case "EXPIRED" -> "账户已过期";
            case "CREDENTIALS_EXPIRED" -> "凭据已过期";
            default -> "认证失败";
        };

        return Result.error(e.getHttpStatus(), mainMessage)
                .path(getCurrentRequestPath())
                .requestId(getRequestId());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Result<Object> handleAccessDeniedException(AccessDeniedException e) {
        return Result.error(403, "权限不足")
                .path(getCurrentRequestPath())
                .requestId(getRequestId());
    }

    @ExceptionHandler(RequestRejectedException.class)
    public Result<Object> handleRequestRejectedException(RequestRejectedException e) {
        return Result.error(400, "非法请求路径")
                .path(getCurrentRequestPath())
                .requestId(getRequestId());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public Result<Object> handleNoHandlerFoundException(NoHandlerFoundException e) {
        // 获取请求ID，优先从请求头获取，否则生成新的
        String requestId = getRequestId();
        
        return Result.error(404, "接口不存在")
                .path(getCurrentRequestPath())
                .requestId(requestId);
    }


    /**
     * 获取请求ID
     */
    private String getRequestId() {
        try {
            // 优先从RequestIdFilter获取
            String requestId = RequestIdFilter.getCurrentRequestId();
            if (requestId != null && !requestId.trim().isEmpty()) {
                return requestId;
            }
            
            // 备用方案：从请求头获取或生成新的
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                jakarta.servlet.http.HttpServletRequest request = attributes.getRequest();
                requestId = request.getHeader("X-Request-ID");
                if (requestId == null || requestId.trim().isEmpty()) {
                    requestId = "req-" + System.currentTimeMillis() + "-" + java.util.UUID.randomUUID().toString().substring(0, 8);
                }
                return requestId;
            }
        } catch (Exception e) {
            // 忽略异常，避免影响正常业务
        }
        return "req-" + System.currentTimeMillis() + "-" + java.util.UUID.randomUUID().toString().substring(0, 8);
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
