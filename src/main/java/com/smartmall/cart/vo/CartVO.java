package com.smartmall.cart.vo;


import lombok.Data;


@Data
public class CartVO {


    /**
     * 购物车ID
     */
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


}