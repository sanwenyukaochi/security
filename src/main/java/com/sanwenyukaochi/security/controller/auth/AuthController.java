package com.sanwenyukaochi.security.controller.auth;

import com.sanwenyukaochi.security.dto.LoginDTO;
import com.sanwenyukaochi.security.model.CaptchaPair;
import com.sanwenyukaochi.security.model.JwtTokenPair;
import com.sanwenyukaochi.security.security.jwt.RSAUtil;
import com.sanwenyukaochi.security.ao.LoginAO;

import com.sanwenyukaochi.security.security.service.AuthService;
import com.sanwenyukaochi.security.vo.CaptchaVO;
import com.sanwenyukaochi.security.vo.LoginVO;
import com.sanwenyukaochi.security.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @GetMapping("/rsaPublicKey")
    @Operation(summary = "获取RSA公钥", description = "加密前端发送的密码")
    public Result<String> getRsaPublicKey() {
        return Result.success(RSAUtil.getPublicKeyStr());
    }

    @PostMapping("/signIn")
    @Operation(summary = "获取认证token", description = "传入账号:username,密码:password")
    public Result<LoginVO> authenticateUser(@Valid @RequestBody LoginAO loginAO) {
        LoginDTO loginDTO = new LoginDTO(loginAO.getUsername(), loginAO.getPassword(), loginAO.getCaptchaKey(), loginAO.getCaptcha());
        JwtTokenPair jwtTokenPair = authService.getTokenPair(loginDTO);
        return Result.success(new LoginVO(jwtTokenPair.getAccessToken(), jwtTokenPair.getRefreshToken()));
    }

    @GetMapping("/captcha")
    @Operation(summary = "获取验证码")
    public Result<CaptchaVO> captcha() {
        CaptchaPair captcha = authService.getCaptcha();
        return Result.success(new CaptchaVO(captcha.getCaptchaKey(), captcha.getCaptcha()));
    }
    
    @PostMapping("/signOut")
    @Operation(summary = "登出")
    public Result<?> signOutUser(){
        return Result.success("退出成功！");
    }
}