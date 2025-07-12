# RBAC权限系统使用说明

## 概述

本系统实现了基于角色的访问控制（RBAC）权限系统，包含以下核心组件：

- **用户（User）**: 系统用户
- **角色（Role）**: 用户角色，如管理员、普通用户、访客
- **权限（Permission）**: 具体操作权限，如用户查看、新增、编辑、删除
- **用户角色关联（UserRole）**: 用户与角色的多对多关系
- **角色权限关联（RolePermission）**: 角色与权限的多对多关系
- **租户（Tenant）**: 系统租户，用于隔离不同用户数据

## 系统架构

### 数据库表结构

1. **sys_users** - 用户表
2. **sys_roles** - 角色表
3. **sys_permissions** - 权限表
4. **sys_user_roles** - 用户角色关联表
5. **sys_role_permissions** - 角色权限关联表
6. **sys_tenants** - 租户表

### 核心服务

1. **UserPermissionService** - 权限查询服务
2. **UserPermissionCacheService** - 权限缓存服务
3. **CustomPermissionEvaluator** - 自定义权限评估器

## 测试用户

系统初始化时会创建以下测试用户：

| 用户名 | 密码 | 角色 | 权限 |
|--------|------|------|------|
| admin | 123456 | ADMIN | 所有权限（user:manage, user:view, user:add, user:edit, user:delete） |
| user | 123456 | USER | 查看权限（user:view） |
| guest | 123456 | GUEST | 无权限 |

## API接口

### 认证接口

#### 登录
```http
POST /api/auth/signin
Content-Type: application/json

{
  "username": "admin",
  "password": "123456"
}
```

#### 获取当前用户信息
```http
GET /api/auth/user
```

#### 登出
```http
POST /api/auth/signout
```

### 测试接口

#### 公开接口
```http
GET /api/test/public
```

#### 需要USER角色的接口
```http
GET /api/test/user
```

#### 需要ADMIN角色的接口
```http
GET /api/test/admin
```

#### 需要特定权限的接口
```http
GET /api/test/user:view    # 需要user:view权限
GET /api/test/user:add     # 需要user:add权限
GET /api/test/user:edit    # 需要user:edit权限
GET /api/test/user:delete  # 需要user:delete权限
GET /api/test/user:manage  # 需要user:manage权限
```

### 权限管理接口

#### 获取用户角色
```http
GET /api/permission/user/{userId}/roles
```

#### 获取用户权限
```http
GET /api/permission/user/{userId}/permissions
```

#### 获取用户所有权限（包括角色和权限）
```http
GET /api/permission/user/{userId}/authorities
```

#### 为用户分配角色
```http
POST /api/permission/user/{userId}/role/{roleId}
```

#### 移除用户角色
```http
DELETE /api/permission/user/{userId}/role/{roleId}
```

#### 为角色分配权限
```http
POST /api/permission/role/{roleId}/permission/{permissionId}
```

#### 移除角色权限
```http
DELETE /api/permission/role/{roleId}/permission/{permissionId}
```

#### 清除用户权限缓存
```http
POST /api/permission/cache/clear/user/{userId}
```

#### 清除所有权限缓存
```http
POST /api/permission/cache/clear/all
```

## 权限注解使用

### 角色控制
```java
@PreAuthorize("hasRole('ADMIN')")
@PreAuthorize("hasRole('USER')")
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
```

### 权限控制
```java
@PreAuthorize("hasAuthority('user:view')")
@PreAuthorize("hasAuthority('user:add')")
@PreAuthorize("hasAnyAuthority('user:view', 'user:add')")
```

### 组合控制
```java
@PreAuthorize("hasRole('ADMIN') and hasAuthority('user:edit')")
@PreAuthorize("hasRole('USER') or hasAuthority('user:view')")
```

## 权限缓存

系统使用Redis缓存用户权限信息，提高查询性能：

- 缓存时间：30分钟
- 缓存键格式：
  - 用户角色：`user:roles:{userId}`
  - 用户权限：`user:permissions:{userId}`
  - 用户所有权限：`user:authorities:{userId}`

当用户角色或权限发生变化时，系统会自动清除相关缓存。

## 权限流程

1. **用户登录** → 验证用户名密码
2. **加载用户权限** → 查询用户角色和权限
3. **生成JWT令牌** → 包含用户信息和权限
4. **请求拦截** → JWT过滤器验证令牌
5. **权限检查** → 根据注解检查用户权限
6. **访问控制** → 允许或拒绝访问

## 扩展说明

### 添加新权限

1. 在数据库中插入新权限记录
2. 为角色分配新权限
3. 在控制器方法上添加权限注解

### 添加新角色

1. 在数据库中插入新角色记录
2. 为角色分配相应权限
3. 为用户分配新角色

### 自定义权限评估

可以通过继承`CustomPermissionEvaluator`来实现自定义的权限评估逻辑。

## 注意事项

1. 权限变更后需要清除相关缓存
2. JWT令牌包含用户权限信息，令牌过期前权限变更不会生效
3. 建议在生产环境中使用HTTPS
4. 定期清理过期的权限缓存
5. 监控权限查询性能，适时调整缓存策略 