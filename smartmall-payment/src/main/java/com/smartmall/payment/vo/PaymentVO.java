package com.smartmall.payment.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentVO {

    private Long id;

    private Long orderId;

    private String orderNo;

    private Long userId;

    private BigDecimal amount;

    private Integer payType;

    private String payTypeName;

    private String payNo;

    private Integer status;

    private String statusName;

    private LocalDateTime payTime;

    private LocalDateTime createTime;
}