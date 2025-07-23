package com.sanwenyukaochi.security.service;

import com.sanwenyukaochi.security.entity.User;
import com.sanwenyukaochi.security.entity.relation.UserRole;
import com.sanwenyukaochi.security.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserRoleService {
    private final UserRoleRepository userRoleRepository;
    public List<UserRole> findAllByUser(User user) {
        return userRoleRepository.findAllByUser(user);
    }
}
