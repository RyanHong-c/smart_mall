package com.smartmall.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartmall.enums.OrderStatus;
import com.smartmall.enums.ResultCode;
import com.smartmall.exception.BusinessException;
import com.smartmall.inventory.service.InventoryService;
import com.smartmall.order.dto.OrderCreateDTO;
import com.smartmall.order.dto.OrderItemCreateDTO;
import com.smartmall.order.dto.OrderQueryDTO;
import com.smartmall.order.dto.OrderStatusDTO;
import com.smartmall.order.entity.Order;
import com.smartmall.order.entity.OrderItem;
import com.smartmall.order.mapper.OrderMapper;
import com.smartmall.order.service.OrderItemService;
import com.smartmall.order.service.OrderService;
import com.smartmall.product.entity.Product;
import com.smartmall.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final OrderItemService orderItemService;

    private final ProductService productService;

    private final InventoryService inventoryService;

    @Override
    @Transactional
    public Order create(OrderCreateDTO dto) {
        Order order = new Order();
        order.setOrderNo(UUID.randomUUID().toString().replace("-", ""));
        order.setUserId(dto.getUserId());
        order.setStatus(OrderStatus.PENDING.getCode());
        BigDecimal totalAmount = BigDecimal.ZERO;
        save(order);
        for (OrderItemCreateDTO item : dto.getItems()) {
            Product product = productService.getById(item.getProductId());
            if (product == null) {
                throw new BusinessException(ResultCode.PRODUCT_NOT_EXIST);
            }
            inventoryService.decrease(item.getProductId(), item.getQuantity());
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setPrice(product.getPrice());
            orderItem.setQuantity(item.getQuantity());
            totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            orderItemService.save(orderItem);
        }
        order.setTotalAmount(totalAmount);
        updateById(order);
        return order;
    }

    @Override
    public List<Order> list(OrderQueryDTO dto) {

        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(dto.getUserId() != null, Order::getUserId, dto.getUserId())
                .eq(dto.getStatus() != null, Order::getStatus, dto.getStatus())
                .orderByDesc(Order::getCreateTime);

        return list(wrapper);
    }

    @Override
    public Order getOrderById(Long id) {

        Order order = getById(id);

        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_EXIST);
        }

        return order;
    }

    @Override
    @Transactional
    public Order updateStatus(Long id, OrderStatusDTO dto) {

        Order order = getOrderById(id);
        order.setStatus(dto.getStatus());
        updateById(order);

        return order;
    }

    @Override
    @Transactional
    public void cancel(Long id) {

        Order order = getOrderById(id);
        order.setStatus(OrderStatus.CANCELLED.getCode());
        updateById(order);
    }

    @Override
    @Transactional
    public void delete(Long id) {

        getOrderById(id);

        orderItemService.removeByOrderId(id);
        removeById(id);
    }

    @Override
    public Long count(OrderQueryDTO dto) {

        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(dto.getUserId() != null, Order::getUserId, dto.getUserId())
                .eq(dto.getStatus() != null, Order::getStatus, dto.getStatus());

        return count(wrapper);
    }

}