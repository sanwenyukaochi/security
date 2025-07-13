package com.sanwenyukaochi.security.exception;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

/**
 * 自定义认证异常基类
 */
@Getter
public class CustomAuthenticationException extends AuthenticationException {
    
    private final String errorCode;
    private final Integer httpStatus;

    public CustomAuthenticationException(String msg) {
        super(msg);
        this.errorCode = "AUTH_ERROR";
        this.httpStatus = 401;
    }

    public CustomAuthenticationException(String msg, String errorCode) {
        super(msg);
        this.errorCode = errorCode;
        this.httpStatus = 401;
    }

    public CustomAuthenticationException(String msg, String errorCode, Integer httpStatus) {
        super(msg);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public CustomAuthenticationException(String msg, Throwable cause) {
        super(msg, cause);
        this.errorCode = "AUTH_ERROR";
        this.httpStatus = 401;
    }

} 