package com.smartmall.agent.dto;

import lombok.Data;

/**
 * 库存信息（对应 inventory-service 的 InventoryVO 字段子集）
 */
@Data
public class InventoryInfo {
    private Long productId;
    private Integer stock;
    private Integer frozenStock;
    private Integer availableStock;
}
