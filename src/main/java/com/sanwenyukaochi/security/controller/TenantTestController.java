//package com.sanwenyukaochi.security.controller;
//
//import com.sanwenyukaochi.security.context.TenantContext;
//import com.sanwenyukaochi.security.entity.User;
//import com.sanwenyukaochi.security.repository.UserRepository;
//import com.sanwenyukaochi.security.vo.Result;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
///**
// * 租户测试控制器
// * 用于验证多租户数据隔离功能
// */
//@RestController
//@RequestMapping("/api/tenant-test")
//public class TenantTestController {
//    
//    @Autowired
//    private UserRepository userRepository;
//    
//    /**
//     * 获取当前租户的所有用户
//     * @return 用户列表
//     */
//    @GetMapping("/users")
//    public Result<List<User>> getCurrentTenantUsers() {
//        String currentTenant = TenantContext.getTenantId();
//        List<User> users = userRepository.findAll();
//        
//        return Result.success(users, "当前租户(" + currentTenant + ")的用户列表");
//    }
//    
//    /**
//     * 获取当前租户信息
//     * @return 租户信息
//     */
//    @GetMapping("/current-tenant")
//    public Result<String> getCurrentTenant() {
//        String currentTenant = TenantContext.getTenantId();
//        return Result.success(currentTenant, "当前租户ID");
//    }
//    
//    /**
//     * 创建测试用户
//     * @param userName 用户名
//     * @param email 邮箱
//     * @return 创建结果
//     */
//    @PostMapping("/create-user")
//    public Result<User> createTestUser(@RequestParam String userName, 
//                                     @RequestParam String email) {
//        String currentTenant = TenantContext.getTenantId();
//        
//        User user = new User();
//        user.setUserName(userName);
//        user.setEmail(email);
//        user.setPassword("test123");
//        user.setPhone("13800138000");
//        user.setStatus(true);
//        user.setAccountNonExpired(true);
//        user.setAccountNonLocked(true);
//        user.setCredentialsNonExpired(true);
//        user.setCreatedBy(1L);
//        user.setCreatedAt(System.currentTimeMillis());
//        user.setTenantId(currentTenant);
//        
//        User savedUser = userRepository.save(user);
//        
//        return Result.success(savedUser, "用户创建成功");
//    }
//} 