package com.smartmall.security.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtils {

    /**
     * 签名密钥（通过环境变量 JWT_SECRET 覆盖；生产环境必须配置，不要用默认值）
     */
    @Value("${jwt.secret:smartmall-smartmall-smartmall-secret-key}")
    private String secret;

    /** Token 过期时间：24 小时 */
    private static final long EXPIRE_TIME = 1000 * 60 * 60 * 24;

    private SecretKey key() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 创建 Token
     */
    public String createToken(Long userId) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_TIME))
                .signWith(key())
                .compact();
    }

    /**
     * 解析 Token，获取用户 ID
     */
    public Long getUserId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return Long.valueOf(claims.getSubject());
    }
}
