package com.sanwenyukaochi.security.security.handler;

import com.sanwenyukaochi.security.entity.User;
import com.sanwenyukaochi.security.repository.UserRepository;
import com.sanwenyukaochi.security.service.UserPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CustomPermissionEvaluator implements PermissionEvaluator {

    private final UserPermissionService userPermissionService;
    private final UserRepository userRepository;

    // TODO 预留文件，这里可以实现更加复杂的权限设计
    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        // 获取当前用户的权限
        String username = authentication.getName();
        User user = userRepository.findByUserName(username).orElse(null);
        if (user == null) {
            return false;
        }

        List<String> userPermissions = userPermissionService.getUserPermissionCodes(user.getId());

        // 检查是否有指定权限
        return userPermissions.contains(permission.toString());
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        // 获取当前用户的权限
        String username = authentication.getName();
        User user = userRepository.findByUserName(username).orElse(null);
        if (user == null) {
            return false;
        }

        List<String> userPermissions = userPermissionService.getUserPermissionCodes(user.getId());

        // 检查是否有指定权限
        return userPermissions.contains(permission.toString());
    }
} 