package com.smartmall.order.converter;

import com.smartmall.order.entity.OrderItem;
import com.smartmall.order.vo.OrderItemVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderItemConverter {

    public OrderItemVO toVO(OrderItem item) {

        if (item == null) {
            return null;
        }

        OrderItemVO vo = new OrderItemVO();

        BeanUtils.copyProperties(item, vo);

        vo.setSubtotal(
                item.getPrice().multiply(
                        BigDecimal.valueOf(item.getQuantity())
                )
        );

        return vo;
    }

    public List<OrderItemVO> toVOList(List<OrderItem> list) {

        return list.stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

}