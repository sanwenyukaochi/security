package com.sanwenyukaochi.security.repository;

import com.sanwenyukaochi.security.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    Optional<Permission> findByCode(String code);

    Optional<Permission> findByName(String name);

    Boolean existsByCode(String code);

    Boolean existsByName(String name);

    List<Permission> findByParentId(Long parentId);

    List<Permission> findByType(String type);
}