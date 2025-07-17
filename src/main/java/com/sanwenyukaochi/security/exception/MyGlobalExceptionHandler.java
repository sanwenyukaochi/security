package com.sanwenyukaochi.security.exception;

import cn.hutool.crypto.CryptoException;
import cn.hutool.http.HttpStatus;
import com.sanwenyukaochi.security.security.exception.CustomAuthenticationException;
import com.sanwenyukaochi.security.vo.Result;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MyGlobalExceptionHandler {

    // 参数校验失败
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Object> myMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return Result.error(HttpStatus.HTTP_BAD_REQUEST, e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse("参数验证失败"));
    }

    // 解密失败
    @ExceptionHandler(CryptoException.class)
    public Result<Object> handleCryptoException(CryptoException e) {
        String message = e.getMessage();
        return Result.error(HttpStatus.HTTP_UNAUTHORIZED, message);
    }


    // 自定义异常
    @ExceptionHandler(APIException.class)
    public Result<Object> myAPIException(APIException e) {
        String message = e.getMessage();
        return Result.error(e.getCode(), message);
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
        });
    }

    // TODO 项目交给security处理了，上面有一个自定义的 CustomAuthenticationException.class 这个403处理可以考虑是否删除
    @ExceptionHandler(AccessDeniedException.class)
    public Result<Object> handleAccessDeniedException(AccessDeniedException e) {
        return Result.error(HttpStatus.HTTP_FORBIDDEN, "权限不足");
    }

    @ExceptionHandler(RequestRejectedException.class)
    public Result<Object> handleRequestRejectedException(RequestRejectedException e) {
        return Result.error(HttpStatus.HTTP_BAD_REQUEST, "非法请求路径");
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public Result<Object> handleNoHandlerFoundException(NoHandlerFoundException e) {
        return Result.error(HttpStatus.HTTP_NOT_FOUND, "接口不存在");
    }
}
