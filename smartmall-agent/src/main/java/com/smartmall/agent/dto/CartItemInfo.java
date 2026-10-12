package com.smartmall.agent.dto;

import lombok.Data;

/**
 * 购物车条目（对应 cart-service 的 CartVO 字段子集）
 */
@Data
public class CartItemInfo {
    private Long id;
    private Long userId;
    private Long productId;
    private Integer quantity;
    private Integer checked;
}
