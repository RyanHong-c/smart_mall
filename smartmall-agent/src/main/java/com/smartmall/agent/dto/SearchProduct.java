package com.smartmall.agent.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 搜索结果商品（对应 search-service 返回的 records 元素字段子集）
 */
@Data
public class SearchProduct {
    private Long id;
    private Long categoryId;
    private String name;
    private String subtitle;
    private BigDecimal price;
    private Integer stock;
}
