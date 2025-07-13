package com.sanwenyukaochi.security.config;

import com.sanwenyukaochi.security.context.TenantContext;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

/**
 * Hibernate租户标识符解析器
 * 实现CurrentTenantIdentifierResolver接口，为Hibernate提供当前租户ID
 */
@Component
public class CurrentTenantIdentifierResolverImpl implements CurrentTenantIdentifierResolver {
    
    private static final String DEFAULT_TENANT = "default";
    
    @Override
    public String resolveCurrentTenantIdentifier() {
        try {
            String tenantId = TenantContext.getTenantId();
            // 如果租户上下文为空，返回默认租户，避免启动时的错误
            return tenantId != null ? tenantId : DEFAULT_TENANT;
        } catch (Exception e) {
            // 如果出现任何异常，返回默认租户
            return DEFAULT_TENANT;
        }
    }
    
    @Override
    public boolean validateExistingCurrentSessions() {
        return false; // 禁用验证，避免启动时的问题
    }
} 