# 多租户数据初始化指南

## 概述

本项目已经完成了多租户RBAC权限系统的数据初始化，创建了完整的多租户数据结构。

## 数据结构

### 1. 租户结构
- **管理员组** (admin_group): 系统管理员租户
- **租户A** (tenant_a): 第一个业务租户
- **租户B** (tenant_b): 第二个业务租户

### 2. 用户结构

#### 管理员组
- **admin**: 系统管理员 (密码: 123456)

#### 租户A
- **tenant_a_admin**: 租户A管理员 (密码: 123456)
- **tenant_a_user1**: 租户A普通用户1 (密码: 123456)
- **tenant_a_user2**: 租户A普通用户2 (密码: 123456)

#### 租户B
- **tenant_b_admin**: 租户B管理员 (密码: 123456)
- **tenant_b_user1**: 租户B普通用户1 (密码: 123456)
- **tenant_b_user2**: 租户B普通用户2 (密码: 123456)

### 3. 角色结构
- **admin**: 系统管理员 (拥有所有权限)
- **tenant**: 租户管理员 (拥有查看和编辑权限)
- **user**: 普通用户 (只有查看权限)

### 4. 权限结构
- **user:manage**: 用户管理
- **user:view**: 用户查看
- **user:add**: 用户新增
- **user:edit**: 用户编辑
- **user:delete**: 用户删除

### 5. 视频数据
每个普通用户都有一个名为"我的第一个视频"的视频：
- 租户A用户1: 我的第一个视频
- 租户A用户2: 我的第一个视频
- 租户B用户1: 我的第一个视频
- 租户B用户2: 我的第一个视频

## 测试方法

### 1. 登录测试

```bash
# 系统管理员登录
curl -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'

# 租户A管理员登录
curl -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{"username":"tenant_a_admin","password":"123456"}'

# 租户A普通用户1登录
curl -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{"username":"tenant_a_user1","password":"123456"}'

# 租户B管理员登录
curl -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{"username":"tenant_b_admin","password":"123456"}'

# 租户B普通用户1登录
curl -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{"username":"tenant_b_user1","password":"123456"}'
```

### 2. 数据隔离测试

登录后，使用返回的token进行测试：

```bash
# 获取当前租户的用户列表
curl -H "Authorization: Bearer YOUR_TOKEN" \
  http://localhost:8080/api/test/users

# 获取当前租户的视频列表
curl -H "Authorization: Bearer YOUR_TOKEN" \
  http://localhost:8080/api/test/videos

# 获取用户数量
curl -H "Authorization: Bearer YOUR_TOKEN" \
  http://localhost:8080/api/test/users/count

# 获取视频数量
curl -H "Authorization: Bearer YOUR_TOKEN" \
  http://localhost:8080/api/test/videos/count
```

### 3. 预期结果

#### 系统管理员 (admin)
- 用户数量: 7 (可以看到所有租户的用户)
- 视频数量: 4 (可以看到所有租户的视频)

#### 租户A管理员 (tenant_a_admin)
- 用户数量: 3 (只能看到租户A的用户)
- 视频数量: 2 (只能看到租户A的视频)

#### 租户A普通用户1 (tenant_a_user1)
- 用户数量: 3 (只能看到租户A的用户)
- 视频数量: 2 (只能看到租户A的视频)

#### 租户B管理员 (tenant_b_admin)
- 用户数量: 3 (只能看到租户B的用户)
- 视频数量: 2 (只能看到租户B的视频)

#### 租户B普通用户1 (tenant_b_user1)
- 用户数量: 3 (只能看到租户B的用户)
- 视频数量: 2 (只能看到租户B的视频)

## 权限验证

### 1. 系统管理员权限
```bash
# 测试用户管理权限
curl -H "Authorization: Bearer ADMIN_TOKEN" \
  http://localhost:8080/api/test/user:manage

# 测试用户查看权限
curl -H "Authorization: Bearer ADMIN_TOKEN" \
  http://localhost:8080/api/test/user:view

# 测试用户新增权限
curl -H "Authorization: Bearer ADMIN_TOKEN" \
  http://localhost:8080/api/test/user:add

# 测试用户编辑权限
curl -H "Authorization: Bearer ADMIN_TOKEN" \
  http://localhost:8080/api/test/user:edit

# 测试用户删除权限
curl -H "Authorization: Bearer ADMIN_TOKEN" \
  http://localhost:8080/api/test/user:delete
```

### 2. 租户管理员权限
```bash
# 测试用户查看权限
curl -H "Authorization: Bearer TENANT_ADMIN_TOKEN" \
  http://localhost:8080/api/test/user:view

# 测试用户编辑权限
curl -H "Authorization: Bearer TENANT_ADMIN_TOKEN" \
  http://localhost:8080/api/test/user:edit
```

### 3. 普通用户权限
```bash
# 测试用户查看权限
curl -H "Authorization: Bearer USER_TOKEN" \
  http://localhost:8080/api/test/user:view
```

## 注意事项

1. **数据隔离**: 每个租户只能看到自己租户内的数据
2. **权限控制**: 不同角色拥有不同的权限级别
3. **视频数据**: 所有普通用户都有相同名称的视频，但属于不同的租户
4. **登录验证**: 确保使用正确的用户名和密码
5. **Token使用**: 登录后需要使用返回的token进行后续请求

## 故障排除

### 1. 登录失败
- 检查用户名和密码是否正确
- 确认用户状态是否启用
- 查看服务器日志获取详细错误信息

### 2. 数据查询为空
- 确认是否使用了正确的租户上下文
- 检查用户是否属于正确的租户
- 验证多租户配置是否正确

### 3. 权限验证失败
- 确认用户角色是否正确分配
- 检查角色权限是否正确绑定
- 验证权限代码是否正确

## 扩展功能

### 1. 添加新租户
在`DataInitializerDev`中添加新的租户创建代码：

```java
Tenant tenantC = createTenant(snowflake.nextId(), "租户C", "tenant_c", true, creatorId, creatorId);
```

### 2. 添加新用户
```java
User tenantCUser = createUser(snowflake.nextId(), "tenant_c_user", "123456", 
    "tenant_c_user@example.com", "13800138008", true, true, true, true, tenantC, adminId, adminId);
```

### 3. 添加新视频
```java
createVideo(snowflake.nextId(), "我的第一个视频", tenantCUser, adminId);
```

### 4. 添加新权限
```java
Permission newPermission = createPermission(snowflake.nextId(), "video:manage", "视频管理", 
    "/video", 1, snowflake.nextId(), "1", true, adminTenant, adminId, adminId);
``` 