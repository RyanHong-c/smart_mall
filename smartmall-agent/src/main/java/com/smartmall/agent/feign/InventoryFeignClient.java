package com.smartmall.agent.feign;

import com.smartmall.agent.dto.InventoryInfo;
import com.smartmall.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 库存服务 Feign 客户端
 */
@FeignClient(name = "inventory-service")
public interface InventoryFeignClient {

    @GetMapping("/api/inventory/{productId}")
    Result<InventoryInfo> getInventory(@PathVariable("productId") Long productId);
}
