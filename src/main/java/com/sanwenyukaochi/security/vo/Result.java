package com.sanwenyukaochi.security.vo;

import cn.hutool.http.HttpStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一 JSON 返回结构
 * 
 * @author sanwenyukaochi
 * @version 1.0
 * @since 2025-07-13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude
public class Result<T> {
    
    private Integer code;
    private String msg;
    private T data;
    private String path;
    private Long timestamp;
    private String requestId;
    
    public static <T> Result<T> success() {
        return new Result<>(HttpStatus.HTTP_OK, "操作成功", null, null, System.currentTimeMillis(), null);
    }
    
    public static <T> Result<T> success(T data) {
        return new Result<>(HttpStatus.HTTP_OK, "操作成功", data, null, System.currentTimeMillis(), null);
    }
    
    public static <T> Result<T> success(String msg, T data) {
        return new Result<>(HttpStatus.HTTP_OK, msg, data, null, System.currentTimeMillis(), null);
    }
    
    public static <T> Result<T> error() {
        return new Result<>(HttpStatus.HTTP_INTERNAL_ERROR, "系统内部错误", null, null, System.currentTimeMillis(), null);
    }
    
    public static <T> Result<T> error(Integer code, String msg, T data) {
        return new Result<>(code, msg, data, null, System.currentTimeMillis(), null);
    }
    
    public static <T> Result<T> error(Integer code, String msg) {
        return new Result<>(code, msg, null, null, System.currentTimeMillis(), null);
    }
    
    public Result<T> path(String path) {
        this.path = path;
        return this;
    }
    
    public Result<T> requestId(String requestId) {
        this.requestId = requestId;
        return this;
    }
    
}
