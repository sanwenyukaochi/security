package com.sanwenyukaochi.security.controller;

import com.sanwenyukaochi.security.security.service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    @GetMapping("/public")
    public ResponseEntity<Map<String, Object>> publicEndpoint() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "这是公开接口，任何人都可以访问");
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, Object>> userEndpoint(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "这是用户接口，需要USER角色");
        response.put("username", authentication.getName());
        response.put("authorities", authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> adminEndpoint(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "这是管理员接口，需要ADMIN角色");
        response.put("username", authentication.getName());
        response.put("authorities", authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user:view")
    @PreAuthorize("hasAuthority('user:view')")
    public ResponseEntity<Map<String, Object>> userViewPermission(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "这是用户查看权限接口");
        response.put("username", authentication.getName());
        response.put("authorities", authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user:add")
    @PreAuthorize("hasAuthority('user:add')")
    public ResponseEntity<Map<String, Object>> userAddPermission(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "这是用户新增权限接口");
        response.put("username", authentication.getName());
        response.put("authorities", authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user:edit")
    @PreAuthorize("hasAuthority('user:edit')")
    public ResponseEntity<Map<String, Object>> userEditPermission(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "这是用户编辑权限接口");
        response.put("username", authentication.getName());
        response.put("authorities", authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user:delete")
    @PreAuthorize("hasAuthority('user:delete')")
    public ResponseEntity<Map<String, Object>> userDeletePermission(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "这是用户删除权限接口");
        response.put("username", authentication.getName());
        response.put("authorities", authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user:manage")
    @PreAuthorize("hasAuthority('user:manage')")
    public ResponseEntity<Map<String, Object>> userManagePermission(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "这是用户管理权限接口");
        response.put("username", authentication.getName());
        response.put("authorities", authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/current-user")
    public ResponseEntity<Map<String, Object>> getCurrentUser(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        if (authentication != null && authentication.isAuthenticated()) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            response.put("id", userDetails.getId());
            response.put("username", userDetails.getUsername());
            response.put("email", userDetails.getEmail());
            response.put("phone", userDetails.getPhone());
            response.put("tenant", userDetails.getTenant());
            response.put("authorities", authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()));
            response.put("status", "success");
        } else {
            response.put("message", "用户未认证");
            response.put("status", "error");
        }
        return ResponseEntity.ok(response);
    }
} 