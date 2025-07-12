package com.sanwenyukaochi.security.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanwenyukaochi.security.entity.*;
import com.sanwenyukaochi.security.repository.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserPermissionCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final UserPermissionService userPermissionService;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final ObjectMapper objectMapper;
    
    // Redis缓存键前缀
    private static final String USER_INFO_CACHE_KEY = "user:info:";
    private static final String USER_ROLES_CACHE_KEY = "user:roles:";
    private static final String USER_PERMISSIONS_CACHE_KEY = "user:permissions:";
    private static final String USER_AUTHORITIES_CACHE_KEY = "user:authorities:";
    private static final String USER_MENUS_CACHE_KEY = "user:menus:";
    private static final String USER_DETAILS_CACHE_KEY = "user:details:";
    private static final long CACHE_EXPIRE_TIME = 30; // 30分钟

    /**
     * 用户详细信息VO类
     */
    @Setter
    @Getter
    public static class UserInfoVO {
        private Long id;
        private String userName;
        private String email;
        private String phone;
        private Boolean status;
        private TenantInfo tenant;
        private List<RoleInfo> roles;
        private List<PermissionInfo> permissions;
        private List<String> authorities;
        private List<MenuInfo> menus;

        // 构造函数、getter和setter
        public UserInfoVO() {}

        public UserInfoVO(User user) {
            this.id = user.getId();
            this.userName = user.getUserName();
            this.email = user.getEmail();
            this.phone = user.getPhone();
            this.status = user.getStatus();
            // 安全地设置tenant信息
            if (user.getTenant() != null) {
                this.tenant = new TenantInfo(user.getTenant());
            }
        }
    }

    /**
     * 角色信息VO类
     */
    @Setter
    @Getter
    public static class RoleInfo {
        private Long id;
        private String name;
        private String code;
        private Integer dataScope;
        private Boolean status;

        public RoleInfo() {}

        public RoleInfo(Role role) {
            this.id = role.getId();
            this.name = role.getName();
            this.code = role.getCode();
            this.dataScope = role.getDataScope();
            this.status = role.getStatus();
        }

    }

    /**
     * 权限信息VO类
     */
    @Setter
    @Getter
    public static class PermissionInfo {
        private Long id;
        private String name;
        private String code;
        private String type;
        private String path;
        private Long parentId;
        private Integer sort;
        private Boolean visible;

        public PermissionInfo() {}

        public PermissionInfo(Permission permission) {
            this.id = permission.getId();
            this.name = permission.getName();
            this.code = permission.getCode();
            this.type = permission.getType();
            this.path = permission.getPath();
            this.parentId = permission.getParentId();
            this.sort = permission.getSort();
            this.visible = permission.getVisible();
        }

    }

    /**
     * 菜单信息VO类
     */
    @Setter
    @Getter
    public static class MenuInfo {
        private Long id;
        private String name;
        private String code;
        private String path;
        private String component;
        private Long parentId;
        private Integer sort;
        private Boolean visible;
        private String icon;
        private List<MenuInfo> children;

        public MenuInfo() {}

        public MenuInfo(Permission permission) {
            this.id = permission.getId();
            this.name = permission.getName();
            this.code = permission.getCode();
            this.path = permission.getPath();
            this.parentId = permission.getParentId();
            this.sort = permission.getSort();
            this.visible = permission.getVisible();
            this.children = new ArrayList<>();
        }

    }

    /**
     * 租户信息VO类
     */
    @Setter
    @Getter
    public static class TenantInfo {
        private Long id;
        private String name;
        private String code;
        private Boolean status;

        public TenantInfo() {}

        public TenantInfo(Tenant tenant) {
            this.id = tenant.getId();
            this.name = tenant.getName();
            this.code = tenant.getCode();
            this.status = tenant.getStatus();
        }

    }

    /**
     * 获取用户完整信息（带缓存）
     */
    public UserInfoVO getUserInfo(Long userId) {
        String cacheKey = USER_INFO_CACHE_KEY + userId;
        
        UserInfoVO cachedUserInfo = (UserInfoVO) redisTemplate.opsForValue().get(cacheKey);
        if (cachedUserInfo != null) {
            log.debug("从缓存获取用户信息: {}", userId);
            return cachedUserInfo;
        }
        
        log.debug("从数据库获取用户信息: {}", userId);
        UserInfoVO userInfo = buildUserInfo(userId);
        redisTemplate.opsForValue().set(cacheKey, userInfo, CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
        return userInfo;
    }

    /**
     * 构建用户完整信息
     */
    private UserInfoVO buildUserInfo(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return null;
        }

        // 确保懒加载的关联对象被初始化
        if (user.getTenant() != null) {
            // 触发懒加载
            user.getTenant().getId();
        }

        UserInfoVO userInfo = new UserInfoVO(user);
        
        // 获取用户角色
        List<RoleInfo> roles = getUserRoles(userId);
        userInfo.setRoles(roles);
        
        // 获取用户权限
        List<PermissionInfo> permissions = getUserPermissions(userId);
        userInfo.setPermissions(permissions);
        
        // 获取用户所有权限（包括角色和权限）
        List<String> authorities = getUserAuthorities(userId);
        userInfo.setAuthorities(authorities);
        
        // 构建菜单树
        List<MenuInfo> menus = buildMenuTree(permissions);
        userInfo.setMenus(menus);
        
        return userInfo;
    }

    /**
     * 获取用户角色（带缓存）
     */
    public List<RoleInfo> getUserRoles(Long userId) {
        String cacheKey = USER_ROLES_CACHE_KEY + userId;
        
        @SuppressWarnings("unchecked")
        List<RoleInfo> cachedRoles = (List<RoleInfo>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedRoles != null) {
            return cachedRoles;
        }
        
        List<UserRole> userRoles = userRoleRepository.findByUser_Id(userId);
        List<RoleInfo> roles = userRoles.stream()
                .map(UserRole::getRole)
                .map(RoleInfo::new)
                .collect(Collectors.toList());
        
        redisTemplate.opsForValue().set(cacheKey, roles, CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
        return roles;
    }

    /**
     * 获取用户权限（带缓存）
     */
    public List<PermissionInfo> getUserPermissions(Long userId) {
        String cacheKey = USER_PERMISSIONS_CACHE_KEY + userId;
        
        @SuppressWarnings("unchecked")
        List<PermissionInfo> cachedPermissions = (List<PermissionInfo>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedPermissions != null) {
            return cachedPermissions;
        }
        
        List<UserRole> userRoles = userRoleRepository.findByUser_Id(userId);
        List<PermissionInfo> permissions = userRoles.stream()
                .map(UserRole::getRole)
                .map(role -> rolePermissionRepository.findByRole(role))
                .flatMap(List::stream)
                .map(RolePermission::getPermission)
                .map(PermissionInfo::new)
                .distinct()
                .collect(Collectors.toList());
        
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
     * 获取用户菜单（带缓存）
     */
    public List<MenuInfo> getUserMenus(Long userId) {
        String cacheKey = USER_MENUS_CACHE_KEY + userId;
        
        @SuppressWarnings("unchecked")
        List<MenuInfo> cachedMenus = (List<MenuInfo>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedMenus != null) {
            return cachedMenus;
        }
        
        List<PermissionInfo> permissions = getUserPermissions(userId);
        List<MenuInfo> menus = buildMenuTree(permissions);
        redisTemplate.opsForValue().set(cacheKey, menus, CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
        return menus;
    }

    /**
     * 构建菜单树
     */
    private List<MenuInfo> buildMenuTree(List<PermissionInfo> permissions) {
        // 只处理菜单类型的权限
        List<PermissionInfo> menuPermissions = permissions.stream()
                .filter(p -> "1".equals(p.getType())) // 1表示菜单
                .collect(Collectors.toList());
        
        Map<Long, MenuInfo> menuMap = new HashMap<>();
        List<MenuInfo> rootMenus = new ArrayList<>();
        
        // 创建菜单映射
        for (PermissionInfo permission : menuPermissions) {
            MenuInfo menu = new MenuInfo();
            menu.setId(permission.getId());
            menu.setName(permission.getName());
            menu.setCode(permission.getCode());
            menu.setPath(permission.getPath());
            menu.setParentId(permission.getParentId());
            menu.setSort(permission.getSort());
            menu.setVisible(permission.getVisible());
            menu.setChildren(new ArrayList<>());
            
            menuMap.put(menu.getId(), menu);
        }
        
        // 构建树形结构
        for (MenuInfo menu : menuMap.values()) {
            if (menu.getParentId() == null || menu.getParentId() == 0) {
                rootMenus.add(menu);
            } else {
                MenuInfo parent = menuMap.get(menu.getParentId());
                if (parent != null) {
                    parent.getChildren().add(menu);
                }
            }
        }
        
        // 按sort排序
        rootMenus.sort(Comparator.comparing(MenuInfo::getSort, Comparator.nullsLast(Comparator.naturalOrder())));
        
        return rootMenus;
    }

    /**
     * 清除用户权限缓存
     */
    public void clearUserCache(Long userId) {
        String[] cacheKeys = {
            USER_INFO_CACHE_KEY + userId,
            USER_ROLES_CACHE_KEY + userId,
            USER_PERMISSIONS_CACHE_KEY + userId,
            USER_AUTHORITIES_CACHE_KEY + userId,
            USER_MENUS_CACHE_KEY + userId
        };
        
        for (String cacheKey : cacheKeys) {
            redisTemplate.delete(cacheKey);
        }
        
        log.info("清除用户缓存: {}", userId);
    }

    /**
     * 清除所有权限缓存
     */
    public void clearAllCache() {
        Set<String> keys = new HashSet<>();
        
        // 获取所有相关的缓存键
        keys.addAll(redisTemplate.keys(USER_INFO_CACHE_KEY + "*"));
        keys.addAll(redisTemplate.keys(USER_ROLES_CACHE_KEY + "*"));
        keys.addAll(redisTemplate.keys(USER_PERMISSIONS_CACHE_KEY + "*"));
        keys.addAll(redisTemplate.keys(USER_AUTHORITIES_CACHE_KEY + "*"));
        keys.addAll(redisTemplate.keys(USER_MENUS_CACHE_KEY + "*"));
        
        if (!keys.isEmpty()) {
            redisTemplate.delete(keys);
            log.info("清除所有用户缓存，共 {} 个键", keys.size());
        }
    }

    /**
     * 获取在线用户数量
     */
    public long getOnlineUserCount() {
        Set<String> keys = redisTemplate.keys(USER_INFO_CACHE_KEY + "*");
        return keys != null ? keys.size() : 0;
    }

    /**
     * 获取所有在线用户信息
     */
    public List<UserInfoVO> getOnlineUsers() {
        Set<String> keys = redisTemplate.keys(USER_INFO_CACHE_KEY + "*");
        if (keys == null || keys.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<UserInfoVO> onlineUsers = new ArrayList<>();
        for (String key : keys) {
            UserInfoVO userInfo = (UserInfoVO) redisTemplate.opsForValue().get(key);
            if (userInfo != null) {
                onlineUsers.add(userInfo);
            }
        }
        return onlineUsers;
    }

    /**
     * 强制用户下线
     */
    public void forceUserOffline(Long userId) {
        clearUserCache(userId);
        log.info("强制用户下线: {}", userId);
    }

    /**
     * 批量强制用户下线
     */
    public void forceUsersOffline(List<Long> userIds) {
        for (Long userId : userIds) {
            forceUserOffline(userId);
        }
        log.info("批量强制用户下线，共 {} 个用户", userIds.size());
    }

    /**
     * 获取用户会话信息
     */
    public Map<String, Object> getUserSessionInfo(Long userId) {
        UserInfoVO userInfo = getUserInfo(userId);
        if (userInfo == null) {
            return new HashMap<>();
        }

        Map<String, Object> sessionInfo = new HashMap<>();
        sessionInfo.put("userId", userInfo.getId());
        sessionInfo.put("userName", userInfo.getUserName());
        sessionInfo.put("tenant", userInfo.getTenant());
        sessionInfo.put("roles", userInfo.getRoles());
        sessionInfo.put("permissions", userInfo.getPermissions());
        sessionInfo.put("authorities", userInfo.getAuthorities());

        return sessionInfo;
    }

    /**
     * 检查用户是否在线
     */
    public boolean isUserOnline(Long userId) {
        String cacheKey = USER_INFO_CACHE_KEY + userId;
        return Boolean.TRUE.equals(redisTemplate.hasKey(cacheKey));
    }

    /**
     * 获取用户权限缓存键
     */
    public List<String> getUserCacheKeys(Long userId) {
        List<String> keys = new ArrayList<>();
        keys.add(USER_INFO_CACHE_KEY + userId);
        keys.add(USER_ROLES_CACHE_KEY + userId);
        keys.add(USER_PERMISSIONS_CACHE_KEY + userId);
        keys.add(USER_AUTHORITIES_CACHE_KEY + userId);
        keys.add(USER_MENUS_CACHE_KEY + userId);
        return keys;
    }

    /**
     * 预热用户缓存
     */
    public void preloadUserCache(Long userId) {
        try {
            getUserInfo(userId);
            log.debug("预热用户缓存成功: {}", userId);
        } catch (Exception e) {
            log.error("预热用户缓存失败: {}", userId, e);
        }
    }

    /**
     * 批量预热用户缓存
     */
    public void preloadUsersCache(List<Long> userIds) {
        for (Long userId : userIds) {
            preloadUserCache(userId);
        }
        log.info("批量预热用户缓存完成，共 {} 个用户", userIds.size());
    }

    /**
     * 获取缓存统计信息
     */
    public Map<String, Object> getCacheStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 统计各种缓存键的数量
        Set<String> userInfoKeys = redisTemplate.keys(USER_INFO_CACHE_KEY + "*");
        Set<String> userRolesKeys = redisTemplate.keys(USER_ROLES_CACHE_KEY + "*");
        Set<String> userPermissionsKeys = redisTemplate.keys(USER_PERMISSIONS_CACHE_KEY + "*");
        Set<String> userAuthoritiesKeys = redisTemplate.keys(USER_AUTHORITIES_CACHE_KEY + "*");
        Set<String> userMenusKeys = redisTemplate.keys(USER_MENUS_CACHE_KEY + "*");
        
        stats.put("userInfoCacheCount", userInfoKeys != null ? userInfoKeys.size() : 0);
        stats.put("userRolesCacheCount", userRolesKeys != null ? userRolesKeys.size() : 0);
        stats.put("userPermissionsCacheCount", userPermissionsKeys != null ? userPermissionsKeys.size() : 0);
        stats.put("userAuthoritiesCacheCount", userAuthoritiesKeys != null ? userAuthoritiesKeys.size() : 0);
        stats.put("userMenusCacheCount", userMenusKeys != null ? userMenusKeys.size() : 0);
        
        // 计算总缓存大小
        long totalCacheSize = 0;
        if (userInfoKeys != null) totalCacheSize += userInfoKeys.size();
        if (userRolesKeys != null) totalCacheSize += userRolesKeys.size();
        if (userPermissionsKeys != null) totalCacheSize += userPermissionsKeys.size();
        if (userAuthoritiesKeys != null) totalCacheSize += userAuthoritiesKeys.size();
        if (userMenusKeys != null) totalCacheSize += userMenusKeys.size();
        
        stats.put("totalCacheSize", totalCacheSize);
        stats.put("cacheExpireTime", CACHE_EXPIRE_TIME + "分钟");
        
        return stats;
    }

    /**
     * 清理过期缓存
     */
    public void cleanExpiredCache() {
        Set<String> allKeys = new HashSet<>();
        allKeys.addAll(redisTemplate.keys(USER_INFO_CACHE_KEY + "*"));
        allKeys.addAll(redisTemplate.keys(USER_ROLES_CACHE_KEY + "*"));
        allKeys.addAll(redisTemplate.keys(USER_PERMISSIONS_CACHE_KEY + "*"));
        allKeys.addAll(redisTemplate.keys(USER_AUTHORITIES_CACHE_KEY + "*"));
        allKeys.addAll(redisTemplate.keys(USER_MENUS_CACHE_KEY + "*"));
        
        int cleanedCount = 0;
        for (String key : allKeys) {
            if (!Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
                redisTemplate.delete(key);
                cleanedCount++;
            }
        }
        
        log.info("清理过期缓存完成，共清理 {} 个键", cleanedCount);
    }

    /**
     * 获取用户详细信息（包含所有缓存信息）
     */
    public Map<String, Object> getUserDetailedInfo(Long userId) {
        Map<String, Object> detailedInfo = new HashMap<>();
        
        // 基本信息
        UserInfoVO userInfo = getUserInfo(userId);
        if (userInfo != null) {
            detailedInfo.put("userInfo", userInfo);
        }
        
        // 角色信息
        List<RoleInfo> roles = getUserRoles(userId);
        detailedInfo.put("roles", roles);
        
        // 权限信息
        List<PermissionInfo> permissions = getUserPermissions(userId);
        detailedInfo.put("permissions", permissions);
        
        // 权限代码
        List<String> authorities = getUserAuthorities(userId);
        detailedInfo.put("authorities", authorities);
        
        // 菜单信息
        List<MenuInfo> menus = getUserMenus(userId);
        detailedInfo.put("menus", menus);
        
        // 缓存状态
        detailedInfo.put("isOnline", isUserOnline(userId));
        detailedInfo.put("cacheKeys", getUserCacheKeys(userId));
        
        return detailedInfo;
    }
}