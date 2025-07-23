package com.sanwenyukaochi.security.service;

import com.sanwenyukaochi.security.entity.Role;
import com.sanwenyukaochi.security.entity.relation.RolePermission;
import com.sanwenyukaochi.security.repository.RolePermissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RolePermissionService {
    private final RolePermissionRepository rolePermissionRepository;
    public List<RolePermission> findAllByRole(List<Role> roles) {
        return rolePermissionRepository.findAllByRoleIn(roles);
    }
}
