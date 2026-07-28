package com.smartmall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smartmall.order.entity.OrderItem;

import java.util.List;

public interface OrderItemService extends IService<OrderItem> {

    List<OrderItem> getByOrderId(Long orderId);

    void removeByOrderId(Long orderId);

}