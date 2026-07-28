package com.smartmall.product.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductUpdateDTO {

    private Long categoryId;

    private String name;

    private String subtitle;

    private BigDecimal price;

    private Integer stock;

    private String cover;

    private String description;

    private Integer status;
}