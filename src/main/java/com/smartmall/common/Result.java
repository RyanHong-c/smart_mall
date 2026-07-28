package com.smartmall.common;

import com.smartmall.enums.ResultCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {

    private Integer code;

    private String message;

    private T data;

    // ========== 成功 ==========
    public static <T> Result<T> success(T data) {
        return new Result<>(
                ResultCode.SUCCESS.getCode(),
                ResultCode.SUCCESS.getMessage(),
                data
        );
    }

    public static <T> Result<T> success() {
        return new Result<>(
                ResultCode.SUCCESS.getCode(),
                ResultCode.SUCCESS.getMessage(),
                null
        );
    }

    // ========== 失败（传入枚举） ==========
    public static <T> Result<T> fail(ResultCode resultCode) {
        return new Result<>(
                resultCode.getCode(),
                resultCode.getMessage(),
                null
        );
    }

    // ========== 失败（自定义消息，但保留状态码） ==========
    public static <T> Result<T> fail(ResultCode resultCode, String customMessage) {
        return new Result<>(
                resultCode.getCode(),
                customMessage,
                null
        );
    }

    // ========== 失败（快捷方式，兜底用） ==========
    public static <T> Result<T> error(String msg) {
        return new Result<>(
                ResultCode.FAIL.getCode(),
                msg,
                null
        );
    }

    // ========== 保留灵活性：完全自定义 ==========
    public static <T> Result<T> fail(Integer code, String message) {
        return new Result<>(code, message, null);
    }

}