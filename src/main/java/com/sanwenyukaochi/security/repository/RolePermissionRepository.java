package com.sanwenyukaochi.security.repository;

import com.sanwenyukaochi.security.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {

    List<RolePermission> findByRoleId(Long roleId);
    
    List<RolePermission> findByPermissionId(Long permissionId);
    
    Boolean existsByRoleIdAndPermissionId(Long roleId, Long permissionId);
} 