package com.smartmall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smartmall.order.dto.OrderCreateDTO;
import com.smartmall.order.dto.OrderQueryDTO;
import com.smartmall.order.dto.OrderStatusDTO;
import com.smartmall.order.entity.Order;

import java.util.List;

public interface OrderService extends IService<Order> {

    Order create(OrderCreateDTO dto);

    List<Order> list(OrderQueryDTO dto);

    Order getOrderById(Long id);

    Order updateStatus(Long id, OrderStatusDTO dto);

    void cancel(Long id);

    void delete(Long id);

    Long count(OrderQueryDTO dto);

}