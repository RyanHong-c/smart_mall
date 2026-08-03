package com.smartmall.payment.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PayType {

    ALIPAY(1, "支付宝"),
    WECHAT(2, "微信支付"),
    BANK_CARD(3, "银行卡");

    private final Integer code;
    private final String message;
}