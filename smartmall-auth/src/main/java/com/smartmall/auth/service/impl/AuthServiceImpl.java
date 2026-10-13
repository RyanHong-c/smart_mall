package com.smartmall.auth.service.impl;

import com.smartmall.auth.dto.LoginDTO;
import com.smartmall.security.utils.JwtUtils;
import com.smartmall.auth.vo.LoginVO;
import com.smartmall.auth.service.AuthService;
import com.smartmall.enums.ResultCode;
import com.smartmall.exception.BusinessException;
import com.smartmall.user.entity.User;
import com.smartmall.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;         // 注意：这里是依赖UserService,不是继承
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    public LoginVO login(LoginDTO dto) {
        // 之前在 UserServiceImpl.login() 里的逻辑，原样搬过来：
        // 1. 根据用户名查用户（需要UserService提供一个 getByUsername 方法）
        User user = userService.getByUsername(dto.getUsername());
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }
        // 2. 校验密码
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR);
        }
        // 3. 生成Token
        String token = jwtUtils.createToken(user.getId());

        // 4. 封装返回
        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        return vo;
    }
}