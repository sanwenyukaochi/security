package com.sanwenyukaochi.security.security.service;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.http.HttpStatus;
import com.sanwenyukaochi.security.dto.LoginDTO;
import com.sanwenyukaochi.security.exception.APIException;
import com.sanwenyukaochi.security.model.CaptchaPair;
import com.sanwenyukaochi.security.model.JwtTokenPair;
import com.sanwenyukaochi.security.security.captcha.CaptchaContext;
import com.sanwenyukaochi.security.security.captcha.CaptchaPairCodeAndBase;
import com.sanwenyukaochi.security.security.exception.AuthenticationExceptionFactory;
import com.sanwenyukaochi.security.security.jwt.JwtUtils;
import com.sanwenyukaochi.security.security.jwt.RSAUtil;
import com.sanwenyukaochi.security.service.UserPermissionCacheService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder encoder;
    private final UserPermissionCacheService userPermissionCacheService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final Snowflake snowflake;

    public JwtTokenPair getTokenPair(LoginDTO loginDTO) {
        String captcha = (String)redisTemplate.opsForValue().get(loginDTO.getCaptchaKey());
        if (!loginDTO.getCaptcha().equals(captcha)) {
            throw new APIException(HttpStatus.HTTP_UNAUTHORIZED,"验证码不正确");
        }
        redisTemplate.delete(loginDTO.getCaptchaKey());
        Authentication authentication;
        try {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), RSAUtil.decrypt(loginDTO.getPassword()));
            authentication = authenticationManager.authenticate(authenticationToken);
        } catch (AuthenticationException e) {
            throw AuthenticationExceptionFactory.resolve(loginDTO.getUsername(), e);
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return jwtUtils.generateTokenPairFromUsername(userDetails.getUsername());   
    }

    @SneakyThrows
    public CaptchaPair getCaptcha() {
        CaptchaPairCodeAndBase result = CaptchaContext.generateRandomCaptcha(130,48,5);
        String key = "captcha:" + snowflake.nextId();
        redisTemplate.opsForValue().set(key, result.getCode(), 30, TimeUnit.MINUTES);
        log.info("验证码为:{}", redisTemplate.opsForValue().get(key));
        return new CaptchaPair(key, result.getBase64());
    }
}
