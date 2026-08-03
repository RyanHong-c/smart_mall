package com.smartmall.category.vo;

import lombok.Data;

import java.util.List;

@Data
public class CategoryTreeVO {

    private Long id;

    private String name;

    private Long parentId;

    private List<CategoryTreeVO> children;
}
