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
public class DataInitializerDevBak implements CommandLineRunner {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRoleRepository userRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final VideoRepository videoRepository;
    private final PasswordEncoder passwordEncoder;
    private final Snowflake snowflake;

    @Override
    public void run(String... args) {
        log.info("=== 开始初始化多租户RBAC权限系统 ===");

        // 1. 创建管理员组租户
        Long creatorId = snowflake.nextId();
        Long l = snowflake.nextId();
        Tenant adminTenant = createTenant(l, "管理员组", "admin_group", true, creatorId, creatorId,l.toString());

        // 2. 创建租户A和租户B
        Long la = snowflake.nextId();
        Long lb = snowflake.nextId();
        Tenant tenantA = createTenant(la, "租户A", "tenant_a", true, creatorId, creatorId,la.toString());
        Tenant tenantB = createTenant(lb, "租户B", "tenant_b", true, creatorId, creatorId, lb.toString());

        // 3. 创建管理员用户
        Long adminId = snowflake.nextId();
        User adminUser = createUser(adminId, "admin", "123456", "admin@example.com", "13800138001", true, true, true, true, adminTenant, adminId, adminId);

        // 4. 创建租户A的管理员和普通用户
        User tenantAAdmin = createUser(snowflake.nextId(), "tenant_a_admin", "123456", "tenant_a_admin@example.com", "13800138002", true, true, true, true, tenantA, adminId, adminId);
        User tenantAUser1 = createUser(snowflake.nextId(), "tenant_a_user1", "123456", "tenant_a_user1@example.com", "13800138003", true, true, true, true, tenantA, adminId, adminId);
        User tenantAUser2 = createUser(snowflake.nextId(), "tenant_a_user2", "123456", "tenant_a_user2@example.com", "13800138004", true, true, true, true, tenantA, adminId, adminId);

        // 5. 创建租户B的管理员和普通用户
        User tenantBAdmin = createUser(snowflake.nextId(), "tenant_b_admin", "123456", "tenant_b_admin@example.com", "13800138005", true, true, true, true, tenantB, adminId, adminId);
        User tenantBUser1 = createUser(snowflake.nextId(), "tenant_b_user1", "123456", "tenant_b_user1@example.com", "13800138006", true, true, true, true, tenantB, adminId, adminId);
        User tenantBUser2 = createUser(snowflake.nextId(), "tenant_b_user2", "123456", "tenant_b_user2@example.com", "13800138007", true, true, true, true, tenantB, adminId, adminId);

        // 6. 创建角色
        Role roleAdmin = createRole(snowflake.nextId(), "admin", "系统管理员", 0, true, adminTenant, adminId, adminId);
        Role roleTenantA = createRole(snowflake.nextId(), "tenant", "租户管理员", 1, true, tenantA, adminId, adminId);
        Role roleUserA = createRole(snowflake.nextId(), "user", "普通用户", 2, true, tenantA, adminId, adminId);
        Role roleTenantB = createRole(snowflake.nextId(), "tenant", "租户管理员", 1, true, tenantB, adminId, adminId);
        Role roleUserB = createRole(snowflake.nextId(), "user", "普通用户", 2, true, tenantB, adminId, adminId);

        // 7. 创建权限
        Permission userManagePermission = createPermission(snowflake.nextId(), "user:manage", "用户管理", "/user", 1, snowflake.nextId(), "1", true, adminTenant, adminId, adminId);
        Permission userViewPermission = createPermission(snowflake.nextId(), "user:view", "用户查看", "/user/view", 1, userManagePermission.getId(), "2", true, adminTenant, adminId, adminId);
        Permission userAddPermission = createPermission(snowflake.nextId(), "user:add", "用户新增", "/user/add", 2, userManagePermission.getId(), "2", true, adminTenant, adminId, adminId);
        Permission userEditPermission = createPermission(snowflake.nextId(), "user:edit", "用户编辑", "/user/edit", 3, userManagePermission.getId(), "2", true, adminTenant, adminId, adminId);
        Permission userDeletePermission = createPermission(snowflake.nextId(), "user:delete", "用户删除", "/user/delete", 4, userManagePermission.getId(), "2", true, adminTenant, adminId, adminId);

        // 8. 绑定用户和角色
        bindUserAndRole(snowflake.nextId(), adminUser, roleAdmin, adminTenant);
        bindUserAndRole(snowflake.nextId(), tenantAAdmin, roleTenantA, tenantA);
        bindUserAndRole(snowflake.nextId(), tenantAUser1, roleUserA, tenantA);
        bindUserAndRole(snowflake.nextId(), tenantAUser2, roleUserA, tenantA);
        bindUserAndRole(snowflake.nextId(), tenantBAdmin, roleTenantB, tenantB);
        bindUserAndRole(snowflake.nextId(), tenantBUser1, roleUserB, tenantB);
        bindUserAndRole(snowflake.nextId(), tenantBUser2, roleUserB, tenantB);

        // 9. 绑定角色和权限
        bindRoleAndPermission(snowflake.nextId(), roleAdmin, userManagePermission, adminTenant);
        bindRoleAndPermission(snowflake.nextId(), roleAdmin, userViewPermission, adminTenant);
        bindRoleAndPermission(snowflake.nextId(), roleAdmin, userAddPermission, adminTenant);
        bindRoleAndPermission(snowflake.nextId(), roleAdmin, userEditPermission, adminTenant);
        bindRoleAndPermission(snowflake.nextId(), roleAdmin, userDeletePermission, adminTenant);
        bindRoleAndPermission(snowflake.nextId(), roleTenantA, userViewPermission, tenantA);
        bindRoleAndPermission(snowflake.nextId(), roleTenantA, userEditPermission, tenantA);
        bindRoleAndPermission(snowflake.nextId(), roleUserA, userViewPermission, tenantA);
        bindRoleAndPermission(snowflake.nextId(), roleTenantB, userViewPermission, tenantB);
        bindRoleAndPermission(snowflake.nextId(), roleTenantB, userEditPermission, tenantB);
        bindRoleAndPermission(snowflake.nextId(), roleUserB, userViewPermission, tenantB);

        // 10. 创建视频数据
//        createVideo(snowflake.nextId(), "我的第一个视频", tenantAUser1, tenantA, adminId, adminId);
//        createVideo(snowflake.nextId(), "我的第一个视频", tenantAUser2, tenantA, adminId, adminId);
//        createVideo(snowflake.nextId(), "我的第一个视频", tenantBUser1, tenantB, adminId, adminId);
//        createVideo(snowflake.nextId(), "我的第一个视频", tenantBUser2, tenantB, adminId, adminId);

        // 11. 打印初始化结果
        printInitializationResult(adminTenant, tenantA, tenantB, adminUser, tenantAAdmin, tenantAUser1, tenantAUser2, tenantBAdmin, tenantBUser1, tenantBUser2, roleAdmin, roleTenantA, roleUserA);
    }

