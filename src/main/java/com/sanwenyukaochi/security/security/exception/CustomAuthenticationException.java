package com.sanwenyukaochi.security.security.exception;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class CustomAuthenticationException extends AuthenticationException {
    
    private final String errorCode;
    private final Integer httpStatus;

    public CustomAuthenticationException(Integer httpStatus, String msg, String errorCode) {
        super(msg);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

}