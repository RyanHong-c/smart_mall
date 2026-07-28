package com.smartmall.security.filter;

import com.smartmall.security.utils.JwtUtils;
import com.smartmall.security.model.LoginUser;
import com.smartmall.security.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT认证过滤器
 * 每次请求都会经过这里
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        //1. 获取请求头Token
        String authorization = request.getHeader("Authorization");
        //没有Token直接放行
        if (!StringUtils.hasText(authorization) || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        //2. 截取Token
        String token = authorization.substring(7);
        //3. 解析用户名
        Long userId =
                jwtUtils.getUserId(token);
        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            //4. 根据用户ID查询数据库用户
            LoginUser loginUser = userDetailsService.loadUserById(userId);
            //5. 创建认证对象
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
            //6. 存入Security上下文
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        //继续执行请求
        filterChain.doFilter(request, response);
    }
}