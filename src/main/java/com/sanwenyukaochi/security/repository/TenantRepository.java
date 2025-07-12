package com.sanwenyukaochi.security.repository;

import com.sanwenyukaochi.security.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {

    boolean existsByName(String name);
    
    List<Tenant> findByName(String name);
}
