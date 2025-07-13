package com.sanwenyukaochi.security.context;

/**
 * 租户上下文管理类
 * 使用ThreadLocal存储当前请求的租户ID
 */
public class TenantContext {
    
    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();
    
    /**
     * 设置当前租户ID
     * @param tenantId 租户ID
     */
    public static void setTenantId(String tenantId) {
        CURRENT_TENANT.set(tenantId);
    }
    
    /**
     * 获取当前租户ID
     * @return 租户ID
     */
    public static String getTenantId() {
        return CURRENT_TENANT.get();
    }
    
    /**
     * 清除当前租户上下文
     */
    public static void clear() {
        CURRENT_TENANT.remove();
    }
    
    /**
     * 检查是否有租户上下文
     * @return 是否有租户上下文
     */
    public static boolean hasTenant() {
        return CURRENT_TENANT.get() != null;
    }
} 