package com.smartmall.cart.dto;


import lombok.Data;


@Data
public class CartQueryDTO {


    /**
     * 用户ID
     */
    private Long userId;


    /**
     * 是否只查询选中商品
     */
    private Integer checked;


}