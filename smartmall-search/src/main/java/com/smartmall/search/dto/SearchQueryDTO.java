package com.smartmall.search.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SearchQueryDTO {

    private String keyword;

    private Long categoryId;

    private BigDecimal minPrice;

    private BigDecimal maxPrice;

    private String sort;

    private Integer page = 1;

    private Integer size = 10;
}