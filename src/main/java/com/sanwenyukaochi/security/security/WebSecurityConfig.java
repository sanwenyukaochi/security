package com.sanwenyukaochi.security.security;

import cn.hutool.core.lang.Snowflake;
import com.sanwenyukaochi.security.entity.*;
import com.sanwenyukaochi.security.repository.*;
import com.sanwenyukaochi.security.security.jwt.AuthTokenFilter;
import com.sanwenyukaochi.security.security.jwt.AuthEntryPointJwt;
import com.sanwenyukaochi.security.security.service.UserDetailsServiceImpl;
import com.sanwenyukaochi.security.security.handler.CustomPermissionEvaluator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
public class WebSecurityConfig {
    
    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final CustomPermissionEvaluator customPermissionEvaluator;
    private final AuthEntryPointJwt unauthorizedHandler;
    private final Snowflake snowflake;
    
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        return new DaoAuthenticationProvider() {{
            setUserDetailsService(userDetailsServiceImpl);
            setPasswordEncoder(passwordEncoder());
        }};
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DefaultMethodSecurityExpressionHandler methodSecurityExpressionHandler() {
        DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
        handler.setPermissionEvaluator(customPermissionEvaluator);
        return handler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);
        http.exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler));
        http.sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.authorizeHttpRequests(
                auth -> auth.requestMatchers(
                    "/api/auth/**"
                ).permitAll()
        );
        http.authorizeHttpRequests(
                auth -> auth.requestMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/swagger-resources/**",
                    "/webjars/**"
            ).access((authentication, context) -> {
                String ip = context.getRequest().getRemoteAddr();
                boolean isLocalhost = "127.0.0.1".equals(ip) || "::1".equals(ip);
                log.debug("尝试从 IP: {}, 进行访问: {}", ip, isLocalhost);
                return new AuthorizationDecision(isLocalhost);
            })
            .anyRequest().authenticated()
        );
        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));
        return http.build();
    }
    
    @Bean
    @Profile({"dev"})
    public CommandLineRunner initData(TenantRepository tenantRepository, 
                                      UserRepository userRepository, 
                                      RoleRepository roleRepository,
                                      PermissionRepository permissionRepository,
                                      UserRoleRepository userRoleRepository,
                                      RolePermissionRepository rolePermissionRepository,
                                      PasswordEncoder passwordEncoder) {
        return args -> {

            Tenant tenant = tenantRepository.findByName("测试组")
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

            User userAdmin = userRepository.findByUserName("admin")
                    .orElseGet(() -> {
                        User newUser = new User();
                        newUser.setId(snowflake.nextId());
                        newUser.setTenant(tenant);
                        newUser.setTenantId(tenant.getId());
                        newUser.setUserName("admin");
                        newUser.setPassword(passwordEncoder.encode("123456"));
                        newUser.setEmail("admin@example.com");
                        newUser.setPhone("13800138001");
                        newUser.setStatus(true);
                        newUser.setAccountNonExpired(true);
                        newUser.setAccountNonLocked(true);
                        newUser.setCredentialsNonExpired(true);
                        newUser.setCreatedBy(1L);
                        newUser.setUpdatedBy(1L);
                        return userRepository.save(newUser);
                    });

            User userTenant = userRepository.findByUserName("tenant")
                    .orElseGet(() -> {
                        User newUser = new User();
                        newUser.setId(snowflake.nextId());
                        newUser.setTenant(tenant);
                        newUser.setTenantId(tenant.getId());
                        newUser.setUserName("tenant");
                        newUser.setPassword(passwordEncoder.encode("123456"));
                        newUser.setEmail("tenant@example.com");
                        newUser.setPhone("13800138002");
                        newUser.setStatus(true);
                        newUser.setAccountNonExpired(true);
                        newUser.setAccountNonLocked(true);
                        newUser.setCredentialsNonExpired(true);
                        newUser.setCreatedBy(1L);
                        newUser.setUpdatedBy(1L);
                        return userRepository.save(newUser);
                    });

            User userUser = userRepository.findByUserName("user")
                    .orElseGet(() -> {
                        User newUser = new User();
                        newUser.setId(snowflake.nextId());
                        newUser.setTenant(tenant);
                        newUser.setTenantId(tenant.getId());
                        newUser.setUserName("user");
                        newUser.setPassword(passwordEncoder.encode("123456"));
                        newUser.setEmail("user@example.com");
                        newUser.setPhone("13800138003");
                        newUser.setStatus(true);
                        newUser.setAccountNonExpired(true);
                        newUser.setAccountNonLocked(true);
                        newUser.setCredentialsNonExpired(true);
                        newUser.setCreatedBy(1L);
                        newUser.setUpdatedBy(1L);
                        return userRepository.save(newUser);
                    });

            Role roleAdmin = roleRepository.findByCode("admin")
                    .orElseGet(() -> {
                        Role newRole = new Role();
                        newRole.setId(snowflake.nextId());
                        newRole.setTenant(tenant);
                        newRole.setTenantId(tenant.getId());
                        newRole.setName("系统管理员");
                        newRole.setCode("admin");
                        newRole.setDataScope(0);
                        newRole.setStatus(true);
                        newRole.setCreatedBy(1L);
                        newRole.setUpdatedBy(1L);
                        return roleRepository.save(newRole);
                    });

            Role roleTenant = roleRepository.findByCode("tenant")
                    .orElseGet(() -> {
                        Role newRole = new Role();
                        newRole.setId(snowflake.nextId());
                        newRole.setTenant(tenant);
                        newRole.setTenantId(tenant.getId());
                        newRole.setName("租户管理员");
                        newRole.setCode("tenant");
                        newRole.setDataScope(1);
                        newRole.setStatus(true);
                        newRole.setCreatedBy(1L);
                        newRole.setUpdatedBy(1L);
                        return roleRepository.save(newRole);
                    });

            Role roleUser = roleRepository.findByCode("user")
                    .orElseGet(() -> {
                        Role newRole = new Role();
                        newRole.setId(snowflake.nextId());
                        newRole.setTenant(tenant);
                        newRole.setTenantId(tenant.getId());
                        newRole.setName("普通用户");
                        newRole.setCode("user");
                        newRole.setDataScope(2);
                        newRole.setStatus(true);
                        newRole.setCreatedBy(1L);
                        newRole.setUpdatedBy(1L);
                        return roleRepository.save(newRole);
                    });

            Permission userManagePermission = permissionRepository.findByCode("user:manage")
                    .orElseGet(() -> {
                        Permission newPermission = new Permission();
                        newPermission.setId(snowflake.nextId());
                        newPermission.setTenantId(tenant.getId());
                        newPermission.setParentId(0L);
                        newPermission.setType("1"); // 菜单
                        newPermission.setName("用户管理");
                        newPermission.setCode("user:manage");
                        newPermission.setPath("/user");
                        newPermission.setSort(1);
                        newPermission.setVisible(true);
                        newPermission.setCreatedBy(1L);
                        newPermission.setUpdatedBy(1L);
                        return permissionRepository.save(newPermission);
                    });

            Permission userViewPermission = permissionRepository.findByCode("user:view")
                    .orElseGet(() -> {
                        Permission newPermission = new Permission();
                        newPermission.setId(snowflake.nextId());
                        newPermission.setTenantId(tenant.getId());
                        newPermission.setParentId(userManagePermission.getId());
                        newPermission.setType("2"); // 按钮
                        newPermission.setName("用户查看");
                        newPermission.setCode("user:view");
                        newPermission.setPath("/user/view");
                        newPermission.setSort(1);
                        newPermission.setVisible(true);
                        newPermission.setCreatedBy(1L);
                        newPermission.setUpdatedBy(1L);
                        return permissionRepository.save(newPermission);
                    });

            Permission userAddPermission = permissionRepository.findByCode("user:add")
                    .orElseGet(() -> {
                        Permission newPermission = new Permission();
                        newPermission.setId(snowflake.nextId());
                        newPermission.setTenantId(tenant.getId());
                        newPermission.setParentId(userManagePermission.getId());
                        newPermission.setType("2"); // 按钮
                        newPermission.setName("用户新增");
                        newPermission.setCode("user:add");
                        newPermission.setPath("/user/add");
                        newPermission.setSort(2);
                        newPermission.setVisible(true);
                        newPermission.setCreatedBy(1L);
                        newPermission.setUpdatedBy(1L);
                        return permissionRepository.save(newPermission);
                    });

            Permission userEditPermission = permissionRepository.findByCode("user:edit")
                    .orElseGet(() -> {
                        Permission newPermission = new Permission();
                        newPermission.setId(snowflake.nextId());
                        newPermission.setTenantId(tenant.getId());
                        newPermission.setParentId(userManagePermission.getId());
                        newPermission.setType("2"); // 按钮
                        newPermission.setName("用户编辑");
                        newPermission.setCode("user:edit");
                        newPermission.setPath("/user/edit");
                        newPermission.setSort(3);
                        newPermission.setVisible(true);
                        newPermission.setCreatedBy(1L);
                        newPermission.setUpdatedBy(1L);
                        return permissionRepository.save(newPermission);
                    });

            Permission userDeletePermission = permissionRepository.findByCode("user:delete")
                    .orElseGet(() -> {
                        Permission newPermission = new Permission();
                        newPermission.setId(snowflake.nextId());
                        newPermission.setTenantId(tenant.getId());
                        newPermission.setParentId(userManagePermission.getId());
                        newPermission.setType("2"); // 按钮
                        newPermission.setName("用户删除");
                        newPermission.setCode("user:delete");
                        newPermission.setPath("/user/delete");
                        newPermission.setSort(4);
                        newPermission.setVisible(true);
                        newPermission.setCreatedBy(1L);
                        newPermission.setUpdatedBy(1L);
                        return permissionRepository.save(newPermission);
                    });

            bindUserAndRole(userRoleRepository, tenant, userAdmin, roleAdmin);
            bindUserAndRole(userRoleRepository, tenant, userTenant, roleTenant);
            bindUserAndRole(userRoleRepository, tenant, userUser, roleUser);

            bindRoleAndPermission(rolePermissionRepository, roleAdmin, userManagePermission, tenant);
            bindRoleAndPermission(rolePermissionRepository, roleAdmin, userViewPermission, tenant);
            bindRoleAndPermission(rolePermissionRepository, roleAdmin, userAddPermission, tenant);
            bindRoleAndPermission(rolePermissionRepository, roleAdmin, userEditPermission, tenant);
            bindRoleAndPermission(rolePermissionRepository, roleAdmin, userDeletePermission, tenant);
            bindRoleAndPermission(rolePermissionRepository, roleTenant, userViewPermission, tenant);
            bindRoleAndPermission(rolePermissionRepository, roleTenant, userEditPermission, tenant);
            bindRoleAndPermission(rolePermissionRepository, roleUser, userViewPermission, tenant);

            // 初始化完成
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
            log.info("🔑 测试账号 (密码均为: 123456):");
            log.info("   ├─ 系统管理员: admin");
            log.info("   ├─ 租户管理员: tenant");
            log.info("   └─ 普通用户: user");
            log.info("=== 初始化完成 ===");
        };
    }

    private void bindRoleAndPermission(RolePermissionRepository rolePermissionRepository, Role role, Permission Permission, Tenant tenant) {
        rolePermissionRepository.findByRole_IdAndPermission_Id(role.getId(), Permission.getId())
                .orElseGet(() -> {
                    RolePermission newRolePermission = new RolePermission();
                    newRolePermission.setId(snowflake.nextId());
                    newRolePermission.setTenantId(tenant.getId());
                    newRolePermission.setRole(role);
                    newRolePermission.setPermission(Permission);
                    return rolePermissionRepository.save(newRolePermission);
                });
    }

    private void bindUserAndRole(UserRoleRepository userRoleRepository, Tenant tenant, User user, Role role) {
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

}
