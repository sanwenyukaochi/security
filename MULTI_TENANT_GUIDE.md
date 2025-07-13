# 多租户数据隔离实现方案

## 概述

本项目使用Hibernate的`@TenantId`注解实现多租户数据隔离，通过分区策略（DISCRIMINATOR）实现租户间的数据隔离。

## 核心组件

### 1. 租户上下文管理
- **TenantContext**: 使用ThreadLocal存储当前请求的租户ID
- **CurrentTenantIdentifierResolverImpl**: 实现Hibernate的租户标识符解析器

### 2. 多租户配置
- **MultiTenantConfig**: 配置Hibernate多租户相关属性
- **TenantAwareFilter**: 从请求中获取租户ID并设置到上下文

### 3. 实体设计
- **BaseIdEntity**: 包含`@TenantId`注解的`tenantId`字段
- 所有业务实体继承`BaseEntity`，自动获得租户隔离能力

## 使用方法

### 1. 通过请求头设置租户
```bash
curl -H "X-Tenant-ID: tenant1" http://localhost:8080/api/tenant-test/users
```

### 2. 通过请求参数设置租户
```bash
curl "http://localhost:8080/api/tenant-test/users?tenantId=tenant2"
```

### 3. 测试API接口

#### 获取当前租户信息
```bash
GET /api/tenant-test/current-tenant
```

#### 获取当前租户的用户列表
```bash
GET /api/tenant-test/users
```

#### 创建测试用户
```bash
POST /api/tenant-test/create-user?userName=test&email=test@example.com
```

## 数据隔离验证

### 1. 启动应用
应用启动时会自动初始化三个租户的数据：
- tenant1: 租户1
- tenant2: 租户2  
- tenant3: 租户3

### 2. 验证数据隔离
```bash
# 获取租户1的用户
curl -H "X-Tenant-ID: tenant1" http://localhost:8080/api/tenant-test/users

# 获取租户2的用户
curl -H "X-Tenant-ID: tenant2" http://localhost:8080/api/tenant-test/users

# 获取租户3的用户
curl -H "X-Tenant-ID: tenant3" http://localhost:8080/api/tenant-test/users
```

每个租户只能看到自己的数据，实现了完全的数据隔离。

## 技术实现细节

### 1. Hibernate多租户配置
```java
// 设置多租户策略为分区
hibernateProperties.put(Environment.MULTI_TENANCY, MultiTenancyStrategy.DISCRIMINATOR);

// 设置租户标识符解析器
hibernateProperties.put(Environment.MULTI_TENANT_IDENTIFIER_RESOLVER, currentTenantIdentifierResolver);
```

### 2. 实体设计
```java
@TenantId
@Column(name = "tenant_id", nullable = false)
private String tenantId;
```

### 3. 租户上下文管理
```java
// 设置租户上下文
TenantContext.setTenantId(tenantId);

// 获取当前租户
String currentTenant = TenantContext.getTenantId();

// 清理租户上下文
TenantContext.clear();
```

## 注意事项

1. **租户ID类型**: 使用String类型作为租户ID，便于扩展和管理
2. **默认租户**: 当请求中没有指定租户时，使用"default"作为默认租户
3. **数据隔离**: 所有继承BaseEntity的实体都会自动进行租户隔离
4. **事务管理**: 租户上下文在请求结束时自动清理，避免内存泄漏

## 扩展建议

1. **租户验证**: 可以添加租户有效性验证，确保请求的租户ID是有效的
2. **租户缓存**: 可以添加租户信息的缓存机制，提高性能
3. **租户权限**: 可以基于租户实现更细粒度的权限控制
4. **租户监控**: 可以添加租户使用情况的监控和统计 