package com.smartmall.agent.tool;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 工具返回给大模型的商品结构（会被序列化为 JSON 喂给 LLM）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductInfo {

    private Long id;
    private String name;
    private String category;
    private BigDecimal price;
    private Integer stock;
}
