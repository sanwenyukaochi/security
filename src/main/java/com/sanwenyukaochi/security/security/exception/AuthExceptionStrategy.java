package com.sanwenyukaochi.security.security.exception;

import org.springframework.security.core.AuthenticationException;

@FunctionalInterface
public interface AuthExceptionStrategy {
    CustomAuthenticationException handle(String username, AuthenticationException e);
} 