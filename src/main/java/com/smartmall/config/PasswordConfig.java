package com.smartmall.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码加密配置
 */
@Configuration
public class PasswordConfig {
    /**
     * BCrypt密码加密器
     */
    //我们之前聊 @Component 时说过——它是加在你自己写的类上的。
    // 但 BCryptPasswordEncoder 是 Spring Security 框架自带的类，类的源码你不能改，
    // 没法在它上面加 @Component（就算能改，也不该去改第三方库的源码）。
    //
    //这时候就需要 @Configuration + @Bean 这套组合——用来把"你不能加注解的第三方类"，
    // 手动注册成 Spring 能管理的 Bean。这是 Spring 里处理第三方库对象的标准解法：
    // 自己的类用 @Component（或 @Service/@Controller 这些语义化变体），
    // 别人的类用 @Configuration 类里的 @Bean 方法。
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}