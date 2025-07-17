package com.sanwenyukaochi.security.exception;

import lombok.Getter;

@Getter
public class APIException extends RuntimeException {
    private final Integer code;
    private final Object data;

    public APIException(String message) {
        super(message);
        this.code = 500;
        this.data = null;
    }

    public APIException(Integer code, String message) {
        super(message);
        this.code = code;
        this.data = null;
    }

    public APIException(Integer code, String message, Object data) {
        super(message);
        this.code = code;
        this.data = data;
    }
}
