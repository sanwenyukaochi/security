package com.sanwenyukaochi.security.security;

import com.sanwenyukaochi.security.entity.*;
import com.sanwenyukaochi.security.repository.*;
import com.sanwenyukaochi.security.security.jwt.AuthTokenFilter;
import com.sanwenyukaochi.security.security.jwt.AuthEntryPointJwt;
import com.sanwenyukaochi.security.security.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

    private final AuthEntryPointJwt unauthorizedHandler;
    
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
                auth -> auth
            .requestMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/swagger-resources/**",
                    "/webjars/**"
            ).access((authentication, context) -> {
                String ip = context.getRequest().getRemoteAddr();
                boolean isLocalhost = "127.0.0.1".equals(ip) || "::1".equals(ip);
                log.debug("Access attempt from IP: {}, isLocalhost: {}", ip, isLocalhost);
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
    public CommandLineRunner initData(
            TenantRepository tenantRepository, 
            UserRepository userRepository, 
            RoleRepository roleRepository,
            PermissionRepository permissionRepository,
            UserRoleRepository userRoleRepository,
            RolePermissionRepository rolePermissionRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            // 1. 创建租户
            Tenant tenant = null;
            if (!tenantRepository.existsByName("测试组")) {
                tenant = new Tenant();
                tenant.setId(1L);
                tenant.setName("测试组");
                tenant.setCode("test_group");
                tenant.setStatus(true);
                tenant.setCreatedBy(1L);
                tenant.setUpdatedBy(1L);
                tenant = tenantRepository.save(tenant);
            } else {
                tenant = tenantRepository.findByName("测试组").get(0);
            }
            
            // 2. 创建角色
            Role adminRole = null;
            Role userRole = null;
            Role guestRole = null;
            
            if (!roleRepository.existsByCode("admin")) {
                adminRole = new Role();
                adminRole.setId(1L);
                adminRole.setTenant(tenant);
                adminRole.setTenantId(tenant.getId());
                adminRole.setName("系统管理员");
                adminRole.setCode("admin");
                adminRole.setDataScope(0); // 租户级别
                adminRole.setStatus(true);
                adminRole.setCreatedBy(1L);
                adminRole.setUpdatedBy(1L);
                adminRole = roleRepository.save(adminRole);
            } else {
                adminRole = roleRepository.findByCode("admin").orElseThrow();
            }
            
            if (!roleRepository.existsByCode("user")) {
                userRole = new Role();
                userRole.setId(2L);
                userRole.setTenant(tenant);
                userRole.setTenantId(tenant.getId());
                userRole.setName("普通用户");
                userRole.setCode("user");
                userRole.setDataScope(1); // 本人级别
                userRole.setStatus(true);
                userRole.setCreatedBy(1L);
                userRole.setUpdatedBy(1L);
                userRole = roleRepository.save(userRole);
            } else {
                userRole = roleRepository.findByCode("user").orElseThrow();
            }
            
            if (!roleRepository.existsByCode("guest")) {
                guestRole = new Role();
                guestRole.setId(3L);
                guestRole.setTenant(tenant);
                guestRole.setTenantId(tenant.getId());
                guestRole.setName("访客");
                guestRole.setCode("guest");
                guestRole.setDataScope(1); // 本人级别
                guestRole.setStatus(true);
                guestRole.setCreatedBy(1L);
                guestRole.setUpdatedBy(1L);
                guestRole = roleRepository.save(guestRole);
            } else {
                guestRole = roleRepository.findByCode("guest").orElseThrow();
            }
            
            // 3. 创建权限
            Permission userManagePermission = null;
            Permission userViewPermission = null;
            Permission userAddPermission = null;
            Permission userEditPermission = null;
            Permission userDeletePermission = null;
            
            if (!permissionRepository.existsByCode("user:manage")) {
                userManagePermission = new Permission();
                userManagePermission.setId(1L);
                userManagePermission.setTenantId(tenant.getId());
                userManagePermission.setParentId(0L);
                userManagePermission.setType("1"); // 菜单
                userManagePermission.setName("用户管理");
                userManagePermission.setCode("user:manage");
                userManagePermission.setPath("/user");
                userManagePermission.setSort(1);
                userManagePermission.setVisible(true);
                userManagePermission.setCreatedBy(1L);
                userManagePermission.setUpdatedBy(1L);
                userManagePermission = permissionRepository.save(userManagePermission);
            } else {
                userManagePermission = permissionRepository.findByCode("user:manage").orElseThrow();
            }
            
            if (!permissionRepository.existsByCode("user:view")) {
                userViewPermission = new Permission();
                userViewPermission.setId(2L);
                userViewPermission.setTenantId(tenant.getId());
                userViewPermission.setParentId(1L);
                userViewPermission.setType("2"); // 按钮
                userViewPermission.setName("用户查看");
                userViewPermission.setCode("user:view");
                userViewPermission.setPath("/user/view");
                userViewPermission.setSort(1);
                userViewPermission.setVisible(true);
                userViewPermission.setCreatedBy(1L);
                userViewPermission.setUpdatedBy(1L);
                userViewPermission = permissionRepository.save(userViewPermission);
            } else {
                userViewPermission = permissionRepository.findByCode("user:view").orElseThrow();
            }
            
            if (!permissionRepository.existsByCode("user:add")) {
                userAddPermission = new Permission();
                userAddPermission.setId(3L);
                userAddPermission.setTenantId(tenant.getId());
                userAddPermission.setParentId(1L);
                userAddPermission.setType("2"); // 按钮
                userAddPermission.setName("用户新增");
                userAddPermission.setCode("user:add");
                userAddPermission.setPath("/user/add");
                userAddPermission.setSort(2);
                userAddPermission.setVisible(true);
                userAddPermission.setCreatedBy(1L);
                userAddPermission.setUpdatedBy(1L);
                userAddPermission = permissionRepository.save(userAddPermission);
            } else {
                userAddPermission = permissionRepository.findByCode("user:add").orElseThrow();
            }
            
            if (!permissionRepository.existsByCode("user:edit")) {
                userEditPermission = new Permission();
                userEditPermission.setId(4L);
                userEditPermission.setTenantId(tenant.getId());
                userEditPermission.setParentId(1L);
                userEditPermission.setType("2"); // 按钮
                userEditPermission.setName("用户编辑");
                userEditPermission.setCode("user:edit");
                userEditPermission.setPath("/user/edit");
                userEditPermission.setSort(3);
                userEditPermission.setVisible(true);
                userEditPermission.setCreatedBy(1L);
                userEditPermission.setUpdatedBy(1L);
                userEditPermission = permissionRepository.save(userEditPermission);
            } else {
                userEditPermission = permissionRepository.findByCode("user:edit").orElseThrow();
            }
            
            if (!permissionRepository.existsByCode("user:delete")) {
                userDeletePermission = new Permission();
                userDeletePermission.setId(5L);
                userDeletePermission.setTenantId(tenant.getId());
                userDeletePermission.setParentId(1L);
                userDeletePermission.setType("2"); // 按钮
                userDeletePermission.setName("用户删除");
                userDeletePermission.setCode("user:delete");
                userDeletePermission.setPath("/user/delete");
                userDeletePermission.setSort(4);
                userDeletePermission.setVisible(true);
                userDeletePermission.setCreatedBy(1L);
                userDeletePermission.setUpdatedBy(1L);
                userDeletePermission = permissionRepository.save(userDeletePermission);
            } else {
                userDeletePermission = permissionRepository.findByCode("user:delete").orElseThrow();
            }
            
            // 4. 创建用户
            User adminUser = null;
            User normalUser = null;
            User guestUser = null;
            
            if (!userRepository.existsByUserName("admin")) {
                adminUser = new User();
                adminUser.setId(1L);
                adminUser.setTenant(tenant);
                adminUser.setTenantId(tenant.getId());
                adminUser.setUserName("admin");
                adminUser.setPassword(passwordEncoder.encode("123456"));
                adminUser.setEmail("admin@example.com");
                adminUser.setPhone("13800138001");
                adminUser.setStatus(true);
                adminUser.setAccountNonExpired(true);
                adminUser.setAccountNonLocked(true);
                adminUser.setCredentialsNonExpired(true);
                adminUser.setCreatedBy(1L);
                adminUser.setUpdatedBy(1L);
                adminUser = userRepository.save(adminUser);
            } else {
                adminUser = userRepository.findByUserName("admin").orElseThrow();
            }
            
            if (!userRepository.existsByUserName("user")) {
                normalUser = new User();
                normalUser.setId(2L);
                normalUser.setTenant(tenant);
                normalUser.setTenantId(tenant.getId());
                normalUser.setUserName("user");
                normalUser.setPassword(passwordEncoder.encode("123456"));
                normalUser.setEmail("user@example.com");
                normalUser.setPhone("13800138002");
                normalUser.setStatus(true);
                normalUser.setAccountNonExpired(true);
                normalUser.setAccountNonLocked(true);
                normalUser.setCredentialsNonExpired(true);
                normalUser.setCreatedBy(1L);
                normalUser.setUpdatedBy(1L);
                normalUser = userRepository.save(normalUser);
            } else {
                normalUser = userRepository.findByUserName("user").orElseThrow();
            }
            
            if (!userRepository.existsByUserName("guest")) {
                guestUser = new User();
                guestUser.setId(3L);
                guestUser.setTenant(tenant);
                guestUser.setTenantId(tenant.getId());
                guestUser.setUserName("guest");
                guestUser.setPassword(passwordEncoder.encode("123456"));
                guestUser.setEmail("guest@example.com");
                guestUser.setPhone("13800138003");
                guestUser.setStatus(true);
                guestUser.setAccountNonExpired(true);
                guestUser.setAccountNonLocked(true);
                guestUser.setCredentialsNonExpired(true);
                guestUser.setCreatedBy(1L);
                guestUser.setUpdatedBy(1L);
                guestUser = userRepository.save(guestUser);
            } else {
                guestUser = userRepository.findByUserName("guest").orElseThrow();
            }
            
            // 5. 分配用户角色
            if (!userRoleRepository.existsByUserIdAndRoleId(adminUser.getId(), adminRole.getId())) {
                UserRole adminUserRole = new UserRole();
                adminUserRole.setId(1L);
                adminUserRole.setTenantId(tenant.getId());
                adminUserRole.setUserId(adminUser.getId());
                adminUserRole.setRoleId(adminRole.getId());
                userRoleRepository.save(adminUserRole);
            }
            
            if (!userRoleRepository.existsByUserIdAndRoleId(normalUser.getId(), userRole.getId())) {
                UserRole normalUserRole = new UserRole();
                normalUserRole.setId(2L);
                normalUserRole.setTenantId(tenant.getId());
                normalUserRole.setUserId(normalUser.getId());
                normalUserRole.setRoleId(userRole.getId());
                userRoleRepository.save(normalUserRole);
            }
            
            if (!userRoleRepository.existsByUserIdAndRoleId(guestUser.getId(), guestRole.getId())) {
                UserRole guestUserRole = new UserRole();
                guestUserRole.setId(3L);
                guestUserRole.setTenantId(tenant.getId());
                guestUserRole.setUserId(guestUser.getId());
                guestUserRole.setRoleId(guestRole.getId());
                userRoleRepository.save(guestUserRole);
            }
            
            // 6. 分配角色权限
            // 管理员拥有所有权限
            if (!rolePermissionRepository.existsByRoleIdAndPermissionId(adminRole.getId(), userManagePermission.getId())) {
                RolePermission adminUserManage = new RolePermission();
                adminUserManage.setId(1L);
                adminUserManage.setTenantId(tenant.getId());
                adminUserManage.setRoleId(adminRole.getId());
                adminUserManage.setPermissionId(userManagePermission.getId());
                rolePermissionRepository.save(adminUserManage);
            }
            
            if (!rolePermissionRepository.existsByRoleIdAndPermissionId(adminRole.getId(), userViewPermission.getId())) {
                RolePermission adminUserView = new RolePermission();
                adminUserView.setId(2L);
                adminUserView.setTenantId(tenant.getId());
                adminUserView.setRoleId(adminRole.getId());
                adminUserView.setPermissionId(userViewPermission.getId());
                rolePermissionRepository.save(adminUserView);
            }
            
            if (!rolePermissionRepository.existsByRoleIdAndPermissionId(adminRole.getId(), userAddPermission.getId())) {
                RolePermission adminUserAdd = new RolePermission();
                adminUserAdd.setId(3L);
                adminUserAdd.setTenantId(tenant.getId());
                adminUserAdd.setRoleId(adminRole.getId());
                adminUserAdd.setPermissionId(userAddPermission.getId());
                rolePermissionRepository.save(adminUserAdd);
            }
            
            if (!rolePermissionRepository.existsByRoleIdAndPermissionId(adminRole.getId(), userEditPermission.getId())) {
                RolePermission adminUserEdit = new RolePermission();
                adminUserEdit.setId(4L);
                adminUserEdit.setTenantId(tenant.getId());
                adminUserEdit.setRoleId(adminRole.getId());
                adminUserEdit.setPermissionId(userEditPermission.getId());
                rolePermissionRepository.save(adminUserEdit);
            }
            
            if (!rolePermissionRepository.existsByRoleIdAndPermissionId(adminRole.getId(), userDeletePermission.getId())) {
                RolePermission adminUserDelete = new RolePermission();
                adminUserDelete.setId(5L);
                adminUserDelete.setTenantId(tenant.getId());
                adminUserDelete.setRoleId(adminRole.getId());
                adminUserDelete.setPermissionId(userDeletePermission.getId());
                rolePermissionRepository.save(adminUserDelete);
            }
            
            // 普通用户只有查看权限
            if (!rolePermissionRepository.existsByRoleIdAndPermissionId(userRole.getId(), userViewPermission.getId())) {
                RolePermission normalUserView = new RolePermission();
                normalUserView.setId(6L);
                normalUserView.setTenantId(tenant.getId());
                normalUserView.setRoleId(userRole.getId());
                normalUserView.setPermissionId(userViewPermission.getId());
                rolePermissionRepository.save(normalUserView);
            }
            
            // 访客没有任何权限
            log.info("RBAC初始化完成！");
            log.info("测试用户：");
            log.info("管理员 - 用户名: admin, 密码: 123456");
            log.info("普通用户 - 用户名: user, 密码: 123456");
            log.info("访客 - 用户名: guest, 密码: 123456");
        };
    }
    
    /*
    @Bean
    public CommandLineRunner initData(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Retrieve or create roles
            Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                    .orElseGet(() -> {
                        Role newUserRole = new Role(AppRole.ROLE_USER);
                        return roleRepository.save(newUserRole);
                    });

            Role sellerRole = roleRepository.findByRoleName(AppRole.ROLE_SELLER)
                    .orElseGet(() -> {
                        Role newSellerRole = new Role(AppRole.ROLE_SELLER);
                        return roleRepository.save(newSellerRole);
                    });

            Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                    .orElseGet(() -> {
                        Role newAdminRole = new Role(AppRole.ROLE_ADMIN);
                        return roleRepository.save(newAdminRole);
                    });

            Set<Role> userRoles = Set.of(userRole);
            Set<Role> sellerRoles = Set.of(sellerRole);
            Set<Role> adminRoles = Set.of(userRole, sellerRole, adminRole);


            // Create users if not already present
            if (!userRepository.existsByUserName("user1")) {
                User user1 = new User("user1", "user1@example.com", passwordEncoder.encode("password1"));
                userRepository.save(user1);
            }

            if (!userRepository.existsByUserName("seller1")) {
                User seller1 = new User("seller1", "seller1@example.com", passwordEncoder.encode("password2"));
                userRepository.save(seller1);
            }

            if (!userRepository.existsByUserName("admin")) {
                User admin = new User("admin", "admin@example.com", passwordEncoder.encode("adminPass"));
                userRepository.save(admin);
            }

            // Update roles for existing users
            userRepository.findByUserName("user1").ifPresent(user -> {
                user.setRoles(userRoles);
                userRepository.save(user);
            });

            userRepository.findByUserName("seller1").ifPresent(seller -> {
                seller.setRoles(sellerRoles);
                userRepository.save(seller);
            });

            userRepository.findByUserName("admin").ifPresent(admin -> {
                admin.setRoles(adminRoles);
                userRepository.save(admin);
            });
        };
    }*/
    
}
