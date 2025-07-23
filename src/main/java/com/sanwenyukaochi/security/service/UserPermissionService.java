package com.sanwenyukaochi.security.service;

import com.sanwenyukaochi.security.entity.*;
import com.sanwenyukaochi.security.entity.relation.RolePermission;
import com.sanwenyukaochi.security.entity.relation.UserRole;
import com.sanwenyukaochi.security.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserPermissionService {

    private final UserRoleRepository userRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    /**
     * 获取用户的所有角色代码
     */
    public List<String> getUserRoleCodes(Long userId) {
        List<UserRole> userRoles = userRoleRepository.findAllByUser_Id(userId);
        return userRoles.stream()
                .map(UserRole::getRole)
                .map(Role::getCode)
                .collect(Collectors.toList());
    }

    /**
     * 获取用户的所有权限代码
     */
    public List<String> getUserPermissionCodes(Long userId) {
        List<UserRole> userRoles = userRoleRepository.findAllByUser_Id(userId);

        return userRoles.stream()
                .map(UserRole::getRole)
                .map(role -> rolePermissionRepository.findByRole(role))
                .flatMap(List::stream)
                .map(RolePermission::getPermission)
                .map(Permission::getCode)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 获取用户的所有角色和权限代码
     */
    public List<String> getUserAuthorities(Long userId) {
        List<String> roles = getUserRoleCodes(userId);
        List<String> permissions = getUserPermissionCodes(userId);

        // 合并角色和权限，角色以ROLE_开头
        List<String> authorities = roles.stream()
                .map(role -> "ROLE_" + role.toUpperCase())
                .collect(Collectors.toList());

        authorities.addAll(permissions);

        return authorities;
    }
} 