    private Tenant createTenant(Long id, String name, String code, Boolean status, Long createdBy, Long updatedBy, String tenantId) {
        return tenantRepository.findByName(name)
                .orElseGet(() -> {
                    Tenant newTenant = new Tenant();
                    newTenant.setId(id);
                    newTenant.setName(name);
                    newTenant.setCode(code);
                    newTenant.setStatus(status);
                    newTenant.setCreatedBy(createdBy);
                    newTenant.setUpdatedBy(updatedBy);
                    newTenant.setTenantId(tenantId);
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

    private void createVideo(Long id, String name, User user, Tenant tenant, Long createdBy, Long updatedBy) {
        videoRepository.findByNameAndCreatedBy(name, user.getId())
                .orElseGet(() -> {
                    Video newVideo = new Video();
                    newVideo.setId(id);
                    newVideo.setName(name);
                    newVideo.setTenantId(tenant.getId().toString());
                    newVideo.setCreatedBy(createdBy);
                    newVideo.setUpdatedBy(updatedBy);
                    return videoRepository.save(newVideo);
                });
    }

    private void printInitializationResult(Tenant adminTenant, Tenant tenantA, Tenant tenantB, 
                                        User adminUser, User tenantAAdmin, User tenantAUser1, User tenantAUser2,
                                        User tenantBAdmin, User tenantBUser1, User tenantBUser2,
                                        Role roleAdmin, Role roleTenant, Role roleUser) {
        log.info("=== 多租户RBAC权限系统初始化完成 ===");
        log.info("🗃️ 数据统计:");
        log.info("   ├─ 租户: {} 个", 3);
        log.info("   │  ├─ {} (ID: {})", adminTenant.getName(), adminTenant.getId());
        log.info("   │  ├─ {} (ID: {})", tenantA.getName(), tenantA.getId());
        log.info("   │  └─ {} (ID: {})", tenantB.getName(), tenantB.getId());
        log.info("   ├─ 用户: {} 个", 7);
        log.info("   ├─ 角色: {} 个", 3);
        log.info("   ├─ 权限: {} 个", 5);
        log.info("   └─ 视频: {} 个", 4);
        log.info("");
        log.info("📒 用户信息:");
        log.info("   ├─ 系统管理员: {} (角色: {})", adminUser.getUserName(), roleAdmin.getName());
        log.info("   ├─ 租户A管理员: {} (角色: {})", tenantAAdmin.getUserName(), roleTenant.getName());
        log.info("   ├─ 租户A用户1: {} (角色: {})", tenantAUser1.getUserName(), roleUser.getName());
        log.info("   ├─ 租户A用户2: {} (角色: {})", tenantAUser2.getUserName(), roleUser.getName());
        log.info("   ├─ 租户B管理员: {} (角色: {})", tenantBAdmin.getUserName(), roleTenant.getName());
        log.info("   ├─ 租户B用户1: {} (角色: {})", tenantBUser1.getUserName(), roleUser.getName());
        log.info("   └─ 租户B用户2: {} (角色: {})", tenantBUser2.getUserName(), roleUser.getName());
        log.info("");
        log.info("🔐 权限分配:");
        log.info("   ├─ {}: 拥有所有权限 (管理/查看/新增/编辑/删除)", roleAdmin.getName());
        log.info("   ├─ {}: 拥有查看和编辑权限", roleTenant.getName());
        log.info("   └─ {}: 只有查看权限", roleUser.getName());
        log.info("");
        log.info("🎬 视频数据:");
        log.info("   ├─ 租户A用户1: 我的第一个视频");
        log.info("   ├─ 租户A用户2: 我的第一个视频");
        log.info("   ├─ 租户B用户1: 我的第一个视频");
        log.info("   └─ 租户B用户2: 我的第一个视频");
        log.info("");
        log.info("🔑 测试账号:");
        log.info("   ├─ 系统管理员: {} (密码: 123456)", adminUser.getUserName());
        log.info("   ├─ 租户A管理员: {} (密码: 123456)", tenantAAdmin.getUserName());
        log.info("   ├─ 租户A用户1: {} (密码: 123456)", tenantAUser1.getUserName());
        log.info("   ├─ 租户A用户2: {} (密码: 123456)", tenantAUser2.getUserName());
        log.info("   ├─ 租户B管理员: {} (密码: 123456)", tenantBAdmin.getUserName());
        log.info("   ├─ 租户B用户1: {} (密码: 123456)", tenantBUser1.getUserName());
        log.info("   └─ 租户B用户2: {} (密码: 123456)", tenantBUser2.getUserName());
        log.info("");
        log.info("👤 默认创建者: {} (ID: {})", adminUser.getUserName(), adminUser.getId());
        log.info("=== 初始化完成 ===");
    }
} 