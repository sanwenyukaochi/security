package com.sanwenyukaochi.security.security.jwt;


import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class JwtUtils {

    public String getJwtFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public String generateTokenFromUsername(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000))
                .signWith(RSAUtil.getPrivateKey(), Jwts.SIG.RS256)
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith(RSAUtil.getPublicKey())
                .build().parseSignedClaims(token)
                .getPayload().getSubject();
    }


    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().verifyWith(RSAUtil.getPublicKey()).build().parseSignedClaims(authToken);
            return true;
        } catch (MalformedJwtException e) {
            log.error("无效的JWT令牌: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT令牌已过期: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("不支持的JWT令牌: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT声明字符串为空: {}", e.getMessage());
        }
        return false;
    }
}
