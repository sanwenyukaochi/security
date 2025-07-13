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
public class DataInitializer implements CommandLineRunner {

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
        Tenant tenant = createTenant();

        // 创建用户
        User userAdmin = createUser("adminadmin", "12345678", "admin@example.com", "13800138001", tenant);
        User userTenant = createUser("tenant", "123456", "tenant@example.com", "13800138002", tenant);
        User userUser = createUser("user", "123456", "user@example.com", "13800138003", tenant);

        // 创建角色
        Role roleAdmin = createRole("admin", "系统管理员", 0, tenant);
        Role roleTenant = createRole("tenant", "租户管理员", 1, tenant);
        Role roleUser = createRole("user", "普通用户", 2, tenant);

        // 创建权限
        Permission userManagePermission = createPermission("user:manage", "用户管理", "/user", 1, 0L, "1", tenant);
        Permission userViewPermission = createPermission("user:view", "用户查看", "/user/view", 1, userManagePermission.getId(), "2", tenant);
        Permission userAddPermission = createPermission("user:add", "用户新增", "/user/add", 2, userManagePermission.getId(), "2", tenant);
        Permission userEditPermission = createPermission("user:edit", "用户编辑", "/user/edit", 3, userManagePermission.getId(), "2", tenant);
        Permission userDeletePermission = createPermission("user:delete", "用户删除", "/user/delete", 4, userManagePermission.getId(), "2", tenant);

        // 绑定用户和角色
        bindUserAndRole(userAdmin, roleAdmin, tenant);
        bindUserAndRole(userTenant, roleTenant, tenant);
        bindUserAndRole(userUser, roleUser, tenant);

        // 绑定角色和权限
        bindRoleAndPermission(roleAdmin, userManagePermission, tenant);
        bindRoleAndPermission(roleAdmin, userViewPermission, tenant);
        bindRoleAndPermission(roleAdmin, userAddPermission, tenant);
        bindRoleAndPermission(roleAdmin, userEditPermission, tenant);
        bindRoleAndPermission(roleAdmin, userDeletePermission, tenant);
        bindRoleAndPermission(roleTenant, userViewPermission, tenant);
        bindRoleAndPermission(roleTenant, userEditPermission, tenant);
        bindRoleAndPermission(roleUser, userViewPermission, tenant);

        // 打印初始化结果
        printInitializationResult(tenant, userAdmin, userTenant, userUser, roleAdmin, roleTenant, roleUser);
    }

    private Tenant createTenant() {
        return tenantRepository.findByName("测试组")
                .orElseGet(() -> {
                    Tenant newTenant = new Tenant();
                    newTenant.setId(snowflake.nextId());
                    newTenant.setName("测试组");
                    newTenant.setCode("test_group");
                    newTenant.setStatus(true);
                    newTenant.setCreatedBy(1L);
                    newTenant.setUpdatedBy(1L);
                    return tenantRepository.save(newTenant);
                });
    }

    private User createUser(String username, String password, String email, String phone, Tenant tenant) {
        return userRepository.findByUserName(username)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setId(snowflake.nextId());
                    newUser.setTenant(tenant);
                    newUser.setTenantId(tenant.getId());
                    newUser.setUserName(username);
                    newUser.setPassword(passwordEncoder.encode(password));
                    newUser.setEmail(email);
                    newUser.setPhone(phone);
                    newUser.setStatus(true);
                    newUser.setAccountNonExpired(true);
                    newUser.setAccountNonLocked(true);
                    newUser.setCredentialsNonExpired(true);
                    newUser.setCreatedBy(1L);
                    newUser.setUpdatedBy(1L);
                    return userRepository.save(newUser);
                });
    }

    private Role createRole(String code, String name, Integer dataScope, Tenant tenant) {
        return roleRepository.findByCode(code)
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setId(snowflake.nextId());
                    newRole.setTenant(tenant);
                    newRole.setTenantId(tenant.getId());
                    newRole.setName(name);
                    newRole.setCode(code);
                    newRole.setDataScope(dataScope);
                    newRole.setStatus(true);
                    newRole.setCreatedBy(1L);
                    newRole.setUpdatedBy(1L);
                    return roleRepository.save(newRole);
                });
    }

    private Permission createPermission(String code, String name, String path, Integer sort, Long parentId, String type, Tenant tenant) {
        return permissionRepository.findByCode(code)
                .orElseGet(() -> {
                    Permission newPermission = new Permission();
                    newPermission.setId(snowflake.nextId());
                    newPermission.setTenantId(tenant.getId());
                    newPermission.setParentId(parentId);
                    newPermission.setType(type);
                    newPermission.setName(name);
                    newPermission.setCode(code);
                    newPermission.setPath(path);
                    newPermission.setSort(sort);
                    newPermission.setVisible(true);
                    newPermission.setCreatedBy(1L);
                    newPermission.setUpdatedBy(1L);
                    return permissionRepository.save(newPermission);
                });
    }

    private void bindUserAndRole(User user, Role role, Tenant tenant) {
        userRoleRepository.findByUser_IdAndRole_Id(user.getId(), role.getId())
                .orElseGet(() -> {
                    UserRole newUserRole = new UserRole();
                    newUserRole.setId(snowflake.nextId());
                    newUserRole.setTenantId(tenant.getId());
                    newUserRole.setUser(user);
                    newUserRole.setRole(role);
                    return userRoleRepository.save(newUserRole);
                });
    }

    private void bindRoleAndPermission(Role role, Permission permission, Tenant tenant) {
        rolePermissionRepository.findByRole_IdAndPermission_Id(role.getId(), permission.getId())
                .orElseGet(() -> {
                    RolePermission newRolePermission = new RolePermission();
                    newRolePermission.setId(snowflake.nextId());
                    newRolePermission.setTenantId(tenant.getId());
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
        log.info("=== 初始化完成 ===");
    }
} 