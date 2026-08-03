package com.smartmall.security.model;

import com.smartmall.user.entity.User;
import lombok.Data;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collections;

@Data
//Spring Security内部认证时只认识UserDetails这个接口类型，
//而不认识你自己的User实体。这个类的作用就是“包层壳”，让你的User对象能够被Spring Security读懂
public class LoginUser implements UserDetails {
    private User user;

    public LoginUser(User user) {
        this.user = user;
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public boolean isEnabled() {
        return user.getStatus() == 1;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public java.util.Collection getAuthorities() {
        return Collections.emptyList();
    }
}