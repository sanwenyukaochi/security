package com.sanwenyukaochi.security.repository;

import com.sanwenyukaochi.security.entity.UserRole;
import com.sanwenyukaochi.security.entity.User;
import com.sanwenyukaochi.security.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    @EntityGraph(attributePaths = {"user", "role"})
    List<UserRole> findByUser(User user);
    
    @EntityGraph(attributePaths = {"user", "role"})
    List<UserRole> findByUser_Id(Long userId);
    
    @EntityGraph(attributePaths = {"user", "role"})
    List<UserRole> findByRole(Role role);
    
    @EntityGraph(attributePaths = {"user", "role"})
    List<UserRole> findByRole_Id(Long roleId);
    
    Boolean existsByUserAndRole(User user, Role role);
    
    Boolean existsByUser_IdAndRole_Id(Long userId, Long roleId);
} 