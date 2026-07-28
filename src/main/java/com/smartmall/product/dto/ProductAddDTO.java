package com.smartmall.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductAddDTO {

    @NotNull(message = "分类不能为空")
    private Long categoryId;

    @NotBlank(message = "商品名称不能为空")
    private String name;

    private String subtitle;

    @NotNull(message = "价格不能为空")
    private BigDecimal price;

    private Integer stock;

    private String cover;

    private String description;
}