package com.sanwenyukaochi.security.service;

import com.sanwenyukaochi.security.entity.Role;
import com.sanwenyukaochi.security.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    public List<Role> findAllById(List<Long> ids) {
        return roleRepository.findAllById(ids);
    }
}
