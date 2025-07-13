//package com.sanwenyukaochi.security.config;
//
//import com.sanwenyukaochi.security.context.TenantContext;
//import com.sanwenyukaochi.security.entity.*;
//import com.sanwenyukaochi.security.repository.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//import java.util.Arrays;
//import java.util.List;
//
///**
// * 数据初始化器
// * 初始化系统基础数据，支持多租户
// */
//@Component
//public class DataInitializer implements CommandLineRunner {
//    
//    @Autowired
//    private UserRepository userRepository;
//    
//    @Autowired
//    private RoleRepository roleRepository;
//    
//    @Autowired
//    private PermissionRepository permissionRepository;
//    
//    @Autowired
//    private UserRoleRepository userRoleRepository;
//    
//    @Autowired
//    private RolePermissionRepository rolePermissionRepository;
//    
//    @Autowired
//    private TenantRepository tenantRepository;
//    
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//    
//    @Override
//    public void run(String... args) throws Exception {
//        // 初始化租户数据
//        initializeTenants();
//        
//        // 为每个租户初始化数据
//        List<String> tenantIds = Arrays.asList("tenant1", "tenant2", "tenant3");
//        
//        for (String tenantId : tenantIds) {
//            TenantContext.setTenantId(tenantId);
//            initializeDataForTenant(tenantId);
//        }
//    }
//    
//    private void initializeTenants() {
//        // 创建租户
//        Tenant tenant1 = new Tenant();
//        tenant1.setId(1L);
//        tenant1.setTenantName("租户1");
//        tenant1.setTenantCode("tenant1");
//        tenant1.setStatus(true);
//        tenant1.setCreatedBy(1L);
//        tenant1.setCreatedAt(System.currentTimeMillis());
//        tenantRepository.save(tenant1);
//        
//        Tenant tenant2 = new Tenant();
//        tenant2.setId(2L);
//        tenant2.setTenantName("租户2");
//        tenant2.setTenantCode("tenant2");
//        tenant2.setStatus(true);
//        tenant2.setCreatedBy(1L);
//        tenant2.setCreatedAt(System.currentTimeMillis());
//        tenantRepository.save(tenant2);
//        
//        Tenant tenant3 = new Tenant();
//        tenant3.setId(3L);
//        tenant3.setTenantName("租户3");
//        tenant3.setTenantCode("tenant3");
//        tenant3.setStatus(true);
//        tenant3.setCreatedBy(1L);
//        tenant3.setCreatedAt(System.currentTimeMillis());
//        tenantRepository.save(tenant3);
//    }
//    
//    private void initializeDataForTenant(String tenantId) {
//        // 创建权限
//        Permission userPermission = createPermission("USER_READ", "用户读取权限", tenantId);
//        Permission userWritePermission = createPermission("USER_WRITE", "用户写入权限", tenantId);
//        Permission adminPermission = createPermission("ADMIN_ACCESS", "管理员权限", tenantId);
//        
//        // 创建角色
//        Role userRole = createRole("USER", "普通用户", tenantId);
//        Role adminRole = createRole("ADMIN", "管理员", tenantId);
//        
//        // 关联角色和权限
//        createRolePermission(userRole, userPermission, tenantId);
//        createRolePermission(adminRole, userPermission, tenantId);
//        createRolePermission(adminRole, userWritePermission, tenantId);
//        createRolePermission(adminRole, adminPermission, tenantId);
//        
//        // 创建用户
//        User adminUser = createUser("admin", "admin@example.com", "admin123", tenantId);
//        User normalUser = createUser("user", "user@example.com", "user123", tenantId);
//        
//        // 关联用户和角色
//        createUserRole(adminUser, adminRole, tenantId);
//        createUserRole(normalUser, userRole, tenantId);
//    }
//    
//    private Permission createPermission(String code, String name, String tenantId) {
//        Permission permission = new Permission();
//        permission.setPermissionCode(code);
//        permission.setPermissionName(name);
//        permission.setStatus(true);
//        permission.setCreatedBy(1L);
//        permission.setCreatedAt(System.currentTimeMillis());
//        permission.setTenantId(tenantId);
//        return permissionRepository.save(permission);
//    }
//    
//    private Role createRole(String roleCode, String roleName, String tenantId) {
//        Role role = new Role();
//        role.setRoleCode(roleCode);
//        role.setRoleName(roleName);
//        role.setStatus(true);
//        role.setCreatedBy(1L);
//        role.setCreatedAt(System.currentTimeMillis());
//        role.setTenantId(tenantId);
//        return roleRepository.save(role);
//    }
//    
//    private User createUser(String userName, String email, String password, String tenantId) {
//        User user = new User();
//        user.setUserName(userName);
//        user.setEmail(email);
//        user.setPassword(passwordEncoder.encode(password));
//        user.setPhone("13800138000");
//        user.setStatus(true);
//        user.setAccountNonExpired(true);
//        user.setAccountNonLocked(true);
//        user.setCredentialsNonExpired(true);
//        user.setCreatedBy(1L);
//        user.setCreatedAt(System.currentTimeMillis());
//        user.setTenantId(tenantId);
//        
//        // 设置租户关联
//        Tenant tenant = tenantRepository.findByTenantCode(tenantId);
//        user.setTenant(tenant);
//        
//        return userRepository.save(user);
//    }
//    
//    private void createRolePermission(Role role, Permission permission, String tenantId) {
//        RolePermission rolePermission = new RolePermission();
//        rolePermission.setRole(role);
//        rolePermission.setPermission(permission);
//        rolePermission.setCreatedBy(1L);
//        rolePermission.setCreatedAt(System.currentTimeMillis());
//        rolePermission.setTenantId(tenantId);
//        rolePermissionRepository.save(rolePermission);
//    }
//    
//    private void createUserRole(User user, Role role, String tenantId) {
//        UserRole userRole = new UserRole();
//        userRole.setUser(user);
//        userRole.setRole(role);
//        userRole.setCreatedBy(1L);
//        userRole.setCreatedAt(System.currentTimeMillis());
//        userRole.setTenantId(tenantId);
//        userRoleRepository.save(userRole);
//    }
//} 