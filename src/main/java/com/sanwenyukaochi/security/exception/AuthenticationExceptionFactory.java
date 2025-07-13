package com.sanwenyukaochi.security.exception;

/**
 * 认证异常工厂 - 简化版本
 * 只保留最常用的异常类型，减少文件数量
 */
public class AuthenticationExceptionFactory {
    
    /**
     * 用户不存在
     */
    public static CustomAuthenticationException userNotFound(String username) {
        return new CustomAuthenticationException("用户不存在: " + username, "USER_NOT_FOUND");
    }
    
    /**
     * 密码错误
     */
    public static CustomAuthenticationException invalidPassword(String username) {
        return new CustomAuthenticationException("密码错误", "INVALID_PASSWORD");
    }
    
    /**
     * 账户状态异常（禁用/锁定/过期等）
     */
    public static CustomAuthenticationException accountStatus(String username, String status) {
        String message = switch (status) {
            case "DISABLED" -> "账户已被禁用";
            case "LOCKED" -> "账户已被锁定";
            case "EXPIRED" -> "账户已过期";
            case "CREDENTIALS_EXPIRED" -> "凭据已过期";
            default -> "账户状态异常";
        };
        
        return new CustomAuthenticationException(message + ": " + username, status);
    }
} 