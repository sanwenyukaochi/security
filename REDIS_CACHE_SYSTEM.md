# Redis缓存系统使用说明

## 概述

本系统实现了类似若依(RuoYi)项目的Redis缓存功能，提供了丰富的用户信息缓存、在线用户管理、权限缓存等功能。

## 缓存结构

### Redis键命名规范

```
user:info:{userId}      - 用户完整信息缓存
user:roles:{userId}     - 用户角色信息缓存
user:permissions:{userId} - 用户权限信息缓存
user:authorities:{userId} - 用户所有权限缓存
user:menus:{userId}     - 用户菜单信息缓存
```

### 缓存数据结构

#### 1. 用户信息VO (UserInfoVO)
```json
{
  "id": 1234567890,
  "userName": "admin",
  "email": "admin@example.com",
  "phone": "13800138001",
  "status": true,
  "tenant": {
    "id": 1234567890,
    "name": "测试组",
    "code": "test_group"
  },
  "roles": [
    {
      "id": 1234567890,
      "name": "系统管理员",
      "code": "admin",
      "dataScope": 0,
      "status": true
    }
  ],
  "permissions": [
    {
      "id": 1234567890,
      "name": "用户管理",
      "code": "user:manage",
      "type": "1",
      "path": "/user",
      "parentId": 0,
      "sort": 1,
      "visible": true
    }
  ],
  "authorities": [
    "ROLE_ADMIN",
    "user:manage",
    "user:view",
    "user:add",
    "user:edit",
    "user:delete"
  ],
  "menus": [
    {
      "id": 1234567890,
      "name": "用户管理",
      "code": "user:manage",
      "path": "/user",
      "component": null,
      "parentId": 0,
      "sort": 1,
      "visible": true,
      "icon": null,
      "children": []
    }
  ],
  "loginTime": "2024-01-01T10:00:00.000Z",
  "loginIp": "192.168.1.100",
  "browser": "Chrome",
  "os": "Windows 10"
}
```

## API接口

### 用户缓存管理

#### 获取当前用户缓存信息
```http
GET /api/cache/user/info
Authorization: Bearer {token}
```

#### 获取用户详细信息
```http
GET /api/cache/user/{userId}/detailed
```

#### 获取用户会话信息
```http
GET /api/cache/user/{userId}/session
```

### 在线用户管理

#### 获取在线用户列表
```http
GET /api/cache/online/users
```

#### 获取在线用户数量
```http
GET /api/cache/online/count
```

#### 检查用户是否在线
```http
GET /api/cache/user/{userId}/online
```

#### 强制用户下线
```http
POST /api/cache/user/{userId}/offline
```

#### 批量强制用户下线
```http
POST /api/cache/users/offline
Content-Type: application/json

[1, 2, 3, 4, 5]
```

### 权限信息查询

#### 获取用户角色
```http
GET /api/cache/user/{userId}/roles
```

#### 获取用户权限
```http
GET /api/cache/user/{userId}/permissions
```

#### 获取用户所有权限
```http
GET /api/cache/user/{userId}/authorities
```

#### 获取用户菜单
```http
GET /api/cache/user/{userId}/menus
```

### 缓存管理

#### 清除用户缓存
```http
POST /api/cache/user/{userId}/clear
```

#### 清除所有缓存
```http
POST /api/cache/clear/all
```

#### 预热用户缓存
```http
POST /api/cache/user/{userId}/preload
```

#### 批量预热用户缓存
```http
POST /api/cache/users/preload
Content-Type: application/json

[1, 2, 3, 4, 5]
```

#### 清理过期缓存
```http
POST /api/cache/clean/expired
```

### 统计信息

#### 获取登录统计
```http
GET /api/cache/stats/login
```

响应示例：
```json
{
  "totalOnlineUsers": 10,
  "browserStats": {
    "Chrome": 6,
    "Firefox": 3,
    "Safari": 1
  },
  "osStats": {
    "Windows 10": 5,
    "macOS": 3,
    "Linux": 2
  },
  "ipStats": {
    "192.168.1.100": 3,
    "192.168.1.101": 2,
    "192.168.1.102": 1
  }
}
```

#### 获取缓存统计
```http
GET /api/cache/stats/cache
```

响应示例：
```json
{
  "userInfoCacheCount": 10,
  "userRolesCacheCount": 10,
  "userPermissionsCacheCount": 10,
  "userAuthoritiesCacheCount": 10,
  "userMenusCacheCount": 10,
  "totalCacheSize": 50,
  "cacheExpireTime": "30分钟"
}
```

## 功能特性

### 1. 丰富的用户信息缓存
- 用户基本信息
- 角色信息
- 权限信息
- 菜单信息
- 登录信息（IP、浏览器、操作系统）

### 2. 在线用户管理
- 实时在线用户统计
- 用户会话信息查询
- 强制用户下线功能
- 批量用户管理

### 3. 权限缓存优化
- 分层缓存设计
- 自动缓存更新
- 缓存预热功能
- 过期缓存清理

### 4. 统计监控
- 在线用户统计
- 浏览器分布统计
- 操作系统分布统计
- IP地址分布统计
- 缓存使用统计

### 5. 性能优化
- 缓存预热
- 批量操作
- 过期清理
- 统计监控

## 使用示例

### 1. 用户登录时自动缓存
```java
// 在AuthController中，用户登录成功后自动更新缓存
userPermissionCacheService.updateUserLoginInfo(
    userDetails.getId(),
    getClientIpAddress(request),
    userAgent.getBrowser().getName(),
    userAgent.getOperatingSystem().getName()
);
```

### 2. 获取用户完整信息
```java
UserInfoVO userInfo = userPermissionCacheService.getUserInfo(userId);
```

### 3. 检查用户在线状态
```java
boolean isOnline = userPermissionCacheService.isUserOnline(userId);
```

### 4. 强制用户下线
```java
userPermissionCacheService.forceUserOffline(userId);
```

### 5. 获取在线用户统计
```java
Map<String, Object> stats = userPermissionCacheService.getUserLoginStats();
```

## 配置说明

### Redis配置
```properties
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.database=0
spring.data.redis.password=123456
spring.data.redis.timeout=10s
```

### 缓存配置
```java
// 缓存过期时间：30分钟
private static final long CACHE_EXPIRE_TIME = 30;
```

## 注意事项

1. **缓存一致性**：当用户权限发生变化时，需要及时清除相关缓存
2. **内存使用**：监控Redis内存使用情况，避免内存溢出
3. **性能监控**：定期检查缓存命中率和响应时间
4. **安全考虑**：敏感信息不要直接存储在缓存中
5. **数据备份**：重要缓存数据需要定期备份

## 扩展功能

### 1. 添加新的缓存类型
```java
private static final String USER_CUSTOM_CACHE_KEY = "user:custom:";
```

### 2. 自定义缓存策略
```java
public void setCustomCacheStrategy(String key, Object value, long expireTime) {
    redisTemplate.opsForValue().set(key, value, expireTime, TimeUnit.MINUTES);
}
```

### 3. 缓存事件监听
```java
@EventListener
public void handleCacheEvent(CacheEvent event) {
    // 处理缓存事件
}
```

这个Redis缓存系统提供了类似若依项目的丰富功能，包括用户信息缓存、在线用户管理、权限缓存、统计监控等，可以满足企业级应用的需求。 