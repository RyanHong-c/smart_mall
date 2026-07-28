package com.smartmall.category.vo;

import lombok.Data;

@Data
public class CategoryVO {

    private Long id;

    private String name;

    private Long parentId;

    private Integer sort;

    private Integer status;
}