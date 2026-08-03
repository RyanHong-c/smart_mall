package com.smartmall.order.dto;

import lombok.Data;

@Data
public class OrderQueryDTO {

    private Long userId;

    private Integer status;

    private Integer page = 1;

    private Integer size = 10;

}