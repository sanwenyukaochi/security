package com.sanwenyukaochi.security.aspect;

import com.sanwenyukaochi.security.annotation.IgnoreTenantFilter;
import com.sanwenyukaochi.security.security.service.UserDetailsImpl;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;

import java.lang.reflect.Method;
import java.util.Collection;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class TenantFilterAspect {
    
    private final EntityManager entityManager;

    //    @Around("execution(* com.sanwenyukaochi.security..*(..))")
    @Around("execution(* com.sanwenyukaochi.security..repository..*(..)) || execution(* com.sanwenyukaochi.security..service..*(..)) || execution(* com.sanwenyukaochi.security..controller..*(..))")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 检查是否有@IgnoreTenantFilter注解
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        if (method.isAnnotationPresent(IgnoreTenantFilter.class)) {
            // 直接关闭过滤器
            EntityManager em = EntityManagerFactoryUtils.getTransactionalEntityManager(entityManager.getEntityManagerFactory());
            if (em == null) {
                em = entityManager;
            }
            Session session = em.unwrap(Session.class);
            session.disableFilter("tenantFilter");
            return joinPoint.proceed();
        }
        // 1. 获取当前用户权限
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean enableTenantFilter = false;
        String tenantId = null;

         if (authentication != null && authentication.getAuthorities() != null) {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            authorities.forEach(authority -> {
                log.info("Authority: {}", authority.getAuthority());
            });
            enableTenantFilter = authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_TENANT"));
            if (enableTenantFilter && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
                try {
                    tenantId = userDetails.getTenant().getId().toString();
                } catch (Exception ignored) {}
            }
        }

        EntityManager em = EntityManagerFactoryUtils.getTransactionalEntityManager(entityManager.getEntityManagerFactory());
        if (em == null) {
            em = entityManager;
        }
        Session session = em.unwrap(Session.class);
        Filter filter = null;
        if (enableTenantFilter && tenantId != null) {
            filter = session.enableFilter("tenantFilter");
            filter.setParameter("tenantId", tenantId);
            log.info("【AOP】启用租户过滤器: tenantId={}", tenantId);
        } else {
            session.disableFilter("tenantFilter");
            log.info("【AOP】启用租户过滤器: tenantId={}", tenantId);
            log.info("【AOP】关闭租户过滤器");
        }

        try {
            return joinPoint.proceed();
        } finally {
            session.disableFilter("tenantFilter");
        }
    }
}