package com.sanwenyukaochi.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserPermissionCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final UserPermissionService userPermissionService;
    
    private static final String USER_ROLES_CACHE_KEY = "user:roles:";
    private static final String USER_PERMISSIONS_CACHE_KEY = "user:permissions:";
    private static final String USER_AUTHORITIES_CACHE_KEY = "user:authorities:";
    private static final long CACHE_EXPIRE_TIME = 30; // 30分钟

    /**
     * 获取用户角色（带缓存）
     */
    public List<String> getUserRoleCodes(Long userId) {
        String cacheKey = USER_ROLES_CACHE_KEY + userId;
        
        @SuppressWarnings("unchecked")
        List<String> cachedRoles = (List<String>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedRoles != null) {
            return cachedRoles;
        }
        
        List<String> roles = userPermissionService.getUserRoleCodes(userId);
        redisTemplate.opsForValue().set(cacheKey, roles, CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
        return roles;
    }

    /**
     * 获取用户权限（带缓存）
     */
    public List<String> getUserPermissionCodes(Long userId) {
        String cacheKey = USER_PERMISSIONS_CACHE_KEY + userId;
        
        @SuppressWarnings("unchecked")
        List<String> cachedPermissions = (List<String>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedPermissions != null) {
            return cachedPermissions;
        }
        
        List<String> permissions = userPermissionService.getUserPermissionCodes(userId);
        redisTemplate.opsForValue().set(cacheKey, permissions, CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
        return permissions;
    }

    /**
     * 获取用户所有权限（带缓存）
     */
    public List<String> getUserAuthorities(Long userId) {
        String cacheKey = USER_AUTHORITIES_CACHE_KEY + userId;
        
        @SuppressWarnings("unchecked")
        List<String> cachedAuthorities = (List<String>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedAuthorities != null) {
            return cachedAuthorities;
        }
        
        List<String> authorities = userPermissionService.getUserAuthorities(userId);
        redisTemplate.opsForValue().set(cacheKey, authorities, CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
        return authorities;
    }

    /**
     * 清除用户权限缓存
     */
    public void clearUserCache(Long userId) {
        String rolesCacheKey = USER_ROLES_CACHE_KEY + userId;
        String permissionsCacheKey = USER_PERMISSIONS_CACHE_KEY + userId;
        String authoritiesCacheKey = USER_AUTHORITIES_CACHE_KEY + userId;
        
        redisTemplate.delete(rolesCacheKey);
        redisTemplate.delete(permissionsCacheKey);
        redisTemplate.delete(authoritiesCacheKey);
    }

    /**
     * 清除所有权限缓存
     */
    public void clearAllCache() {
        redisTemplate.delete(redisTemplate.keys(USER_ROLES_CACHE_KEY + "*"));
        redisTemplate.delete(redisTemplate.keys(USER_PERMISSIONS_CACHE_KEY + "*"));
        redisTemplate.delete(redisTemplate.keys(USER_AUTHORITIES_CACHE_KEY + "*"));
    }
} 