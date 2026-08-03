package com.smartmall.order.feign;

import com.smartmall.order.feign.fallback.InventoryFeignClientFallback;
import com.smartmall.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 库存服务 Feign 客户端
 * 通过 Nacos 发现 inventory-service，调用其 HTTP 接口
 */
@FeignClient(name = "inventory-service", fallback = InventoryFeignClientFallback.class)
public interface InventoryFeignClient {

    @PostMapping("/api/inventory/decrease")
    Result<Void> decrease(@RequestParam("productId") Long productId,
                          @RequestParam("count") Integer count);
}