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

import java.util.Collections;
import java.util.Optional;

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

    
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRoleRepository userRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Bean
    // @Profile({"staging"})
    public CommandLineRunner initData() {
        return args -> {
            
            Optional<Tenant> tenant = Optional.ofNullable(tenantRepository.findByName("测试组"))
                    .orElseGet(() -> {
                        Tenant newTenant = new Tenant();
                        newTenant.setId(snowflake.nextId());
                        newTenant.setName("测试组");
                        newTenant.setCode("test_group");
                        newTenant.setStatus(true);
                        newTenant.setCreatedBy(1L);
                        newTenant.setUpdatedBy(1L);
                        return Optional.of(tenantRepository.save(newTenant));
                    });
            
            Optional<User> userAdmin = Optional.ofNullable(userRepository.findByUserName("admin"))
                    .orElseGet(() -> {
                        User newUser = new User();
                        newUser.setId(snowflake.nextId());
                        newUser.setTenant(tenant.orElseThrow());
                        newUser.setTenantId(tenant.orElseThrow().getId());
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
                        return Optional.of(userRepository.save(newUser));
                    }
            );

            Optional<User> userTenant = Optional.ofNullable(userRepository.findByUserName("tenant"))
                    .orElseGet(() -> {
                        User newUser = new User();
                        newUser.setId(snowflake.nextId());
                        newUser.setTenant(tenant.orElseThrow());
                        newUser.setTenantId(tenant.orElseThrow().getId());
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
                        return Optional.of(userRepository.save(newUser));
                    }
            );

            Optional<User> userUser = Optional.ofNullable(userRepository.findByUserName("user"))
                    .orElseGet(() -> {
                        User newUser = new User();
                        newUser.setId(snowflake.nextId());
                        newUser.setTenant(tenant.orElseThrow());
                        newUser.setTenantId(tenant.orElseThrow().getId());
                        newUser.setUserName("userUser");
                        newUser.setPassword(passwordEncoder.encode("123456"));
                        newUser.setEmail("userUser@example.com");
                        newUser.setPhone("13800138003");
                        newUser.setStatus(true);
                        newUser.setAccountNonExpired(true);
                        newUser.setAccountNonLocked(true);
                        newUser.setCredentialsNonExpired(true);
                        newUser.setCreatedBy(1L);
                        newUser.setUpdatedBy(1L);
                        return Optional.of(userRepository.save(newUser));
                    }
            );


            Optional<Role> roleAdmin = Optional.ofNullable(roleRepository.findByCode("admin"))
                    .orElseGet(() -> {
                                Role newRole = new Role();
                                newRole.setId(snowflake.nextId());
                                newRole = new Role();
                                newRole.setId(snowflake.nextId());
                                newRole.setTenant(tenant.orElseThrow());
                                newRole.setTenantId(tenant.orElseThrow().getId());
                                newRole.setName("系统管理员");
                                newRole.setCode("admin");
                                newRole.setDataScope(0);
                                newRole.setStatus(true);
                                newRole.setCreatedBy(1L);
                                newRole.setUpdatedBy(1L);
                                return Optional.of(roleRepository.save(newRole));
                            }
                    );


            Optional<Role> roleTenant = Optional.ofNullable(roleRepository.findByCode("tenant"))
                    .orElseGet(() -> {
                                Role newRole = new Role();
                                newRole.setId(snowflake.nextId());
                                newRole = new Role();
                                newRole.setId(snowflake.nextId());
                                newRole.setTenant(tenant.orElseThrow());
                                newRole.setTenantId(tenant.orElseThrow().getId());
                                newRole.setName("租户管理员");
                                newRole.setCode("tenant");
                                newRole.setDataScope(1);
                                newRole.setStatus(true);
                                newRole.setCreatedBy(1L);
                                newRole.setUpdatedBy(1L);
                                return Optional.of(roleRepository.save(newRole));
                            }
                    );
            
//            Permission userManagePermission = null;
//            Permission userViewPermission = null;
//            Permission userAddPermission = null;
//            Permission userEditPermission = null;
//            Permission userDeletePermission = null;
//            
//            if (!permissionRepository.existsByCode("user:manage")) {
//                userManagePermission = new Permission();
//                userManagePermission.setId(1L);
//                userManagePermission.setTenantId(tenant.getId());
//                userManagePermission.setParentId(0L);
//                userManagePermission.setType("1"); // 菜单
//                userManagePermission.setName("用户管理");
//                userManagePermission.setCode("user:manage");
//                userManagePermission.setPath("/user");
//                userManagePermission.setSort(1);
//                userManagePermission.setVisible(true);
//                userManagePermission.setCreatedBy(1L);
//                userManagePermission.setUpdatedBy(1L);
//                userManagePermission = permissionRepository.save(userManagePermission);
//            } else {
//                userManagePermission = permissionRepository.findByCode("user:manage").orElseThrow();
//            }
//            
//            if (!permissionRepository.existsByCode("user:view")) {
//                userViewPermission = new Permission();
//                userViewPermission.setId(2L);
//                userViewPermission.setTenantId(tenant.getId());
//                userViewPermission.setParentId(1L);
//                userViewPermission.setType("2"); // 按钮
//                userViewPermission.setName("用户查看");
//                userViewPermission.setCode("user:view");
//                userViewPermission.setPath("/user/view");
//                userViewPermission.setSort(1);
//                userViewPermission.setVisible(true);
//                userViewPermission.setCreatedBy(1L);
//                userViewPermission.setUpdatedBy(1L);
//                userViewPermission = permissionRepository.save(userViewPermission);
//            } else {
//                userViewPermission = permissionRepository.findByCode("user:view").orElseThrow();
//            }
//            
//            if (!permissionRepository.existsByCode("user:add")) {
//                userAddPermission = new Permission();
//                userAddPermission.setId(3L);
//                userAddPermission.setTenantId(tenant.getId());
//                userAddPermission.setParentId(1L);
//                userAddPermission.setType("2"); // 按钮
//                userAddPermission.setName("用户新增");
//                userAddPermission.setCode("user:add");
//                userAddPermission.setPath("/user/add");
//                userAddPermission.setSort(2);
//                userAddPermission.setVisible(true);
//                userAddPermission.setCreatedBy(1L);
//                userAddPermission.setUpdatedBy(1L);
//                userAddPermission = permissionRepository.save(userAddPermission);
//            } else {
//                userAddPermission = permissionRepository.findByCode("user:add").orElseThrow();
//            }
//            
//            if (!permissionRepository.existsByCode("user:edit")) {
//                userEditPermission = new Permission();
//                userEditPermission.setId(4L);
//                userEditPermission.setTenantId(tenant.getId());
//                userEditPermission.setParentId(1L);
//                userEditPermission.setType("2"); // 按钮
//                userEditPermission.setName("用户编辑");
//                userEditPermission.setCode("user:edit");
//                userEditPermission.setPath("/user/edit");
//                userEditPermission.setSort(3);
//                userEditPermission.setVisible(true);
//                userEditPermission.setCreatedBy(1L);
//                userEditPermission.setUpdatedBy(1L);
//                userEditPermission = permissionRepository.save(userEditPermission);
//            } else {
//                userEditPermission = permissionRepository.findByCode("user:edit").orElseThrow();
//            }
//            
//            if (!permissionRepository.existsByCode("user:delete")) {
//                userDeletePermission = new Permission();
//                userDeletePermission.setId(5L);
//                userDeletePermission.setTenantId(tenant.getId());
//                userDeletePermission.setParentId(1L);
//                userDeletePermission.setType("2"); // 按钮
//                userDeletePermission.setName("用户删除");
//                userDeletePermission.setCode("user:delete");
//                userDeletePermission.setPath("/user/delete");
//                userDeletePermission.setSort(4);
//                userDeletePermission.setVisible(true);
//                userDeletePermission.setCreatedBy(1L);
//                userDeletePermission.setUpdatedBy(1L);
//                userDeletePermission = permissionRepository.save(userDeletePermission);
//            } else {
//                userDeletePermission = permissionRepository.findByCode("user:delete").orElseThrow();
//            }
//            
//            // 4. 创建用户
//            User adminUser = null;
//            User normalUser = null;
//            User guestUser = null;
//            
//
//            
//
//            
//            // 5. 分配用户角色
//            if (!userRoleRepository.existsByUser_IdAndRole_Id(adminUser.getId(), adminRole.getId())) {
//                UserRole adminUserRole = new UserRole();
//                adminUserRole.setId(1L);
//                adminUserRole.setTenantId(tenant.getId());
//                adminUserRole.setUser(adminUser);
//                adminUserRole.setRole(adminRole);
//                userRoleRepository.save(adminUserRole);
//            }
//            
//            if (!userRoleRepository.existsByUser_IdAndRole_Id(normalUser.getId(), userRole.getId())) {
//                UserRole normalUserRole = new UserRole();
//                normalUserRole.setId(2L);
//                normalUserRole.setTenantId(tenant.getId());
//                normalUserRole.setUser(normalUser);
//                normalUserRole.setRole(userRole);
//                userRoleRepository.save(normalUserRole);
//            }
//            
//            if (!userRoleRepository.existsByUser_IdAndRole_Id(guestUser.getId(), guestRole.getId())) {
//                UserRole guestUserRole = new UserRole();
//                guestUserRole.setId(3L);
//                guestUserRole.setTenantId(tenant.getId());
//                guestUserRole.setUser(guestUser);
//                guestUserRole.setRole(guestRole);
//                userRoleRepository.save(guestUserRole);
//            }
//            
//            // 6. 分配角色权限
//            // 管理员拥有所有权限
//            if (!rolePermissionRepository.existsByRole_IdAndPermission_Id(adminRole.getId(), userManagePermission.getId())) {
//                RolePermission adminUserManage = new RolePermission();
//                adminUserManage.setId(1L);
//                adminUserManage.setTenantId(tenant.getId());
//                adminUserManage.setRole(adminRole);
//                adminUserManage.setPermission(userManagePermission);
//                rolePermissionRepository.save(adminUserManage);
//            }
//            
//            if (!rolePermissionRepository.existsByRole_IdAndPermission_Id(adminRole.getId(), userViewPermission.getId())) {
//                RolePermission adminUserView = new RolePermission();
//                adminUserView.setId(2L);
//                adminUserView.setTenantId(tenant.getId());
//                adminUserView.setRole(adminRole);
//                adminUserView.setPermission(userViewPermission);
//                rolePermissionRepository.save(adminUserView);
//            }
//            
//            if (!rolePermissionRepository.existsByRole_IdAndPermission_Id(adminRole.getId(), userAddPermission.getId())) {
//                RolePermission adminUserAdd = new RolePermission();
//                adminUserAdd.setId(3L);
//                adminUserAdd.setTenantId(tenant.getId());
//                adminUserAdd.setRole(adminRole);
//                adminUserAdd.setPermission(userAddPermission);
//                rolePermissionRepository.save(adminUserAdd);
//            }
//            
//            if (!rolePermissionRepository.existsByRole_IdAndPermission_Id(adminRole.getId(), userEditPermission.getId())) {
//                RolePermission adminUserEdit = new RolePermission();
//                adminUserEdit.setId(4L);
//                adminUserEdit.setTenantId(tenant.getId());
//                adminUserEdit.setRole(adminRole);
//                adminUserEdit.setPermission(userEditPermission);
//                rolePermissionRepository.save(adminUserEdit);
//            }
//            
//            if (!rolePermissionRepository.existsByRole_IdAndPermission_Id(adminRole.getId(), userDeletePermission.getId())) {
//                RolePermission adminUserDelete = new RolePermission();
//                adminUserDelete.setId(5L);
//                adminUserDelete.setTenantId(tenant.getId());
//                adminUserDelete.setRole(adminRole);
//                adminUserDelete.setPermission(userDeletePermission);
//                rolePermissionRepository.save(adminUserDelete);
//            }
//            
//            // 普通用户只有查看权限
//            if (!rolePermissionRepository.existsByRole_IdAndPermission_Id(userRole.getId(), userViewPermission.getId())) {
//                RolePermission normalUserView = new RolePermission();
//                normalUserView.setId(6L);
//                normalUserView.setTenantId(tenant.getId());
//                normalUserView.setRole(userRole);
//                normalUserView.setPermission(userViewPermission);
//                rolePermissionRepository.save(normalUserView);
//            }
            
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
