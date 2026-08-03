package com.smartmall.payment.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentCallbackDTO {

    private String payNo;

    private String orderNo;

    private BigDecimal amount;

    private Integer status;

    private String sign;
}