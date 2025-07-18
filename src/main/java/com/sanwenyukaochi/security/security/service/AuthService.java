package com.sanwenyukaochi.security.security.service;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.crypto.CryptoException;
import cn.hutool.http.HttpStatus;
import com.sanwenyukaochi.security.dto.LoginDTO;
import com.sanwenyukaochi.security.exception.APIException;
import com.sanwenyukaochi.security.model.CaptchaPair;
import com.sanwenyukaochi.security.model.JwtTokenPair;
import com.sanwenyukaochi.security.security.captcha.CaptchaContext;
import com.sanwenyukaochi.security.security.captcha.CaptchaPairCodeAndBase;
import com.sanwenyukaochi.security.security.jwt.JwtUtils;
import com.sanwenyukaochi.security.security.jwt.RSAUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;

import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtils jwtUtils;
    private final Snowflake snowflake;
    private final AuthenticationManager authenticationManager;
    private final RedisTemplate<String, Object> redisTemplate;
    private final Environment env;

    public JwtTokenPair getTokenPair(LoginDTO loginDTO) {
        if (!Objects.equals(loginDTO.getCaptcha(), redisTemplate.opsForValue().get(loginDTO.getCaptchaKey())) && !Arrays.asList(env.getActiveProfiles()).contains("dev")) {
            throw new APIException(HttpStatus.HTTP_UNAUTHORIZED, "验证码不正确");
        }
        redisTemplate.delete(loginDTO.getCaptchaKey());
        Authentication authentication;
        try {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), RSAUtil.decrypt(loginDTO.getPassword()));
            authentication = authenticationManager.authenticate(authenticationToken);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("用户名或密码错误");
        } catch (InternalAuthenticationServiceException e) {
            throw new InternalAuthenticationServiceException("用户认证服务异常，请稍后再试");
        } catch (CryptoException e) {
            throw new CryptoException("密码解密失败，请检查加密逻辑");
        } catch (AuthenticationServiceException e) {
            throw new AuthenticationServiceException("认证失败");
        } catch (Exception e) {
            throw new APIException("系统错误，请联系管理员");
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
