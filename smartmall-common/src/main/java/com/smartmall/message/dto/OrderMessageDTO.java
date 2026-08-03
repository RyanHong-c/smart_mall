package com.smartmall.message.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderMessageDTO implements Serializable {

    private Long orderId;
    private String orderNo;
    private Long userId;
    private BigDecimal totalAmount;
    private String traceId;
    private String spanId;
    private List<OrderItemMessage> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemMessage implements Serializable {
        private Long productId;
        private String productName;
        private BigDecimal price;
        private Integer quantity;
    }
}