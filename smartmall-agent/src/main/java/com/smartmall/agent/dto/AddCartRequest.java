package com.smartmall.agent.dto;

import lombok.Data;

/**
 * 加购请求（对应 cart-service 的 CartAddDTO）
 */
@Data
public class AddCartRequest {
    private Long userId;
    private Long productId;
    private Integer quantity;
}
