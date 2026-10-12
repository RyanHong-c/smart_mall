package com.smartmall.agent.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemInfo {
    private Long productId;
    private String productName;
    private BigDecimal price;
    private Integer quantity;
}
