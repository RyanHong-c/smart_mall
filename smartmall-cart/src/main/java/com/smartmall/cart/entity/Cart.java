package com.smartmall.cart.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

import java.time.LocalDateTime;


@Data
@TableName("cart")
public class Cart {


    @TableId(type = IdType.AUTO)
    private Long id;


    /**
     * 用户ID
     */
    private Long userId;


    /**
     * 商品ID
     */
    private Long productId;


    /**
     * 商品数量
     */
    private Integer quantity;


    /**
     * 是否选中
     */
    private Integer checked;


    private LocalDateTime createTime;


    private LocalDateTime updateTime;

}