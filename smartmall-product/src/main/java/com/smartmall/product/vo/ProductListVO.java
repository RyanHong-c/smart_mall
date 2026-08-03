package com.smartmall.product.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductListVO {

    private Long id;

    private String name;

    private BigDecimal price;

    private Integer stock;

    private Integer sales;

    private String cover;

    private String categoryName;
}