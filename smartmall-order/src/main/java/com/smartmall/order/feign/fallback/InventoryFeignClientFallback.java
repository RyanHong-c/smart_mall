package com.smartmall.order.feign.fallback;

import com.smartmall.order.feign.InventoryFeignClient;
import com.smartmall.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class InventoryFeignClientFallback implements InventoryFeignClient {

    @Override
    public Result<Void> decrease(Long productId, Integer count) {
        log.error("库存服务调用失败，触发熔断降级: productId={}, count={}", productId, count);
        return Result.fail(500, "库存服务暂不可用");
    }
}