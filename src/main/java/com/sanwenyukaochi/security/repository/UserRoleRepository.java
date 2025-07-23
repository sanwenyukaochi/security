package com.sanwenyukaochi.security.repository;

import com.sanwenyukaochi.security.entity.relation.UserRole;
import com.sanwenyukaochi.security.entity.User;
import com.sanwenyukaochi.security.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    @EntityGraph(attributePaths = {"user", "role"})
    List<UserRole> findAllByUser(User user);

    @EntityGraph(attributePaths = {"user", "role"})
    List<UserRole> findAllByUser_Id(Long userId);

    @EntityGraph(attributePaths = {"user", "role"})
    List<UserRole> findAllByRole(Role role);

    @EntityGraph(attributePaths = {"user", "role"})
    List<UserRole> findAllByRole_Id(Long roleId);

    Boolean existsByUserAndRole(User user, Role role);

    Boolean existsByUser_IdAndRole_Id(Long userId, Long roleId);

    Optional<UserRole> findByUser_IdAndRole_Id(Long userId, Long roleId);

    Optional<UserRole> findByUserAndRole(User user, Role role);
}