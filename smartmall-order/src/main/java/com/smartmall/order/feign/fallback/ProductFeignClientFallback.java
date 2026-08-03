package com.smartmall.order.feign.fallback;

import com.smartmall.order.feign.ProductFeignClient;
import com.smartmall.product.vo.ProductVO;
import com.smartmall.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProductFeignClientFallback implements ProductFeignClient {

    @Override
    public Result<ProductVO> getById(Long id) {
        log.error("商品服务调用失败，触发熔断降级: productId={}", id);
        return Result.fail(500, "商品服务暂不可用");
    }
}