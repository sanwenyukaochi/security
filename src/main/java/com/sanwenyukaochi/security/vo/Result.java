package com.sanwenyukaochi.security.vo;

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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> {

    /**
     * 状态码 (200=成功, 其他=失败)
     */
    private Integer code;
    
    /**
     * 提示信息
     */
    private String msg;
    
    /**
     * 返回数据
     */
    private T data;
    
    /**
     * 请求路径
     */
    private String path;
    
    /**
     * 响应时间戳（毫秒）
     */
    private Long timestamp;
    
    /**
     * 请求ID（用于链路追踪）
     */
    private String requestId;

    /**
     * 成功（无数据）
     */
    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功", null, null, System.currentTimeMillis(), null);
    }

    /**
     * 成功（带数据）
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data, null, System.currentTimeMillis(), null);
    }

    /**
     * 成功（自定义消息）
     */
    public static <T> Result<T> success(String msg, T data) {
        return new Result<>(200, msg, data, null, System.currentTimeMillis(), null);
    }

    /**
     * 失败（默认错误码 500）
     */
    public static <T> Result<T> error() {
        return new Result<>(500, "系统内部错误", null, null, System.currentTimeMillis(), null);
    }

    /**
     * 失败（带数据）
     */
    public static <T> Result<T> error(Integer code, String msg, T data) {
        return new Result<>(code, msg, data, null, System.currentTimeMillis(), null);
    }

    /**
     * 失败（自定义错误信息）
     */
    public static <T> Result<T> error(Integer code, String msg) {
        return new Result<>(code, msg, null, null, System.currentTimeMillis(), null);
    }

    /**
     * 设置请求路径（供AOP和异常处理器使用）
     */
    public Result<T> path(String path) {
        this.path = path;
        return this;
    }

    /**
     * 设置请求ID
     */
    public Result<T> requestId(String requestId) {
        this.requestId = requestId;
        return this;
    }


}
