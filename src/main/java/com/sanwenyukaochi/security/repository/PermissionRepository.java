package com.sanwenyukaochi.security.repository;

import com.sanwenyukaochi.security.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    @EntityGraph(attributePaths = {"rolePermissions"})
    Optional<Permission> findByCode(String code);
    
    @EntityGraph(attributePaths = {"rolePermissions"})
    Optional<Permission> findByName(String name);
    
    Boolean existsByCode(String code);
    
    Boolean existsByName(String name);
    
    @EntityGraph(attributePaths = {"rolePermissions"})
    List<Permission> findByParentId(Long parentId);
    
    @EntityGraph(attributePaths = {"rolePermissions"})
    List<Permission> findByType(String type);
}