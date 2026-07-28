package com.smartmall.user.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户返回对象
 * 不包含密码
 */
@Data
public class UserVO {
    private Long id;
    private String username;
    private String nickname;
    private String phone;
    private String email;
    private Integer gender;
    private String avatar;
    private Integer status;
    private LocalDateTime createTime;
}