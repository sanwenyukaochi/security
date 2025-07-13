package com.sanwenyukaochi.security.aspect;

import com.sanwenyukaochi.security.interceptor.RequestIdInterceptor;
import com.sanwenyukaochi.security.vo.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;



/**
 * 自动设置响应路径的AOP切面
 */
@Aspect
@Component
public class ResultPathAspect {

    /**
     * 拦截所有Controller方法的返回值
     */
    @Around("execution(* com.sanwenyukaochi.security.controller.*.*(..))")
    public Object setResultPath(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        
        // 如果返回值是Result类型，自动设置路径和请求ID
        if (result instanceof Result) {
            Result<?> resultObj = (Result<?>) result;
            
            // 设置请求路径
            String path = getCurrentRequestPath();
            if (path != null) {
                resultObj.setPath(path);
            }
            
            // 设置请求ID
            String requestId = RequestIdInterceptor.getCurrentRequestId();
            if (requestId != null) {
                resultObj.setRequestId(requestId);
            }
        }
        
        return result;
    }

    /**
     * 获取当前请求路径
     */
    private String getCurrentRequestPath() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                return request.getRequestURI();
            }
        } catch (Exception e) {
            // 忽略异常，避免影响正常业务
        }
        return null;
    }
} 