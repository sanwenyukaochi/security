package com.sanwenyukaochi.security.security.initializer;

import cn.hutool.core.lang.Snowflake;
import com.sanwenyukaochi.security.entity.*;
import com.sanwenyukaochi.security.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile({"dev"})
@RequiredArgsConstructor
@Slf4j
public class DataInitializerDev implements CommandLineRunner {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRoleRepository userRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PasswordEncoder passwordEncoder;
    private final Snowflake snowflake;

    @Override
    public void run(String... args) {
        log.info("=== 开始初始化RBAC权限系统 ===");

        // 创建租户
        Long creatorId = snowflake.nextId();
        Tenant tenant = createTenant(snowflake.nextId(), "测试组", "test_group", true, creatorId, creatorId);

        // 创建默认管理员用户
        Long adminId = snowflake.nextId();
        User defaultAdmin = createUser(adminId, "adminadmin", "12345678", "admin@example.com", "13800138001", true, true, true, true , tenant, adminId, adminId);

        // 创建其他用户
        User userTenant = createUser(snowflake.nextId(), "tenant", "123456", "tenant@example.com", "13800138002", true, true, true, true, tenant, adminId, adminId);
        User userUser = createUser(snowflake.nextId(), "user", "123456", "user@example.com", "13800138003", true, true, true, true, tenant, adminId, adminId);

        // 创建角色
        Role roleAdmin = createRole(snowflake.nextId(), "admin", "系统管理员", 0, true, tenant, adminId, adminId);
        Role roleTenant = createRole(snowflake.nextId(), "tenant", "租户管理员", 1, true, tenant, adminId, adminId);
        Role roleUser = createRole(snowflake.nextId(), "user", "普通用户", 2, true, tenant, adminId, adminId);

        // 创建权限
        Permission userManagePermission = createPermission(snowflake.nextId(), "user:manage", "用户管理", "/user", 1, snowflake.nextId(), "1", true, tenant, adminId, adminId);
        Permission userViewPermission = createPermission(snowflake.nextId(), "user:view", "用户查看", "/user/view", 1, userManagePermission.getId(), "2", true, tenant, adminId, adminId);
        Permission userAddPermission = createPermission(snowflake.nextId(), "user:add", "用户新增", "/user/add", 2, userManagePermission.getId(), "2", true, tenant, adminId, adminId);
        Permission userEditPermission = createPermission(snowflake.nextId(), "user:edit", "用户编辑", "/user/edit", 3, userManagePermission.getId(), "2", true, tenant, adminId, adminId);
        Permission userDeletePermission = createPermission(snowflake.nextId(), "user:delete", "用户删除", "/user/delete", 4, userManagePermission.getId(), "2", true, tenant, adminId, adminId);

        // 绑定用户和角色
        bindUserAndRole(snowflake.nextId(), defaultAdmin, roleAdmin, tenant);
        bindUserAndRole(snowflake.nextId(), userTenant, roleTenant, tenant);
        bindUserAndRole(snowflake.nextId(), userUser, roleUser, tenant);

        // 绑定角色和权限
        bindRoleAndPermission(snowflake.nextId(), roleAdmin, userManagePermission, tenant);
        bindRoleAndPermission(snowflake.nextId(), roleAdmin, userViewPermission, tenant);
        bindRoleAndPermission(snowflake.nextId(), roleAdmin, userAddPermission, tenant);
        bindRoleAndPermission(snowflake.nextId(), roleAdmin, userEditPermission, tenant);
        bindRoleAndPermission(snowflake.nextId(), roleAdmin, userDeletePermission, tenant);
        bindRoleAndPermission(snowflake.nextId(), roleTenant, userViewPermission, tenant);
        bindRoleAndPermission(snowflake.nextId(), roleTenant, userEditPermission, tenant);
        bindRoleAndPermission(snowflake.nextId(), roleUser, userViewPermission, tenant);

        // 打印初始化结果
        printInitializationResult(tenant, defaultAdmin, userTenant, userUser, roleAdmin, roleTenant, roleUser);
    }

    private Tenant createTenant(Long id, String name, String code, Boolean status, Long createdBy, Long updatedBy) {
        return tenantRepository.findByName(name)
                .orElseGet(() -> {
                    Tenant newTenant = new Tenant();
                    newTenant.setId(id);
                    newTenant.setName(name);
                    newTenant.setCode(code);
                    newTenant.setStatus(status);
                    newTenant.setCreatedBy(createdBy);
                    newTenant.setUpdatedBy(updatedBy);
                    return tenantRepository.save(newTenant);
                });
    }

