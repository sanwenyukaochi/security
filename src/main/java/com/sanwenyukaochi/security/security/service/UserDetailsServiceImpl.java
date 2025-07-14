package com.sanwenyukaochi.security.security.service;

import com.sanwenyukaochi.security.entity.User;
import com.sanwenyukaochi.security.security.exception.AuthenticationExceptionFactory;
import com.sanwenyukaochi.security.repository.UserRepository;
import com.sanwenyukaochi.security.service.UserPermissionService;
import com.sanwenyukaochi.security.service.UserPermissionCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserPermissionService userPermissionService;
    private final UserPermissionCacheService userPermissionCacheService;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        return UserDetailsImpl.build(user, userPermissionService, userPermissionCacheService);
    }
    
}
