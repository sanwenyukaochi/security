package com.sanwenyukaochi.security.security.service;

import com.sanwenyukaochi.security.entity.*;
import com.sanwenyukaochi.security.entity.relation.RolePermission;
import com.sanwenyukaochi.security.repository.UserRepository;
import com.sanwenyukaochi.security.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserPermissionService userPermissionService;
    private final UserPermissionCacheService userPermissionCacheService;
    private final UserRoleService userRoleService;
    private final RoleService roleService;
    private final RolePermissionService rolePermissionService;
    private final PermissionService permissionService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));

        List<String> authorityCodes = Optional.ofNullable(userPermissionCacheService)
                .map(service -> service.getUserAuthorities(user.getId()))
                .orElseGet(() -> userPermissionService.getUserAuthorities(user.getId()));

        List<Role> roles = roleService.findAllById(userRoleService.findAllByUser(user).stream()
                .map(ur -> ur.getRole().getId())
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));

        List<RolePermission> polePermissions = rolePermissionService.findAllByRole(roles);

        List<Permission> permissions = permissionService.findAllById(polePermissions.stream()
                .map(rp -> rp.getPermission().getId())
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
        
        UserDetailsImpl build = UserDetailsImpl.build(user, authorityCodes, roles, permissions);
        redisTemplate.opsForValue().set("t", build);
        return build;
    }
}
