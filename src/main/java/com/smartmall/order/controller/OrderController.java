package com.smartmall.order.controller;

import com.smartmall.common.Result;
import com.smartmall.order.converter.OrderConverter;
import com.smartmall.order.converter.OrderItemConverter;
import com.smartmall.order.dto.OrderCreateDTO;
import com.smartmall.order.dto.OrderQueryDTO;
import com.smartmall.order.dto.OrderStatusDTO;
import com.smartmall.order.entity.Order;
import com.smartmall.order.entity.OrderItem;
import com.smartmall.order.service.OrderItemService;
import com.smartmall.order.service.OrderService;
import com.smartmall.order.vo.OrderDetailVO;
import com.smartmall.order.vo.OrderVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderItemService orderItemService;
    private final OrderConverter orderConverter;
    private final OrderItemConverter orderItemConverter;

    @PostMapping
    public Result<OrderVO> create(@RequestBody @Valid OrderCreateDTO dto) {

        Order order = orderService.create(dto);

        return Result.success(orderConverter.toVO(order));
    }

    @GetMapping("/{id}")
    public Result<OrderDetailVO> detail(@PathVariable Long id) {

        Order order = orderService.getOrderById(id);
        List<OrderItem> items = orderItemService.getByOrderId(id);

        OrderDetailVO vo = orderConverter.toDetailVO(order);
        vo.setItems(orderItemConverter.toVOList(items));

        return Result.success(vo);
    }

    @GetMapping("/list")
    public Result<List<OrderVO>> list(OrderQueryDTO dto) {

        List<Order> list = orderService.list(dto);

        return Result.success(orderConverter.toVOList(list));
    }

    @PutMapping("/{id}/status")
    public Result<OrderVO> updateStatus(@PathVariable Long id,@RequestBody @Valid OrderStatusDTO dto) {

        Order order = orderService.updateStatus(id,dto);

        return Result.success(orderConverter.toVO(order));
    }

    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id) {

        orderService.cancel(id);

        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {

        orderService.delete(id);

        return Result.success();
    }

}