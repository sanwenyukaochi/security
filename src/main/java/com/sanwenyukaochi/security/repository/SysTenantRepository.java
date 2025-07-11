package com.sanwenyukaochi.security.repository;

import com.sanwenyukaochi.security.entity.SysTenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SysTenantRepository extends JpaRepository<SysTenant, Long> {

    boolean existsByName(String name);


    List<SysTenant> findByName(String name);
}
