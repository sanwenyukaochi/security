package com.sanwenyukaochi.security.service;

import com.sanwenyukaochi.security.entity.Permission;
import com.sanwenyukaochi.security.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionService {
    private final PermissionRepository permissionRepository;
    public List<Permission> findAllById(List<Long> ids) {
        return permissionRepository.findAllById(ids);
    }
}
