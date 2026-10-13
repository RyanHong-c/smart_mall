package com.smartmall.user.dto;

import lombok.Data;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


//用户名与密码不允许修改（专用通道）
@Data
public class UserUpdateDTO {

    @Size(max = 50, message = "昵称长度不能超过50位")
    private String nickname;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "邮箱格式不正确")
    private String email;

    private Integer gender;

    private String avatar;
}