    private User createUser(Long id, String username, String password, String email, String phone, Boolean status, Boolean accountNonExpired, Boolean accountNonLocked, Boolean credentialsNonExpired, Tenant tenant, Long createdBy, Long updatedBy) {
        return userRepository.findByUserName(username)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setId(id);
                    newUser.setTenant(tenant);
                    newUser.setTenantId(tenant.getId().toString());
                    newUser.setUserName(username);
                    newUser.setPassword(passwordEncoder.encode(password));
                    newUser.setEmail(email);
                    newUser.setPhone(phone);
                    newUser.setStatus(status);
                    newUser.setAccountNonExpired(accountNonExpired);
                    newUser.setAccountNonLocked(accountNonLocked);
                    newUser.setCredentialsNonExpired(credentialsNonExpired);
                    newUser.setCreatedBy(createdBy);
                    newUser.setUpdatedBy(updatedBy);
                    return userRepository.save(newUser);
                });
    }

    private Role createRole(Long id, String code, String name, Integer dataScope, Boolean status, Tenant tenant, Long createdBy, Long updatedBy) {
        return roleRepository.findByCode(code)
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setId(id);
                    newRole.setTenant(tenant);
                    newRole.setTenantId(tenant.getId().toString());
                    newRole.setName(name);
                    newRole.setCode(code);
                    newRole.setDataScope(dataScope);
                    newRole.setStatus(status);
                    newRole.setCreatedBy(createdBy);
                    newRole.setUpdatedBy(updatedBy);
                    return roleRepository.save(newRole);
                });
    }

    private Permission createPermission(Long id, String code, String name, String path, Integer sort, Long parentId, String type, Boolean visible, Tenant tenant, Long createdBy, Long updatedBy) {
        return permissionRepository.findByCode(code)
                .orElseGet(() -> {
                    Permission newPermission = new Permission();
                    newPermission.setId(id);
                    newPermission.setTenantId(tenant.getId().toString());
                    newPermission.setParentId(parentId);
                    newPermission.setType(type);
                    newPermission.setName(name);
                    newPermission.setCode(code);
                    newPermission.setPath(path);
                    newPermission.setSort(sort);
                    newPermission.setVisible(visible);
                    newPermission.setCreatedBy(createdBy);
                    newPermission.setUpdatedBy(updatedBy);
                    return permissionRepository.save(newPermission);
                });
    }

    private void bindUserAndRole(Long id, User user, Role role, Tenant tenant) {
        userRoleRepository.findByUser_IdAndRole_Id(user.getId(), role.getId())
                .orElseGet(() -> {
                    UserRole newUserRole = new UserRole();
                    newUserRole.setId(id);
                    newUserRole.setTenantId(tenant.getId().toString());
                    newUserRole.setUser(user);
                    newUserRole.setRole(role);
                    return userRoleRepository.save(newUserRole);
                });
    }

    private void bindRoleAndPermission(Long id, Role role, Permission permission, Tenant tenant) {
        rolePermissionRepository.findByRole_IdAndPermission_Id(role.getId(), permission.getId())
                .orElseGet(() -> {
                    RolePermission newRolePermission = new RolePermission();
                    newRolePermission.setId(id);
                    newRolePermission.setTenantId(tenant.getId().toString());
                    newRolePermission.setRole(role);
                    newRolePermission.setPermission(permission);
                    return rolePermissionRepository.save(newRolePermission);
                });
    }

    private void printInitializationResult(Tenant tenant, User userAdmin, User userTenant, User userUser, 
                                        Role roleAdmin, Role roleTenant, Role roleUser) {
        log.info("=== RBAC权限系统初始化完成 ===");
        log.info("🗃️ 数据统计:");
        log.info("   ├─ 租户: {} (ID: {})", tenant.getName(), tenant.getId());
        log.info("   ├─ 用户: {} 个", 3);
        log.info("   ├─ 角色: {} 个", 3);
        log.info("   └─ 权限: {} 个", 5);
        log.info("");
        log.info("📒 用户信息:");
        log.info("   ├─ 系统管理员: {} (角色: {})", userAdmin.getUserName(), roleAdmin.getName());
        log.info("   ├─ 租户管理员: {} (角色: {})", userTenant.getUserName(), roleTenant.getName());
        log.info("   └─ 普通用户: {} (角色: {})", userUser.getUserName(), roleUser.getName());
        log.info("");
        log.info("🔐 权限分配:");
        log.info("   ├─ {}: 拥有所有权限 (管理/查看/新增/编辑/删除)", roleAdmin.getName());
        log.info("   ├─ {}: 拥有查看和编辑权限", roleTenant.getName());
        log.info("   └─ {}: 只有查看权限", roleUser.getName());
        log.info("");
        log.info("🔑 测试账号:");
        log.info("   ├─ 系统管理员: {} (密码: 12345678)", userAdmin.getUserName());
        log.info("   ├─ 租户管理员: {} (密码: 123456)", userTenant.getUserName());
        log.info("   └─ 普通用户: {} (密码: 123456)", userUser.getUserName());
        log.info("");
        log.info("👤 默认创建者: {} (ID: {})", userAdmin.getUserName(), userAdmin.getId());
        log.info("=== 初始化完成 ===");
    }
} 