package com.sanwenyukaochi.security.security.exception;

import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Map;


// TODO 工厂 + 策略 但是考虑一下 username 有没有用，没用可以把 username 删掉
public class AuthenticationExceptionFactory {

    private static final Map<Class<? extends AuthenticationException>, AuthExceptionStrategy> STRATEGY_MAP = Map.of(
            UsernameNotFoundException.class, (username, e) -> AuthenticationExceptionFactory.authenticationError(401, username, "USER_NOT_FOUND"),
            BadCredentialsException.class, (username, e) -> AuthenticationExceptionFactory.authenticationError(401, username, "INVALID_PASSWORD"),
            DisabledException.class, (username, e) -> AuthenticationExceptionFactory.accountStatus(403, username, "DISABLED"),
            LockedException.class, (username, e) -> AuthenticationExceptionFactory.accountStatus(403, username, "LOCKED"),
            AccountExpiredException.class, (username, e) -> AuthenticationExceptionFactory.accountStatus(403, username, "EXPIRED"),
            CredentialsExpiredException.class, (username, e) -> AuthenticationExceptionFactory.accountStatus(403, username, "CREDENTIALS_EXPIRED")
            
    );

    public static CustomAuthenticationException resolve(String username, AuthenticationException e) {
        AuthExceptionStrategy strategy = STRATEGY_MAP.get(e.getClass());
        if (strategy != null) {
            return strategy.handle(username, e);
        }
        return new CustomAuthenticationException(401, "认证失败: " + e.getMessage(), "AUTH_ERROR");
    }

    public static CustomAuthenticationException authenticationError(Integer code, String username, String errorCode) {
        String message = switch (errorCode) {
            case "USER_NOT_FOUND" -> "用户不存在";
            case "INVALID_PASSWORD" -> "密码错误";
            default -> "认证失败";
        };
        return new CustomAuthenticationException(code ,message, errorCode);
    }

    public static CustomAuthenticationException accountStatus(Integer code, String username, String errorCode) {
        String message = switch (errorCode) {
            case "DISABLED" -> "账户已被禁用";
            case "LOCKED" -> "账户已被锁定";
            case "EXPIRED" -> "账户已过期";
            case "CREDENTIALS_EXPIRED" -> "凭据已过期";
            default -> "账户状态异常";
        };
        return new CustomAuthenticationException(code, message, errorCode);
    }
}