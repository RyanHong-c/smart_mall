package com.smartmall.security.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartmall.security.model.LoginUser;
import com.smartmall.user.entity.User;
import com.smartmall.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
//告诉 Spring Security：用户不要去内存找，去 MySQL 查
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserMapper userMapper;

    public LoginUser loadUserById(Long id){

        User user = userMapper.selectById(id);

        if(user == null){
            throw new UsernameNotFoundException(
                    "用户不存在"
            );
        }

        return new LoginUser(user);
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, username)
        );
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        return new LoginUser(user);
    }
}