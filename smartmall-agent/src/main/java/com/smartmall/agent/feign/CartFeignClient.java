package com.smartmall.agent.feign;

import com.smartmall.agent.dto.AddCartRequest;
import com.smartmall.agent.dto.CartItemInfo;
import com.smartmall.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 购物车服务 Feign 客户端
 */
@FeignClient(name = "cart-service")
public interface CartFeignClient {

    @PostMapping("/api/cart")
    Result<CartItemInfo> add(@RequestBody AddCartRequest request);

    @GetMapping("/api/cart/{userId}")
    Result<List<CartItemInfo>> list(@PathVariable("userId") Long userId);
}
