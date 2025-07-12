package com.sanwenyukaochi.security.controller;

import com.sanwenyukaochi.security.security.service.UserDetailsImpl;
import com.sanwenyukaochi.security.service.UserPermissionCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/cache")
@RequiredArgsConstructor
public class UserCacheController {

    private final UserPermissionCacheService userPermissionCacheService;

    /**
     * 获取当前用户的完整缓存信息
     */
    @GetMapping("/user/info")
    public ResponseEntity<?> getCurrentUserCacheInfo(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.badRequest().body(Map.of("message", "用户未认证"));
        }

        // 从认证信息中获取用户ID（这里需要根据您的UserDetails实现来调整）
        Long userId = getUserIdFromAuthentication(authentication);
        if (userId == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "无法获取用户ID"));
        }

        Map<String, Object> detailedInfo = userPermissionCacheService.getUserDetailedInfo(userId);
        return ResponseEntity.ok(detailedInfo);
    }

    /**
     * 获取在线用户列表
     */
    @GetMapping("/online/users")
    public ResponseEntity<?> getOnlineUsers() {
        List<UserPermissionCacheService.UserInfoVO> onlineUsers = userPermissionCacheService.getOnlineUsers();
        return ResponseEntity.ok(Map.of(
            "onlineUsers", onlineUsers,
            "totalCount", onlineUsers.size()
        ));
    }

    /**
     * 获取在线用户数量
     */
    @GetMapping("/online/count")
    public ResponseEntity<?> getOnlineUserCount() {
        long count = userPermissionCacheService.getOnlineUserCount();
        return ResponseEntity.ok(Map.of("onlineUserCount", count));
    }

    /**
     * 获取用户登录统计信息
     */
    @GetMapping("/stats/login")
    public ResponseEntity<?> getLoginStats() {
        Map<String, Object> stats = userPermissionCacheService.getUserLoginStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * 获取缓存统计信息
     */
    @GetMapping("/stats/cache")
    public ResponseEntity<?> getCacheStats() {
        Map<String, Object> stats = userPermissionCacheService.getCacheStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * 强制用户下线
     */
    @PostMapping("/user/{userId}/offline")
    public ResponseEntity<?> forceUserOffline(@PathVariable Long userId) {
        userPermissionCacheService.forceUserOffline(userId);
        return ResponseEntity.ok(Map.of("message", "用户已强制下线", "userId", userId));
    }

    /**
     * 批量强制用户下线
     */
    @PostMapping("/users/offline")
    public ResponseEntity<?> forceUsersOffline(@RequestBody List<Long> userIds) {
        userPermissionCacheService.forceUsersOffline(userIds);
        return ResponseEntity.ok(Map.of(
            "message", "批量强制用户下线完成",
            "userIds", userIds,
            "count", userIds.size()
        ));
    }

    /**
     * 清除用户缓存
     */
    @PostMapping("/user/{userId}/clear")
    public ResponseEntity<?> clearUserCache(@PathVariable Long userId) {
        userPermissionCacheService.clearUserCache(userId);
        return ResponseEntity.ok(Map.of("message", "用户缓存已清除", "userId", userId));
    }

    /**
     * 清除所有缓存
     */
    @PostMapping("/clear/all")
    public ResponseEntity<?> clearAllCache() {
        userPermissionCacheService.clearAllCache();
        return ResponseEntity.ok(Map.of("message", "所有缓存已清除"));
    }

    /**
     * 检查用户是否在线
     */
    @GetMapping("/user/{userId}/online")
    public ResponseEntity<?> isUserOnline(@PathVariable Long userId) {
        boolean isOnline = userPermissionCacheService.isUserOnline(userId);
        return ResponseEntity.ok(Map.of(
            "userId", userId,
            "isOnline", isOnline
        ));
    }

    /**
     * 获取用户会话信息
     */
    @GetMapping("/user/{userId}/session")
    public ResponseEntity<?> getUserSessionInfo(@PathVariable Long userId) {
        Map<String, Object> sessionInfo = userPermissionCacheService.getUserSessionInfo(userId);
        return ResponseEntity.ok(sessionInfo);
    }

    /**
     * 获取用户最后活跃时间
     */
    @GetMapping("/user/{userId}/last-active")
    public ResponseEntity<?> getUserLastActiveTime(@PathVariable Long userId) {
        java.util.Date lastActiveTime = userPermissionCacheService.getUserLastActiveTime(userId);
        return ResponseEntity.ok(Map.of(
            "userId", userId,
            "lastActiveTime", lastActiveTime
        ));
    }

    /**
     * 更新用户最后活跃时间
     */
    @PostMapping("/user/{userId}/update-active")
    public ResponseEntity<?> updateUserLastActiveTime(@PathVariable Long userId) {
        userPermissionCacheService.updateUserLastActiveTime(userId);
        return ResponseEntity.ok(Map.of("message", "用户活跃时间已更新", "userId", userId));
    }

    /**
     * 预热用户缓存
     */
    @PostMapping("/user/{userId}/preload")
    public ResponseEntity<?> preloadUserCache(@PathVariable Long userId) {
        userPermissionCacheService.preloadUserCache(userId);
        return ResponseEntity.ok(Map.of("message", "用户缓存预热完成", "userId", userId));
    }

    /**
     * 批量预热用户缓存
     */
    @PostMapping("/users/preload")
    public ResponseEntity<?> preloadUsersCache(@RequestBody List<Long> userIds) {
        userPermissionCacheService.preloadUsersCache(userIds);
        return ResponseEntity.ok(Map.of(
            "message", "批量预热用户缓存完成",
            "userIds", userIds,
            "count", userIds.size()
        ));
    }

    /**
     * 清理过期缓存
     */
    @PostMapping("/clean/expired")
    public ResponseEntity<?> cleanExpiredCache() {
        userPermissionCacheService.cleanExpiredCache();
        return ResponseEntity.ok(Map.of("message", "过期缓存清理完成"));
    }

    /**
     * 获取用户菜单
     */
    @GetMapping("/user/{userId}/menus")
    public ResponseEntity<?> getUserMenus(@PathVariable Long userId) {
        List<UserPermissionCacheService.MenuInfo> menus = userPermissionCacheService.getUserMenus(userId);
        return ResponseEntity.ok(Map.of(
            "userId", userId,
            "menus", menus
        ));
    }

    /**
     * 获取用户角色
     */
    @GetMapping("/user/{userId}/roles")
    public ResponseEntity<?> getUserRoles(@PathVariable Long userId) {
        List<UserPermissionCacheService.RoleInfo> roles = userPermissionCacheService.getUserRoles(userId);
        return ResponseEntity.ok(Map.of(
            "userId", userId,
            "roles", roles
        ));
    }

    /**
     * 获取用户权限
     */
    @GetMapping("/user/{userId}/permissions")
    public ResponseEntity<?> getUserPermissions(@PathVariable Long userId) {
        List<UserPermissionCacheService.PermissionInfo> permissions = userPermissionCacheService.getUserPermissions(userId);
        return ResponseEntity.ok(Map.of(
            "userId", userId,
            "permissions", permissions
        ));
    }

    /**
     * 获取用户所有权限（包括角色和权限）
     */
    @GetMapping("/user/{userId}/authorities")
    public ResponseEntity<?> getUserAuthorities(@PathVariable Long userId) {
        List<String> authorities = userPermissionCacheService.getUserAuthorities(userId);
        return ResponseEntity.ok(Map.of(
            "userId", userId,
            "authorities", authorities
        ));
    }

    /**
     * 从认证信息中获取用户ID
     * 注意：这里需要根据您的UserDetails实现来调整
     */
    private Long getUserIdFromAuthentication(Authentication authentication) {
        try {
            // 这里需要根据您的UserDetails实现来获取用户ID
            // 假设您的UserDetailsImpl有getId()方法
            if (authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
                return userDetails.getId();
            }
            return null;
        } catch (Exception e) {
            log.error("获取用户ID失败", e);
            return null;
        }
    }
} 