package com.smartmall.agent.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单信息（对应 order-service 的 OrderDetailVO 字段子集）
 */
@Data
public class OrderInfo {
    private Long id;
    private String orderNo;
    private Long userId;
    private BigDecimal totalAmount;
    private Integer status;
    private String statusName;
    private LocalDateTime createTime;
    private List<OrderItemInfo> items;
}
