package com.smartmall.agent.dto;

import lombok.Data;

import java.util.List;

/**
 * 下单请求（对应 order-service 的 OrderCreateDTO）
 */
@Data
public class CreateOrderRequest {
    private Long userId;
    private List<Item> items;

    @Data
    public static class Item {
        private Long productId;
        private Integer quantity;
    }
}
