package com.smartmall.security.config;

import com.smartmall.security.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                //关闭csrf
                //为什么要关:CSRF 防护是 Spring Security 默认开启的一个机制,
                //它原本是为基于 Session 的传统 Web 应用设计的——防止用户在登录了A网站的情况下,
                //被诱导点击了恶意网站B上的链接,B网站利用用户浏览器里还留存的A网站 Session Cookie,
                //伪造一个请求冒充用户操作A网站。
                //
                //但你现在做的是 JWT 认证,前后端分离,浏览器不会自动带上 Cookie 去认证
                //(Token 是你手动放在请求头 Authorization 里的),
                //所以 CSRF 这套针对 Cookie/Session 的攻击方式对你的项目根本不适用,
                //如果不关掉,Spring Security 会要求所有非GET请求都带上一个"CSRF Token",
                // 而你的前端根本不会提供这个东西,会导致所有 POST/PUT/DELETE 请求全部被拦截报错。
                // 所以基于JWT的前后端分离项目,关闭CSRF是标准做法
                .csrf(csrf -> csrf.disable())
                //配置请求权限
                .authorizeHttpRequests(auth -> auth
                        //登录接口放行
                        //requestMatchers("/auth/login") 表示"匹配这个具体路径的请求";
                        //permitAll() 表示"允许所有人访问,不需要登录、不需要任何权限"
                        //这行的意思就是:访问 /auth/login 这个接口,不需要登录
                        .requestMatchers("/api/user/login").permitAll()
                        //注册接口放行
                        .requestMatchers("/api/user/register").permitAll()
                        //其他所有接口需要认证
                        .anyRequest().authenticated()
                );
        http.addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
        );
        return http.build();
    }
}