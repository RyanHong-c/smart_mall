package com.smartmall.product.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductVO {

    private Long id;

    private Long categoryId;

    private String categoryName;

    private String name;

    private String subtitle;

    private BigDecimal price;

    private Integer stock;

    private Integer sales;

    private String cover;

    private String description;

    private Integer status;

    private LocalDateTime createTime;
}