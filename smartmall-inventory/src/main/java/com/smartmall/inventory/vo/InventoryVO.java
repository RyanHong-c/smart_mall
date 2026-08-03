package com.smartmall.inventory.vo;

import lombok.Data;

@Data
public class InventoryVO {
    private Long productId;
    private Integer stock;
    private Integer frozenStock;
    private Integer availableStock;
}