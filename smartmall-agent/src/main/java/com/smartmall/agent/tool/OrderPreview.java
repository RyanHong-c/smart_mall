package com.smartmall.agent.tool;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 下单预览（写操作护栏：先返回待确认方案，用户确认后才真正下单）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderPreview {
    private String confirmCode;
    private String message;
    private Long userId;
    private Long productId;
    private String productName;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal totalAmount;
}
