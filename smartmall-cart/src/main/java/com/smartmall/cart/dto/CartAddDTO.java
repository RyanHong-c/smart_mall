package com.smartmall.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import lombok.Data;


@Data
public class CartAddDTO {


    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;


    /**
     * 商品ID
     */
    @NotNull(message = "商品ID不能为空")
    private Long productId;


    /**
     * 商品数量
     */
    @NotNull(message = "数量不能为空")
    @Min(
            value = 1,
            message = "数量必须大于0"
    )
    private Integer quantity;


}