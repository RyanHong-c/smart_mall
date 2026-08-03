package com.smartmall.payment.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentStatus {

    PENDING(0, "待支付"),
    SUCCESS(1, "支付成功"),
    FAILED(2, "支付失败"),
    REFUNDING(3, "退款中"),
    REFUNDED(4, "已退款");

    private final Integer code;
    private final String message;

    public static String getMessage(Integer code) {
        for (PaymentStatus status : PaymentStatus.values()) {
            if (status.getCode().equals(code)) {
                return status.getMessage();
            }
        }
        return "未知状态";
    }
}