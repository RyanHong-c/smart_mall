package com.smartmall.user.converter;

import com.smartmall.user.entity.User;
import com.smartmall.user.vo.UserVO;
import org.springframework.stereotype.Component;

/**
 * 用户对象转换器
 */
@Component
public class UserConverter {
    /**
     * User 转 UserVO
     *
     * @param user 用户实体
     * @return 用户返回对象
     */
    public UserVO toVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setPhone(user.getPhone());
        vo.setEmail(user.getEmail());
        vo.setGender(user.getGender());
        vo.setAvatar(user.getAvatar());
        vo.setStatus(user.getStatus());
        vo.setCreateTime(user.getCreateTime());
        return vo;
    }
}