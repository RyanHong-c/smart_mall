package com.smartmall.search.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SearchProductVO {

    private Long id;

    private Long categoryId;

    private String name;

    private String subtitle;

    private String cover;

    private BigDecimal price;

    private Integer stock;

    private Integer sales;

    private Integer status;
}