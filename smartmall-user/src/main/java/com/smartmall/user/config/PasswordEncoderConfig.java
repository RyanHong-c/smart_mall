package com.smartmall.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码编码器配置
 * 原本在 smartmall-security 的 SecurityConfig 中定义，
 * 拆分后 user 模块不依赖 security，需要自己提供 BCryptPasswordEncoder
 */
@Configuration
public class PasswordEncoderConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}