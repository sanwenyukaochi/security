package com.sanwenyukaochi.security.filter;

import com.sanwenyukaochi.security.context.TenantContext;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 租户感知过滤器
 * 从请求头或请求参数中获取租户ID并设置到上下文中
 */
@Component
@Order(1)
public class TenantAwareFilter implements Filter {
    
    private static final String TENANT_HEADER = "X-Tenant-ID";
    private static final String TENANT_PARAM = "tenantId";
    private static final String DEFAULT_TENANT = "default";
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        
        try {
            // 从请求头获取租户ID
            String tenantId = httpRequest.getHeader(TENANT_HEADER);
            
            // 如果请求头没有，从请求参数获取
            if (tenantId == null || tenantId.trim().isEmpty()) {
                tenantId = httpRequest.getParameter(TENANT_PARAM);
            }
            
            // 如果都没有，使用默认租户
            if (tenantId == null || tenantId.trim().isEmpty()) {
                tenantId = DEFAULT_TENANT;
            }
            
            // 设置租户上下文
            TenantContext.setTenantId(tenantId);
            
            // 继续过滤器链
            chain.doFilter(request, response);
            
        } finally {
            // 清理租户上下文
            TenantContext.clear();
        }
    }
} 