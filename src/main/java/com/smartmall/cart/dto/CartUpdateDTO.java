package com.smartmall.cart.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import lombok.Data;


@Data
public class CartUpdateDTO {


    /**
     * 数量
     */
    @NotNull(message = "数量不能为空")
    @Min(
            value = 1,
            message = "数量必须大于0"
    )
    private Integer quantity;


}