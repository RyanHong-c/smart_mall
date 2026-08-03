package com.smartmall.order.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartmall.result.Result;
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
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderItemService orderItemService;
    private final OrderConverter orderConverter;
    private final OrderItemConverter orderItemConverter;

    @PostMapping
    @SentinelResource(value = "createOrder", blockHandler = "createOrderBlock")
    public Result<OrderVO> create(@RequestBody @Valid OrderCreateDTO dto) {

        Order order = orderService.create(dto);

        return Result.success(orderConverter.toVO(order));
    }

    public Result<OrderVO> createOrderBlock(OrderCreateDTO dto, BlockException ex) {
        return Result.fail(429, "下单请求过多，请稍后再试");
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

    @GetMapping("/page")
    public Result<Page<OrderVO>> page(OrderQueryDTO dto) {
        Page<Order> result = orderService.page(dto);
        Page<OrderVO> voPage = new Page<>();
        voPage.setCurrent(result.getCurrent());
        voPage.setSize(result.getSize());
        voPage.setTotal(result.getTotal());
        voPage.setRecords(orderConverter.toVOList(result.getRecords()));
        return Result.success(voPage);
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