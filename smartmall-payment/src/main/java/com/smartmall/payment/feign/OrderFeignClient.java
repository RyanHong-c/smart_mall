package com.smartmall.payment.feign;

import com.smartmall.order.vo.OrderVO;
import com.smartmall.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "order-service")
public interface OrderFeignClient {

    @GetMapping("/api/order/{id}")
    Result<OrderVO> getById(@PathVariable("id") Long id);

    @PutMapping("/api/order/{id}/status")
    Result<OrderVO> updateStatus(@PathVariable("id") Long id, @RequestBody Map<String, Integer> body);
}