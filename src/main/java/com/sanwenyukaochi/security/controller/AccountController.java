package com.sanwenyukaochi.security.controller;

import com.sanwenyukaochi.security.dto.AccountDTO;
import com.sanwenyukaochi.security.vo.AccountVO;
import com.sanwenyukaochi.security.vo.Result;
import com.sanwenyukaochi.security.vo.page.PageVO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.sanwenyukaochi.security.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.access.prepost.PreAuthorize;

@Slf4j
@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {
    private final UserService userService;

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('account:user:view')")
    @Operation(summary = "查询账户列表")
    public Result<PageVO<AccountVO>> listUsers(@RequestParam(defaultValue = "0") int currentPage, @RequestParam(defaultValue = "6") int size) {
        Pageable pageable = PageRequest.of(currentPage, size);
        Page<AccountDTO> accountPage = userService.findAllUser(pageable);
        return Result.success(PageVO.from(accountPage.map(newUser -> new AccountVO(
                newUser.getId(),
                newUser.getUserName(),
                newUser.getNickName(),
                newUser.getPhone(),
                newUser.getAvatar(),
                newUser.getUpdatedAt(),
                newUser.getCreatedAt()
        ))));
    }

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('account:user:profile')")
    @Operation(summary = "获取当前登录用户信息")
    public Result<AccountVO> currentUserInfo(Authentication authentication) {
        AccountDTO user = userService.findByUserInfo(authentication);
        return Result.success(new AccountVO(
                user.getId(),
                user.getUserName(),
                user.getNickName(),
                user.getPhone(),
                user.getAvatar(),
                user.getUpdatedAt(),
                user.getCreatedAt()
        ));
    }
}
