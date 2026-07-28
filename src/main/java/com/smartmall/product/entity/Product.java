package com.smartmall.product.entity;

import com.baomidou.mybatisplus.annotation.*;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("product")
public class Product {

    @TableId(
            value = "id",
            type = IdType.AUTO
    )
    private Long id;

    private Long categoryId;

    private String name;

    private String subtitle;

    private BigDecimal price;

    private Integer stock;

    private Integer sales;

    private String cover;

    private String description;

    private Integer status;

    @TableLogic
    private Integer deleted;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}