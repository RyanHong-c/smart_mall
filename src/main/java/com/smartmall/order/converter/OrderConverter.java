package com.smartmall.order.converter;

import com.smartmall.enums.OrderStatus;
import com.smartmall.order.entity.Order;
import com.smartmall.order.vo.OrderDetailVO;
import com.smartmall.order.vo.OrderVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderConverter {
    public OrderVO toVO(Order order) {
        if (order == null) {
            return null;
        }
        OrderVO vo = new OrderVO();
        BeanUtils.copyProperties(order, vo);
        vo.setStatusName(OrderStatus.getMessage(order.getStatus()));
        return vo;
    }

    public List<OrderVO> toVOList(List<Order> list) {
        return list.stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    public OrderDetailVO toDetailVO(Order order) {
        if (order == null) {
            return null;
        }
        OrderDetailVO vo = new OrderDetailVO();
        BeanUtils.copyProperties(order, vo);
        vo.setStatusName(OrderStatus.getMessage(order.getStatus()));
        return vo;
    }
}