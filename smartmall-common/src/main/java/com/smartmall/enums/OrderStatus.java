package com.smartmall.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatus {

    PENDING(0, "待支付"),
    PAID(1, "已支付"),
    SHIPPED(2, "已发货"),
    FINISHED(3, "已完成"),
    CANCELLED(4, "已取消");

    private final Integer code;
    private final String message;

    public static String getMessage(Integer code) {

        for (OrderStatus status : OrderStatus.values()) {
            if (status.getCode().equals(code)) {
                return status.getMessage();
            }
        }

        return "未知状态";
    }

    public static OrderStatus getByCode(Integer code) {

        for (OrderStatus status : OrderStatus.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }

        return null;
    }

}