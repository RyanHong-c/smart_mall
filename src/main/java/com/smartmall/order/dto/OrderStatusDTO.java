package com.smartmall.order.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderStatusDTO {

    @NotNull(message = "订单状态不能为空")
    private Integer status;

}