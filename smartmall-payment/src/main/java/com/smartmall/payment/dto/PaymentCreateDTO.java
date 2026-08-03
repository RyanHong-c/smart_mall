package com.smartmall.payment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentCreateDTO {

    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    @NotNull(message = "支付方式不能为空")
    private Integer payType;
}