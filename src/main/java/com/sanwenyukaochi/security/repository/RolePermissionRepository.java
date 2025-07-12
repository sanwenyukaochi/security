package com.sanwenyukaochi.security.repository;

import com.sanwenyukaochi.security.entity.RolePermission;
import com.sanwenyukaochi.security.entity.Role;
import com.sanwenyukaochi.security.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {

    @EntityGraph(attributePaths = {"role", "permission"})
    List<RolePermission> findByRole(Role role);
    
    @EntityGraph(attributePaths = {"role", "permission"})
    List<RolePermission> findByRole_Id(Long roleId);
    
    @EntityGraph(attributePaths = {"role", "permission"})
    List<RolePermission> findByPermission(Permission permission);
    
    @EntityGraph(attributePaths = {"role", "permission"})
    List<RolePermission> findByPermission_Id(Long permissionId);
    
    Boolean existsByRoleAndPermission(Role role, Permission permission);
    
    Boolean existsByRole_IdAndPermission_Id(Long roleId, Long permissionId);
} 