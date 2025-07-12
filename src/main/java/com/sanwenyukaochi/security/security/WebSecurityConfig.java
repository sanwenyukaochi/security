package com.sanwenyukaochi.security.security;

import com.sanwenyukaochi.security.entity.Tenant;
import com.sanwenyukaochi.security.entity.User;
import com.sanwenyukaochi.security.repository.TenantRepository;
import com.sanwenyukaochi.security.repository.UserRepository;
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
    public CommandLineRunner initData(TenantRepository tenantRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (!tenantRepository.existsByName("测试组")) {
                Tenant tenant = new Tenant();
                tenant.setId(1L);
                tenant.setName("测试组");
                tenant.setCode("");
                tenant.setCreatedBy(1L);
                tenant.setUpdatedBy(1L);
                tenantRepository.save(tenant);
            }
            
            // Create users if not already present
            if (!userRepository.existsByUserName("user1")) {
                User user1 = new User();
                user1.setId(1L);
                user1.setTenantId(1L);
                user1.setTenant(tenantRepository.findById(1L).orElseThrow());
                user1.setUserName("user1");
                user1.setPassword(passwordEncoder.encode("123456"));
                user1.setEmail("");
                user1.setPhone("");
                
                userRepository.save(user1);
            }
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
