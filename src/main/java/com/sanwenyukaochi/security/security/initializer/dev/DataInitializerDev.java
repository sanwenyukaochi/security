package com.sanwenyukaochi.security.security.initializer.dev;

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
    private final VideoRepository videoRepository;

    @Override
    public void run(String... args) {
        log.info("=== 开始初始化系统 ===");

        Long userAdminId = snowflake.nextId();
        // 1. 创建租户
        Tenant sysTenant = createTenant(snowflake.nextId(), "系统管理组", "system_group", true, userAdminId, userAdminId);
        Tenant tenantA = createTenant(snowflake.nextId(), "租户组A", "tenant_group_a", true, userAdminId, userAdminId);
        Tenant tenantB = createTenant(snowflake.nextId(), "租户组B", "tenant_group_b", true, userAdminId, userAdminId);

        // 2. 创建用户
        User userAdmin = createUser(userAdminId, "system_admin", "123456", "system_admin@sys.com", "13800000001", true, true, true, true, sysTenant, userAdminId, userAdminId);
        User userTenantA = createUser(snowflake.nextId(), "tenant_a_admin", "123456", "tenant_a_admin@a.com", "13800000002", true, true, true, true, tenantA, userAdmin.getId(), userAdmin.getId());
        User userTenantB = createUser(snowflake.nextId(), "tenant_b_admin", "123456", "tenant_b_admin@b.com", "13800000003", true, true, true, true, tenantB, userAdmin.getId(), userAdmin.getId());
        User userAA = createUser(snowflake.nextId(), "tenant_a_user_a", "123456", "user_a_a@a.com", "13800000004", true, true, true, true, tenantA, userTenantA.getId(), userTenantA.getId());
        User userAB = createUser(snowflake.nextId(), "tenant_a_user_b", "123456", "tenant_a_user_b@a.com", "13800000005", true, true, true, true, tenantA, userTenantA.getId(), userTenantA.getId());
        User userBA = createUser(snowflake.nextId(), "tenant_b_user_a", "123456", "tenant_b_user_a@b.com", "13800000006", true, true, true, true, tenantB, userTenantB.getId(), userTenantB.getId());
        User userBB = createUser(snowflake.nextId(), "tenant_b_user_b", "123456", "tenant_b_user_b@b.com", "13800000007", true, true, true, true, tenantB, userTenantB.getId(), userTenantB.getId());

        // 3. 创建角色
        Role roleAdmin = createRole(snowflake.nextId(), "admin", "系统管理员", 1, true, sysTenant, userAdmin.getId(), userAdmin.getId());
        Role roleTenantA = createRole(snowflake.nextId(), "tenant", "租户管理员", 2, true, tenantA, userAdmin.getId(), userAdmin.getId());
        Role roleTenantB = createRole(snowflake.nextId(), "tenant", "租户管理员", 2, true, tenantB, userAdmin.getId(), userAdmin.getId());
        Role roleUserA = createRole(snowflake.nextId(), "user", "普通员工", 3, true, tenantA, userTenantA.getId(), userTenantA.getId());
        Role roleUserB = createRole(snowflake.nextId(), "user", "普通员工", 3, true, tenantB, userTenantB.getId(), userTenantB.getId());

        // 创建权限
        Permission userManagePermission = createPermission(snowflake.nextId(), "test:user:manage", "用户管理(测试)", "/api/test/user", 1, 0L, "1", true, sysTenant, userAdminId, userAdminId);
        Permission userViewPermission = createPermission(snowflake.nextId(), "test:user:view", "用户查看(测试)", "/api/test/user/view", 1, userManagePermission.getId(), "2", true, sysTenant, userAdminId, userAdminId);
        Permission userAddPermission = createPermission(snowflake.nextId(), "test:user:add", "用户新增(测试)", "/api/test/user/add", 2, userManagePermission.getId(), "2", true, sysTenant, userAdminId, userAdminId);
        Permission userEditPermission = createPermission(snowflake.nextId(), "test:user:edit", "用户编辑(测试)", "/api/test/user/edit", 3, userManagePermission.getId(), "2", true, sysTenant, userAdminId, userAdminId);
        Permission userDeletePermission = createPermission(snowflake.nextId(), "test:user:delete", "用户删除(测试)", "/api/test/user/delete", 4, userManagePermission.getId(), "2", true, sysTenant, userAdminId, userAdminId);
        Permission videoManagePermission = createPermission(snowflake.nextId(), "video:video:manage", "视频管理", "/api/video", 1, 0L, "1", true, sysTenant, userAdminId, userAdminId);
        Permission videoUploadPermission = createPermission(snowflake.nextId(), "video:video:upload", "视频上传", "/api/video/upload", 1, videoManagePermission.getId(), "2", true, sysTenant, userAdminId, userAdminId);
        Permission videoViewPermission = createPermission(snowflake.nextId(), "video:video:view", "视频查看", "/api/video/view", 2, videoManagePermission.getId(), "2", true, sysTenant, userAdminId, userAdminId);
        Permission videoUpdatePermission = createPermission(snowflake.nextId(), "video:video:update", "视频重命名", "/api/video/rename", 2, videoManagePermission.getId(), "2", true, sysTenant, userAdminId, userAdminId);
        Permission videoDeletePermission = createPermission(snowflake.nextId(), "video:video:delete", "视频删除", "/api/video/delete", 3, videoManagePermission.getId(), "2", true, sysTenant, userAdminId, userAdminId);

        // 4. 绑定用户和角色
        bindUserAndRole(snowflake.nextId(), userAdmin, roleAdmin, sysTenant);
        bindUserAndRole(snowflake.nextId(), userTenantA, roleTenantA, tenantA);
        bindUserAndRole(snowflake.nextId(), userTenantB, roleTenantB, tenantB);
        bindUserAndRole(snowflake.nextId(), userAA, roleUserA, tenantA);
        bindUserAndRole(snowflake.nextId(), userAB, roleUserA, tenantA);
        bindUserAndRole(snowflake.nextId(), userBA, roleUserB, tenantB);
        bindUserAndRole(snowflake.nextId(), userBB, roleUserB, tenantB);

        // 6. 为每个用户创建一条视频数据
        createVideoForUser(snowflake.nextId(), "system_admin_video1", "mp4", 0L, 0.0, "http://videoPath.mp4", "http://cpverImage.jpg", false, false, sysTenant, userAdmin.getId(), userAdmin.getId());
        createVideoForUser(snowflake.nextId(), "tenant_a_admin_video1", "mp4", 0L, 0.0, "http://videoPath.mp4", "http://cpverImage.jpg", false, false, tenantA, userTenantA.getId(), userTenantA.getId());
        createVideoForUser(snowflake.nextId(), "tenant_b_admin_video1", "mp4", 0L, 0.0, "http://videoPath.mp4", "http://cpverImage.jpg", false, false, tenantB, userTenantB.getId(), userTenantB.getId());
        createVideoForUser(snowflake.nextId(), "tenant_a_user_a_video1", "mp4", 0L, 0.0, "http://videoPath.mp4", "http://cpverImage.jpg", false, false, tenantA, userAA.getId(), userAA.getId());
        createVideoForUser(snowflake.nextId(), "tenant_a_user_b_video1", "mp4", 0L, 0.0, "http://videoPath.mp4", "http://cpverImage.jpg", false, false, tenantA, userAB.getId(), userAB.getId());
        createVideoForUser(snowflake.nextId(), "tenant_b_user_a_video1", "mp4", 0L, 0.0, "http://videoPath.mp4", "http://cpverImage.jpg", false, false, tenantB, userBA.getId(), userBA.getId());
        createVideoForUser(snowflake.nextId(), "tenant_b_user_b_video1", "mp4", 0L, 0.0, "http://videoPath.mp4", "http://cpverImage.jpg", false, false, tenantB, userBB.getId(), userBB.getId());

        // 绑定角色和权限
        bindRoleAndPermission(snowflake.nextId(), roleAdmin, userManagePermission, sysTenant);
        bindRoleAndPermission(snowflake.nextId(), roleAdmin, userViewPermission, sysTenant);
        bindRoleAndPermission(snowflake.nextId(), roleAdmin, userAddPermission, sysTenant);
        bindRoleAndPermission(snowflake.nextId(), roleAdmin, userEditPermission, sysTenant);
        bindRoleAndPermission(snowflake.nextId(), roleAdmin, userDeletePermission, sysTenant);
        bindRoleAndPermission(snowflake.nextId(), roleAdmin, videoManagePermission, sysTenant);
        bindRoleAndPermission(snowflake.nextId(), roleAdmin, videoUploadPermission, sysTenant);
        bindRoleAndPermission(snowflake.nextId(), roleAdmin, videoViewPermission, sysTenant);
        bindRoleAndPermission(snowflake.nextId(), roleAdmin, videoUpdatePermission, sysTenant);
        bindRoleAndPermission(snowflake.nextId(), roleAdmin, videoDeletePermission, sysTenant);
        bindRoleAndPermission(snowflake.nextId(), roleTenantA, userViewPermission, tenantA);
        bindRoleAndPermission(snowflake.nextId(), roleTenantA, userEditPermission, tenantA);
        bindRoleAndPermission(snowflake.nextId(), roleTenantB, userViewPermission, tenantB);
        bindRoleAndPermission(snowflake.nextId(), roleTenantB, userEditPermission, tenantB);
        bindRoleAndPermission(snowflake.nextId(), roleUserA, userViewPermission, tenantA);
        bindRoleAndPermission(snowflake.nextId(), roleUserB, userViewPermission, tenantB);
        
        // 5. 打印初始化结果
        printInitializationResult(sysTenant, tenantA, tenantB, userAdmin, userTenantA, userTenantB, userAA, userAB, userBA, userBB, roleAdmin, roleTenantA, roleTenantB, roleUserA, roleUserB);
    }

    private Tenant createTenant(Long id, String name, String code, Boolean status, Long createdBy, Long updatedBy) {
        return tenantRepository.findByName(name)
                .orElseGet(() -> {
                    Tenant newTenant = new Tenant();
                    newTenant.setId(id);
                    newTenant.setTenantId(id); // 注意: 初始化 id
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
                    newUser.setUserName(username);
                    newUser.setPassword(passwordEncoder.encode(password));
                    newUser.setEmail(email);
                    newUser.setPhone(phone);
                    newUser.setStatus(status);
                    newUser.setAccountNonExpired(accountNonExpired);
                    newUser.setAccountNonLocked(accountNonLocked);
                    newUser.setCredentialsNonExpired(credentialsNonExpired);
                    newUser.setTenantId(tenant.getId());
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
                    newRole.setName(name);
                    newRole.setCode(code);
                    newRole.setDataScope(dataScope);
                    newRole.setStatus(status);
                    newRole.setTenantId(tenant.getId());
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
                    newPermission.setParentId(parentId);
                    newPermission.setType(type);
                    newPermission.setName(name);
                    newPermission.setCode(code);
                    newPermission.setPath(path);
                    newPermission.setSort(sort);
                    newPermission.setVisible(visible);
                    newPermission.setTenantId(tenant.getId());
                    newPermission.setCreatedBy(createdBy);
                    newPermission.setUpdatedBy(updatedBy);
                    return permissionRepository.save(newPermission);
                });
    }

    private void bindUserAndRole(Long id, User user, Role role, Tenant tenant) {
        userRoleRepository.findByUserAndRole(user, role)
                .orElseGet(() -> {
                    UserRole newUserRole = new UserRole();
                    newUserRole.setId(id);
                    newUserRole.setUser(user);
                    newUserRole.setRole(role);
                    newUserRole.setTenantId(tenant.getId());
                    return userRoleRepository.save(newUserRole);
                });
    }

    private void bindRoleAndPermission(Long id, Role role, Permission permission, Tenant tenant) {
        rolePermissionRepository.findByRoleAndPermission(role, permission)
                .orElseGet(() -> {
                    RolePermission newRolePermission = new RolePermission();
                    newRolePermission.setId(id);
                    newRolePermission.setTenantId(tenant.getId());
                    newRolePermission.setRole(role);
                    newRolePermission.setPermission(permission);
                    return rolePermissionRepository.save(newRolePermission);
                });
    }

    private void createVideoForUser(Long id, String fileName, String fileExt, Long fileSize, Double duration, String videoPath, String coverImage, Boolean hasClips, Boolean hasOutline, Tenant tenant, Long createdBy, Long updatedBy) {
        videoRepository.findByFileNameAndFileExt(fileName, fileExt)
                .orElseGet(() -> {
                    Video video = new Video();
                    video.setId(id);
                    video.setFileName(fileName);
                    video.setFileExt(fileExt);
                    video.setFileSize(fileSize);
                    video.setDuration(duration);
                    video.setVideoPath(videoPath);
                    video.setCoverImage(coverImage);
                    video.setHasClips(hasClips);
                    video.setHasOutline(hasOutline);
                    video.setTenantId(tenant.getId());
                    video.setCreatedBy(createdBy);
                    video.setUpdatedBy(updatedBy);
                    return videoRepository.save(video);
                });
    }

    private void printInitializationResult(Tenant sysTenant, Tenant tenantA, Tenant tenantB, User userAdmin, User userTenantA, User userTenantB, User userAA, User userAB, User userBA, User userBB, Role roleAdmin, Role roleTenantA, Role roleTenantB, Role roleUserA, Role roleUserB) {
        log.info("=== 系统初始化完成 ===");
        log.info("系统管理组: {} (ID: {})", sysTenant.getName(), sysTenant.getId());
        log.info("  └─ 用户: {} (角色: {})", userAdmin.getUserName(), roleAdmin.getName());
        log.info("租户组A: {} (ID: {})", tenantA.getName(), tenantA.getId());
        log.info("  ├─ 租户管理员: {} (角色: {})", userTenantA.getUserName(), roleTenantA.getName());
        log.info("  ├─ 普通员工: {} (角色: {})", userAA.getUserName(), roleUserA.getName());
        log.info("  └─ 普通员工: {} (角色: {})", userAB.getUserName(), roleUserA.getName());
        log.info("租户组B: {} (ID: {})", tenantB.getName(), tenantB.getId());
        log.info("  ├─ 租户管理员: {} (角色: {})", userTenantB.getUserName(), roleTenantB.getName());
        log.info("  ├─ 普通员工: {} (角色: {})", userBA.getUserName(), roleUserB.getName());
        log.info("  └─ 普通员工: {} (角色: {})", userBB.getUserName(), roleUserB.getName());
        log.info("=== 初始化完成 ===");
    }
}