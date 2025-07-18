package com.sanwenyukaochi.security.exception;

import cn.hutool.crypto.CryptoException;
import cn.hutool.http.HttpStatus;
import com.sanwenyukaochi.security.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // =========================
    // 📌 1. 系统异常
    // =========================

    @ExceptionHandler(CryptoException.class)
    public Result<Object> handleCryptoException(CryptoException e) {
        log.warn("解密失败: {}", e.getMessage(), e);
        return Result.error(HttpStatus.HTTP_UNAUTHORIZED, "解密失败");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return Result.error(HttpStatus.HTTP_BAD_REQUEST, e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse("参数验证失败"));
    }

    @ExceptionHandler(RequestRejectedException.class)
    public Result<Object> handleRequestRejectedException(RequestRejectedException e) {
        log.warn("非法请求路径: {}", e.getMessage(), e);
        return Result.error(HttpStatus.HTTP_BAD_REQUEST, "非法请求路径");
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public Result<Object> handleNoHandlerFoundException(NoHandlerFoundException e) {
        log.warn("接口不存在: {}", e.getMessage(), e);
        return Result.error(HttpStatus.HTTP_NOT_FOUND, "接口不存在");
    }

    // =========================
    // 🔐 2. Spring Security - 认证失败 (401)
    // =========================

    @ExceptionHandler(UsernameNotFoundException.class)
    public Result<Object> handleUsernameNotFoundException(UsernameNotFoundException e) {
        log.warn("用户不存在: {}", e.getMessage(), e);
        return Result.error(HttpStatus.HTTP_UNAUTHORIZED, "用户不存在");
    }

    @ExceptionHandler(BadCredentialsException.class)
    public Result<Object> handleBadCredentialsException(BadCredentialsException e) {
        log.warn("用户名或密码错误: {}", e.getMessage(), e);
        return Result.error(HttpStatus.HTTP_UNAUTHORIZED, "用户名或密码错误");
    }

    @ExceptionHandler(DisabledException.class)
    public Result<Object> handleDisabledException(DisabledException e) {
        log.warn("账户已被禁用: {}", e.getMessage(), e);
        return Result.error(HttpStatus.HTTP_UNAUTHORIZED, "账户已被禁用");
    }

    @ExceptionHandler(LockedException.class)
    public Result<Object> handleLockedException(LockedException e) {
        log.warn("账户已被锁定: {}", e.getMessage(), e);
        return Result.error(HttpStatus.HTTP_UNAUTHORIZED, "账户已被锁定");
    }

    @ExceptionHandler(AccountExpiredException.class)
    public Result<Object> handleAccountExpiredException(AccountExpiredException e) {
        log.warn("账户已过期: {}", e.getMessage(), e);
        return Result.error(HttpStatus.HTTP_UNAUTHORIZED, "账户已过期");
    }

    @ExceptionHandler(CredentialsExpiredException.class)
    public Result<Object> handleCredentialsExpiredException(CredentialsExpiredException e) {
        log.warn("账户已过期: {}", e.getMessage(), e);
        return Result.error(HttpStatus.HTTP_UNAUTHORIZED, "密码已过期");
    }

    @ExceptionHandler(AuthenticationException.class)
    public Result<Object> handleAuthenticationException(AuthenticationException e) {
        log.warn("认证失败: {}", e.getMessage(), e);
        return Result.error(HttpStatus.HTTP_UNAUTHORIZED, "认证失败");
    }

    // =========================
    // ⛔ 3. Spring Security - 授权失败 (403)
    // =========================

    @ExceptionHandler(AccessDeniedException.class)
    public Result<Object> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("您没有访问该资源的权限: {}", e.getMessage(), e);
        return Result.error(HttpStatus.HTTP_FORBIDDEN, "您没有访问该资源的权限");
    }

    @ExceptionHandler(AuthorizationServiceException.class)
    public Result<Object> handleAuthorizationServiceException(AuthorizationServiceException e) {
        log.warn("权限系统内部错误: {}", e.getMessage(), e);
        return Result.error(HttpStatus.HTTP_FORBIDDEN, "权限系统内部错误");
    }

    // =========================
    // ⚙️ 4. 自定义业务异常
    // =========================

    @ExceptionHandler(APIException.class)
    public Result<Object> handleAPIException(APIException e) {
        return Result.error(e.getCode(), e.getMessage());
    }

}
