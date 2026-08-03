package com.smartmall.security.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtils {

    //这是签名密钥的原始文本——你可以理解成"盖章用的私章内容"。
    //这个字符串必须保密(不能泄露给前端或者写在能被公开访问的地方),因为任何拿到这个字符串的人都能伪造合法的Token
    private static final String SECRET = "smartmall-smartmall-smartmall-secret-key";

    //SECRET.getBytes(StandardCharsets.UTF_8):把字符串按UTF-8 编码转成字节阵列(计算机层面,任何字符串本质上都是字节)
    //这是JJWT提供的工具方法，把字节存储打包成一个SecretKey对象，专门用于HMAC-SHA系列签名算法
    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    //24小时后会自动过期
    private static final long EXPIRE_TIME = 1000 * 60 * 60 * 24;

    /**
     * 创建Token
     */
    //制卡
    public String createToken(Long userId) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_TIME))
                .signWith(KEY)
                .compact();
    }

    /**
     * 获取用户ID
     */
    //验卡
    public Long getUserId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return Long.valueOf(claims.getSubject());
    }
}