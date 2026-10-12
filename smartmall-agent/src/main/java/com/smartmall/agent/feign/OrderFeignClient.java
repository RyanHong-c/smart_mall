package com.smartmall.agent.feign;

import com.smartmall.agent.dto.CreateOrderRequest;
import com.smartmall.agent.dto.OrderInfo;
import com.smartmall.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 订单服务 Feign 客户端
 */
@FeignClient(name = "order-service")
public interface OrderFeignClient {

    @PostMapping("/api/order")
    Result<OrderInfo> create(@RequestBody CreateOrderRequest request);

    @GetMapping("/api/order/{id}")
    Result<OrderInfo> getById(@PathVariable("id") Long id);
}
