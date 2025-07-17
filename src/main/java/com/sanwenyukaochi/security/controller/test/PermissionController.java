//package com.sanwenyukaochi.security.controller;
//
//import com.sanwenyukaochi.security.entity.*;
//import com.sanwenyukaochi.security.repository.*;
//import com.sanwenyukaochi.security.service.UserPermissionCacheService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/permission")
//@RequiredArgsConstructor
//public class PermissionController {
//
//    private final UserRepository userRepository;
//    private final RoleRepository roleRepository;
//    private final PermissionRepository permissionRepository;
//    private final UserRoleRepository userRoleRepository;
//    private final RolePermissionRepository rolePermissionRepository;
//    private final UserPermissionCacheService userPermissionCacheService;
//
//    /**
//     * 获取用户的所有角色
//     */
//    @GetMapping("/user/{userId}/roles")
//    @PreAuthorize("hasAuthority('user:view')")
//    public ResponseEntity<Map<String, Object>> getUserRoles(@PathVariable Long userId) {
//        Map<String, Object> response = new HashMap<>();
//
//        if (!userRepository.existsById(userId)) {
//            response.put("message", "用户不存在");
//            response.put("status", "error");
//            return ResponseEntity.badRequest().body(response);
//        }
//
//        List<String> roles = userPermissionCacheService.getUserRoleCodes(userId);
//        response.put("userId", userId);
//        response.put("roles", roles);
//        response.put("status", "success");
//
//        return ResponseEntity.ok(response);
//    }
//
//    /**
//     * 获取用户的所有权限
//     */
//    @GetMapping("/user/{userId}/permissions")
//    @PreAuthorize("hasAuthority('user:view')")
//    public ResponseEntity<Map<String, Object>> getUserPermissions(@PathVariable Long userId) {
//        Map<String, Object> response = new HashMap<>();
//
//        if (!userRepository.existsById(userId)) {
//            response.put("message", "用户不存在");
//            response.put("status", "error");
//            return ResponseEntity.badRequest().body(response);
//        }
//
//        List<String> permissions = userPermissionCacheService.getUserPermissionCodes(userId);
//        response.put("userId", userId);
//        response.put("permissions", permissions);
//        response.put("status", "success");
//
//        return ResponseEntity.ok(response);
//    }
//
//    /**
//     * 获取用户的所有权限（包括角色和权限）
//     */
//    @GetMapping("/user/{userId}/authorities")
//    @PreAuthorize("hasAuthority('user:view')")
//    public ResponseEntity<Map<String, Object>> getUserAuthorities(@PathVariable Long userId) {
//        Map<String, Object> response = new HashMap<>();
//
//        if (!userRepository.existsById(userId)) {
//            response.put("message", "用户不存在");
//            response.put("status", "error");
//            return ResponseEntity.badRequest().body(response);
//        }
//
//        List<String> authorities = userPermissionCacheService.getUserAuthorities(userId);
//        response.put("userId", userId);
//        response.put("authorities", authorities);
//        response.put("status", "success");
//
//        return ResponseEntity.ok(response);
//    }
//
//    /**
//     * 为用户分配角色
//     */
//    @PostMapping("/user/{userId}/role/{roleId}")
//    @PreAuthorize("hasAuthority('user:edit')")
//    public ResponseEntity<Map<String, Object>> assignRoleToUser(@PathVariable Long userId, @PathVariable Long roleId) {
//        Map<String, Object> response = new HashMap<>();
//
//        if (!userRepository.existsById(userId)) {
//            response.put("message", "用户不存在");
//            response.put("status", "error");
//            return ResponseEntity.badRequest().body(response);
//        }
//
//        if (!roleRepository.existsById(roleId)) {
//            response.put("message", "角色不存在");
//            response.put("status", "error");
//            return ResponseEntity.badRequest().body(response);
//        }
//
//        // 检查是否已经分配了该角色
//        if (userRoleRepository.existsByUser_IdAndRole_Id(userId, roleId)) {
//            response.put("message", "用户已经拥有该角色");
//            response.put("status", "error");
//            return ResponseEntity.badRequest().body(response);
//        }
//
//        // 获取用户和角色实体
//        User user = userRepository.findById(userId).orElse(null);
//        Role role = roleRepository.findById(roleId).orElse(null);
//
//        if (user == null || role == null) {
//            response.put("message", "用户或角色不存在");
//            response.put("status", "error");
//            return ResponseEntity.badRequest().body(response);
//        }
//
//        // 创建用户角色关联
//        UserRole userRole = new UserRole();
//        userRole.setUser(user);
//        userRole.setRole(role);
//        userRoleRepository.save(userRole);
//
//        // 清除用户权限缓存
//        userPermissionCacheService.clearUserCache(userId);
//
//        response.put("message", "角色分配成功");
//        response.put("status", "success");
//
//        return ResponseEntity.ok(response);
//    }
//
//    /**
//     * 移除用户的角色
//     */
//    @DeleteMapping("/user/{userId}/role/{roleId}")
//    @PreAuthorize("hasAuthority('user:edit')")
//    public ResponseEntity<Map<String, Object>> removeRoleFromUser(@PathVariable Long userId, @PathVariable Long roleId) {
//        Map<String, Object> response = new HashMap<>();
//
//        if (!userRepository.existsById(userId)) {
//            response.put("message", "用户不存在");
//            response.put("status", "error");
//            return ResponseEntity.badRequest().body(response);
//        }
//
//        // 查找并删除用户角色关联
//        List<UserRole> userRoles = userRoleRepository.findByUser_Id(userId);
//        userRoles.stream()
//                .filter(ur -> ur.getRole().getId().equals(roleId))
//                .findFirst()
//                .ifPresent(userRoleRepository::delete);
//
//        // 清除用户权限缓存
//        userPermissionCacheService.clearUserCache(userId);
//
//        response.put("message", "角色移除成功");
//        response.put("status", "success");
//
//        return ResponseEntity.ok(response);
//    }
//
//    /**
//     * 为角色分配权限
//     */
//    @PostMapping("/role/{roleId}/permission/{permissionId}")
//    @PreAuthorize("hasAuthority('user:edit')")
//    public ResponseEntity<Map<String, Object>> assignPermissionToRole(@PathVariable Long roleId, @PathVariable Long permissionId) {
//        Map<String, Object> response = new HashMap<>();
//
//        if (!roleRepository.existsById(roleId)) {
//            response.put("message", "角色不存在");
//            response.put("status", "error");
//            return ResponseEntity.badRequest().body(response);
//        }
//
//        if (!permissionRepository.existsById(permissionId)) {
//            response.put("message", "权限不存在");
//            response.put("status", "error");
//            return ResponseEntity.badRequest().body(response);
//        }
//
//        // 检查是否已经分配了该权限
//        if (rolePermissionRepository.existsByRole_IdAndPermission_Id(roleId, permissionId)) {
//            response.put("message", "角色已经拥有该权限");
//            response.put("status", "error");
//            return ResponseEntity.badRequest().body(response);
//        }
//
//        // 获取角色和权限实体
//        Role role = roleRepository.findById(roleId).orElse(null);
//        Permission permission = permissionRepository.findById(permissionId).orElse(null);
//
//        if (role == null || permission == null) {
//            response.put("message", "角色或权限不存在");
//            response.put("status", "error");
//            return ResponseEntity.badRequest().body(response);
//        }
//
//        // 创建角色权限关联
//        RolePermission rolePermission = new RolePermission();
//        rolePermission.setRole(role);
//        rolePermission.setPermission(permission);
//        rolePermissionRepository.save(rolePermission);
//
//        // 清除所有用户权限缓存（因为角色权限变更会影响所有拥有该角色的用户）
//        userPermissionCacheService.clearAllCache();
//
//        response.put("message", "权限分配成功");
//        response.put("status", "success");
//
//        return ResponseEntity.ok(response);
//    }
//
//    /**
//     * 移除角色的权限
//     */
//    @DeleteMapping("/role/{roleId}/permission/{permissionId}")
//    @PreAuthorize("hasAuthority('user:edit')")
//    public ResponseEntity<Map<String, Object>> removePermissionFromRole(@PathVariable Long roleId, @PathVariable Long permissionId) {
//        Map<String, Object> response = new HashMap<>();
//
//        if (!roleRepository.existsById(roleId)) {
//            response.put("message", "角色不存在");
//            response.put("status", "error");
//            return ResponseEntity.badRequest().body(response);
//        }
//
//        // 查找并删除角色权限关联
//        List<RolePermission> rolePermissions = rolePermissionRepository.findByRole_Id(roleId);
//        rolePermissions.stream()
//                .filter(rp -> rp.getPermission().getId().equals(permissionId))
//                .findFirst()
//                .ifPresent(rolePermissionRepository::delete);
//
//        // 清除所有用户权限缓存
//        userPermissionCacheService.clearAllCache();
//
//        response.put("message", "权限移除成功");
//        response.put("status", "success");
//
//        return ResponseEntity.ok(response);
//    }
//
//    /**
//     * 清除用户权限缓存
//     */
//    @PostMapping("/cache/clear/user/{userId}")
//    @PreAuthorize("hasAuthority('user:edit')")
//    public ResponseEntity<Map<String, Object>> clearUserCache(@PathVariable Long userId) {
//        Map<String, Object> response = new HashMap<>();
//
//        userPermissionCacheService.clearUserCache(userId);
//
//        response.put("message", "用户权限缓存已清除");
//        response.put("status", "success");
//
//        return ResponseEntity.ok(response);
//    }
//
//    /**
//     * 清除所有权限缓存
//     */
//    @PostMapping("/cache/clear/all")
//    @PreAuthorize("hasAuthority('user:edit')")
//    public ResponseEntity<Map<String, Object>> clearAllCache() {
//        Map<String, Object> response = new HashMap<>();
//
//        userPermissionCacheService.clearAllCache();
//
//        response.put("message", "所有权限缓存已清除");
//        response.put("status", "success");
//
//        return ResponseEntity.ok(response);
//    }
//} 