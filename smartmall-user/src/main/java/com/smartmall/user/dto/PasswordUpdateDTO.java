package com.smartmall.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 修改密码参数
 */
@Data
public class PasswordUpdateDTO {
    /**
     * 原密码
     */
    @NotBlank(message = "原密码不能为空")
    private String oldPassword;

    /**
     * 新密码
     */
    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}