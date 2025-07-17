package com.sanwenyukaochi.security.aspect;

import com.sanwenyukaochi.security.annotation.DataScope;
import com.sanwenyukaochi.security.entity.Role;
import com.sanwenyukaochi.security.enums.DataScopeEnum;
import com.sanwenyukaochi.security.security.service.UserDetailsImpl;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.Session;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class DataScopeAspect {

    private final EntityManager entityManager;

    @Around("@annotation(dataScope)")
    public Object around(ProceedingJoinPoint joinPoint, DataScope dataScope) throws Throwable {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl user)) {
            return joinPoint.proceed();
        }

        Long tenantId = user.getTenant().getId();
        Long userId = user.getId();
        int dataScopeCode = user.getRoles().stream().mapToInt(Role::getDataScope).min().orElse(1);

        String tenantField = dataScope.tenantProperty();
        String userProperty = dataScope.userProperty();

        Session session = getSession();
        try {
            switch (DataScopeEnum.from(dataScopeCode)) {
                case ALL -> {
                    session.disableFilter("tenantFilter");
                    session.disableFilter("createdByFilter");
                }
                case TENANT -> {
                    if (tenantId == null) throw new IllegalStateException("无租户ID");
                    session.enableFilter("tenantFilter").setParameter(tenantField, tenantId);
                    session.disableFilter("createdByFilter");
                }
                case SELF -> {
                    if (tenantId == null) throw new IllegalStateException("无租户ID");
                    if (userId == null) throw new IllegalStateException("无用户ID");
                    session.enableFilter("tenantFilter").setParameter(tenantField, tenantId);
                    session.enableFilter("createdByFilter").setParameter(userProperty, userId);
                }
            }
            return joinPoint.proceed();
        } finally {
            disableAllFilters(session, "tenantFilter", "createdByFilter");
        }
    }

    private Session getSession() {
        EntityManager em = EntityManagerFactoryUtils.getTransactionalEntityManager(entityManager.getEntityManagerFactory());
        if (em == null) {
            em = entityManager;
        }
        return em.unwrap(Session.class);
    }

    private void disableAllFilters(Session session, String... filters) {
        for (String filter : filters) {
            try {
                session.disableFilter(filter);
            } catch (IllegalArgumentException ignored) {
                // 若未开启的filter调用disable会抛异常，忽略即可
            }
        }
    }

}