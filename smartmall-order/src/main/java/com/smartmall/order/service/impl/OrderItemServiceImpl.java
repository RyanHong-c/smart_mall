package com.smartmall.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartmall.order.entity.OrderItem;
import com.smartmall.order.mapper.OrderItemMapper;
import com.smartmall.order.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements OrderItemService {

    @Override
    public List<OrderItem> getByOrderId(Long orderId) {

        return lambdaQuery()
                .eq(OrderItem::getOrderId, orderId)
                .list();
    }

    @Override
    @Transactional
    public void removeByOrderId(Long orderId) {

        LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(OrderItem::getOrderId, orderId);

        remove(wrapper);
    }

}