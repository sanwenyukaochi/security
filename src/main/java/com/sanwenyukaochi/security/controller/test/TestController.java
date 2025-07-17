//package com.sanwenyukaochi.security.controller.test;
//
//import cn.hutool.http.HttpStatus;
//import com.sanwenyukaochi.security.entity.*;
//import com.sanwenyukaochi.security.repository.*;
//import com.sanwenyukaochi.security.security.filter.RequestCorrelationIdFilter;
//import com.sanwenyukaochi.security.security.service.UserDetailsImpl;
//import com.sanwenyukaochi.security.service.VideoService;
//import com.sanwenyukaochi.security.vo.Result;
//import com.sanwenyukaochi.security.vo.page.PageVO;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.RequiredArgsConstructor;
//import lombok.Setter;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Slf4j
//@RestController
//@RequestMapping("/api/test")
//@RequiredArgsConstructor
//@Transactional(readOnly = true)
//public class TestController {
//
//    private final UserRepository userRepository;
//    private final UserRoleRepository userRoleRepository;
//    private final RolePermissionRepository rolePermissionRepository;
//    private final RoleRepository roleRepository;
//    private final PermissionRepository permissionRepository;
//    private final VideoRepository videoRepository;
//    private final VideoService videoService;
//
//    @GetMapping("/queryVideo")
//    public Result<?> getVideo(@RequestParam(defaultValue = "0") int currentPage,
//                              @RequestParam(defaultValue = "6") int size) {
//        log.info("请求追踪ID为: {}", RequestCorrelationIdFilter.getCurrentRequestId());
//        Pageable pageable = PageRequest.of(currentPage, size);
//        Page<Video> allVideo = videoService.findAllVideo(pageable);
//        return Result.success(PageVO.from(allVideo.map(video -> {
//            Map<String, Object> simple = new HashMap<>();
//            simple.put("id", video.getId());
//            simple.put("videoName", video.getFileName() + "." + video.getFileExt());
//            return simple;
//        })));
//    }
//    @GetMapping("/public")
//    public Result<Map<String, Object>> publicEndpoint(Authentication authentication) {
//        Map<String, Object> response = new HashMap<>();
//        response.put("message", "这是公开接口，任何人都可以访问");
//        response.put("status", "success");
//        // response.put("authentication", authentication);
//        return Result.success(response);
//    }
//
//    @GetMapping("/user-vo")
//    public Result<Map<String, Object>> getUserVo(Authentication authentication) {
//        if (authentication == null || !authentication.isAuthenticated()) {
//            Map<String, Object> response = new HashMap<>();
//            response.put("message", "用户未认证");
//            response.put("status", "error");
//            return Result.error(HttpStatus.HTTP_UNAUTHORIZED,"用户未认证", response);
//        }
//
//        // 使用Repository查询来避免懒加载问题
//        User user = userRepository.findByUserName(authentication.getName()).orElseThrow();
//
//        // 详细的VO类定义，包含完整的权限信息
//        @Setter
//        @Getter
//        @AllArgsConstructor
//        class DetailedUserVo {
//            // Getter和Setter方法
//            private Long id;
//            private String userName;
//            private String email;
//            private String phone;
//            private Boolean status;
//            private Tenant tenant;
//            private List<RoleVo> roles;           // 角色详细信息列表
//            private List<PermissionVo> permissions; // 权限详细信息列表
//
//            public DetailedUserVo(User user) {
//                this.id = user.getId();
//                this.userName = user.getUserName();
//                this.email = user.getEmail();
//                this.phone = user.getPhone();
//                this.status = user.getStatus();
//                this.tenant = user.getTenant();
//
//                // 初始化空列表，避免懒加载
//                this.roles = new ArrayList<>();
//                this.permissions = new ArrayList<>();
//            }
//
//            // 角色VO
//            @Setter
//            @Getter
//            static class RoleVo {
//                // Getter和Setter方法
//                private Long id;
//                private String name;
//                private String code;
//                private Integer dataScope;
//                private Boolean status;
//                private Tenant tenant;
//
//                public RoleVo(Role role) {
//                    this.id = role.getId();
//                    this.name = role.getName();
//                    this.code = role.getCode();
//                    this.dataScope = role.getDataScope();
//                    this.status = role.getStatus();
//                    this.tenant = role.getTenant();
//                }
//
//            }
//
//            // 权限VO
//            @Setter
//            @Getter
//            static class PermissionVo {
//                // Getter和Setter方法
//                private Long id;
//                private Long parentId;    // 父节点ID
//                private String type;      // 类型（1菜单 2按钮）
//                private String name;      // 权限名称
//                private String code;      // 权限标识
//                private String path;      // 前端路由/按钮绑定路径
//                private Integer sort;     // 排序号
//                private Boolean visible;  // 是否可见
//
//                public PermissionVo(Permission permission) {
//                    this.id = permission.getId();
//                    this.parentId = permission.getParentId();
//                    this.type = permission.getType();
//                    this.name = permission.getName();
//                    this.code = permission.getCode();
//                    this.path = permission.getPath();
//                    this.sort = permission.getSort();
//                    this.visible = permission.getVisible();
//                }
//
//                // 重写equals和hashCode方法，用于Set去重
//                @Override
//                public boolean equals(Object o) {
//                    if (this == o) return true;
//                    if (o == null || getClass() != o.getClass()) return false;
//                    PermissionVo that = (PermissionVo) o;
//                    return Objects.equals(id, that.id);
//                }
//
//                @Override
//                public int hashCode() {
//                    return Objects.hash(id);
//                }
//            }
//        }
//
//        // 创建VO实例
//        DetailedUserVo userVo = new DetailedUserVo(user);
//
//        // 使用Repository查询来安全地获取角色和权限信息
//        try {
//            // 获取用户角色
//            List<UserRole> userRoles = userRoleRepository.findByUser_Id(user.getId());
//            System.out.println("找到用户角色数量: " + userRoles.size());
//
//            List<DetailedUserVo.RoleVo> roleVos = new ArrayList<>();
//            Set<DetailedUserVo.PermissionVo> permissionVos = new HashSet<>();
//
//            for (UserRole userRole : userRoles) {
//                System.out.println("处理用户角色: " + userRole.getId());
//
//                // 通过ID直接查询角色，避免懒加载
//                Role role = roleRepository.findById(userRole.getRole().getId()).orElse(null);
//                if (role != null) {
//                    System.out.println("角色信息: " + role.getName() + " (" + role.getCode() + ")");
//                    roleVos.add(new DetailedUserVo.RoleVo(role));
//
//                    // 获取角色的权限
//                    List<RolePermission> rolePermissions = rolePermissionRepository.findByRole_Id(role.getId());
//                    System.out.println("角色 " + role.getCode() + " 的权限数量: " + rolePermissions.size());
//
//                    for (RolePermission rolePermission : rolePermissions) {
//                        // 通过ID直接查询权限，避免懒加载
//                        Permission permission = permissionRepository.findById(rolePermission.getPermission().getId()).orElse(null);
//                        if (permission != null) {
//                            System.out.println("权限信息: " + permission.getName() + " (" + permission.getCode() + ")");
//                            permissionVos.add(new DetailedUserVo.PermissionVo(permission));
//                        }
//                    }
//                }
//            }
//
//            System.out.println("最终角色数量: " + roleVos.size());
//            System.out.println("最终权限数量: " + permissionVos.size());
//
//            // 设置VO数据
//            userVo.roles = roleVos;
//            userVo.permissions = new ArrayList<>(permissionVos);
//
//        } catch (Exception e) {
//            // 如果出现异常，返回详细错误信息
//            Map<String, Object> response = new HashMap<>();
//            response.put("message", "获取用户详细信息时出现异常: " + e.getMessage());
//            response.put("status", "error");
//            response.put("userBasicInfo", userVo);
//            response.put("exception", e.getClass().getSimpleName());
//            response.put("stackTrace", e.getStackTrace());
//            return Result.success(response);
//        }
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("message", "用户VO数据");
//        response.put("status", "success");
//        response.put("userVo", userVo);
//        response.put("debug", Map.of(
//                "userId", user.getId(),
//                "userName", user.getUserName(),
//                "rolesCount", userVo.getRoles().size(),
//                "permissionsCount", userVo.getPermissions().size()
//        ));
//        return Result.success(response);
//    }
//
//    @GetMapping("/user")
//    @PreAuthorize("hasRole('USER')")
//    public ResponseEntity<Map<String, Object>> userEndpoint(Authentication authentication) {
//        Map<String, Object> response = new HashMap<>();
//        response.put("message", "这是用户接口，需要USER角色");
//        response.put("username", authentication.getName());
//        response.put("authorities", authentication.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .collect(Collectors.toList()));
//        response.put("status", "success");
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/admin")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<Map<String, Object>> adminEndpoint(Authentication authentication) {
//        Map<String, Object> response = new HashMap<>();
//        response.put("message", "这是管理员接口，需要ADMIN角色");
//        response.put("username", authentication.getName());
//        response.put("authorities", authentication.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .collect(Collectors.toList()));
//        response.put("status", "success");
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/user:view")
//    @PreAuthorize("hasAuthority('user:view')")
//    public ResponseEntity<Map<String, Object>> userViewPermission(Authentication authentication) {
//        Map<String, Object> response = new HashMap<>();
//        response.put("message", "这是用户查看权限接口");
//        response.put("username", authentication.getName());
//        response.put("authorities", authentication.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .collect(Collectors.toList()));
//        response.put("status", "success");
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/user:add")
//    @PreAuthorize("hasAuthority('user:add')")
//    public ResponseEntity<Map<String, Object>> userAddPermission(Authentication authentication) {
//        Map<String, Object> response = new HashMap<>();
//        response.put("message", "这是用户新增权限接口");
//        response.put("username", authentication.getName());
//        response.put("authorities", authentication.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .collect(Collectors.toList()));
//        response.put("status", "success");
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/user:edit")
//    @PreAuthorize("hasAuthority('user:edit')")
//    public ResponseEntity<Map<String, Object>> userEditPermission(Authentication authentication) {
//        Map<String, Object> response = new HashMap<>();
//        response.put("message", "这是用户编辑权限接口");
//        response.put("username", authentication.getName());
//        response.put("authorities", authentication.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .collect(Collectors.toList()));
//        response.put("status", "success");
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/user:delete")
//    @PreAuthorize("hasAuthority('user:delete')")
//    public ResponseEntity<Map<String, Object>> userDeletePermission(Authentication authentication) {
//        Map<String, Object> response = new HashMap<>();
//        response.put("message", "这是用户删除权限接口");
//        response.put("username", authentication.getName());
//        response.put("authorities", authentication.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .collect(Collectors.toList()));
//        response.put("status", "success");
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/user:manage")
//    @PreAuthorize("hasAuthority('user:manage')")
//    public ResponseEntity<Map<String, Object>> userManagePermission(Authentication authentication) {
//        Map<String, Object> response = new HashMap<>();
//        response.put("message", "这是用户管理权限接口");
//        response.put("username", authentication.getName());
//        response.put("authorities", authentication.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .collect(Collectors.toList()));
//        response.put("status", "success");
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/current-user")
//    public ResponseEntity<Map<String, Object>> getCurrentUser(Authentication authentication) {
//        Map<String, Object> response = new HashMap<>();
//        if (authentication != null && authentication.isAuthenticated()) {
//            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
//            response.put("id", userDetails.getId());
//            response.put("username", userDetails.getUsername());
//            response.put("email", userDetails.getEmail());
//            response.put("phone", userDetails.getPhone());
//            response.put("tenant", userDetails.getTenant());
//            response.put("authorities", authentication.getAuthorities().stream()
//                    .map(GrantedAuthority::getAuthority)
//                    .collect(Collectors.toList()));
//            response.put("status", "success");
//        } else {
//            response.put("message", "用户未认证");
//            response.put("status", "error");
//        }
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/debug-user")
//    public ResponseEntity<Map<String, Object>> debugUser(Authentication authentication) {
//        if (authentication == null || !authentication.isAuthenticated()) {
//            Map<String, Object> response = new HashMap<>();
//            response.put("message", "用户未认证");
//            response.put("status", "error");
//            return ResponseEntity.badRequest().body(response);
//        }
//
//        User user = userRepository.findByUserName(authentication.getName()).orElseThrow();
//
//        Map<String, Object> debugInfo = new HashMap<>();
//        debugInfo.put("userId", user.getId());
//        debugInfo.put("userName", user.getUserName());
//        debugInfo.put("email", user.getEmail());
//
//        // 检查用户角色
//        List<UserRole> userRoles = userRoleRepository.findByUser_Id(user.getId());
//        debugInfo.put("userRolesCount", userRoles.size());
//
//        List<Map<String, Object>> roleDetails = new ArrayList<>();
//        for (UserRole userRole : userRoles) {
//            Map<String, Object> roleInfo = new HashMap<>();
//            roleInfo.put("userRoleId", userRole.getId());
//
//            if (userRole.getRole() != null) {
//                Role role = userRole.getRole();
//                roleInfo.put("roleId", role.getId());
//                roleInfo.put("roleName", role.getName());
//                roleInfo.put("roleCode", role.getCode());
//
//                // 检查角色权限
//                List<RolePermission> rolePermissions = rolePermissionRepository.findByRole_Id(role.getId());
//                roleInfo.put("rolePermissionsCount", rolePermissions.size());
//
//                List<String> permissionCodes = new ArrayList<>();
//                for (RolePermission rolePermission : rolePermissions) {
//                    if (rolePermission.getPermission() != null) {
//                        permissionCodes.add(rolePermission.getPermission().getCode());
//                    }
//                }
//                roleInfo.put("permissionCodes", permissionCodes);
//            } else {
//                roleInfo.put("roleId", null);
//                roleInfo.put("roleName", null);
//                roleInfo.put("roleCode", null);
//                roleInfo.put("rolePermissionsCount", 0);
//                roleInfo.put("permissionCodes", new ArrayList<>());
//            }
//
//            roleDetails.add(roleInfo);
//        }
//        debugInfo.put("roleDetails", roleDetails);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("message", "调试用户信息");
//        response.put("status", "success");
//        response.put("debugInfo", debugInfo);
//        return ResponseEntity.ok(response);
//    }
//
//
//
//    /**
//     * 获取当前租户的所有用户
//     */
//    @GetMapping("/users")
//    public Result<List<User>> getCurrentTenantUsers() {
//        List<User> users = userRepository.findAll();
//        return Result.success(users);
//    }
//
//    /**
//     * 获取当前租户的所有视频
//     */
//    @GetMapping("/videos")
//    public Result<List<Video>> getCurrentTenantVideos() {
//        List<Video> videos = videoRepository.findAll();
//        return Result.success(videos);
//    }
//
//    /**
//     * 根据ID获取用户
//     */
//    @GetMapping("/users/{id}")
//    public Result<User> getUserById(@PathVariable Long id) {
//        return userRepository.findById(id)
//                .map(Result::success)
//                .orElse(Result.error(404, "用户不存在"));
//    }
//
//    /**
//     * 根据ID获取视频
//     */
//    @GetMapping("/videos/{id}")
//    public Result<Video> getVideoById(@PathVariable Long id) {
//        return videoRepository.findById(id)
//                .map(Result::success)
//                .orElse(Result.error(404, "视频不存在"));
//    }
//
//    /**
//     * 获取用户数量
//     */
//    @GetMapping("/users/count")
//    public Result<Long> getUserCount() {
//        long count = userRepository.count();
//        return Result.success(count);
//    }
//
//    /**
//     * 获取视频数量
//     */
//    @GetMapping("/videos/count")
//    public Result<Long> getVideoCount() {
//        long count = videoRepository.count();
//        return Result.success(count);
//    }
//
//} 