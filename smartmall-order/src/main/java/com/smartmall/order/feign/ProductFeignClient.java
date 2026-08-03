package com.smartmall.order.feign;

import com.smartmall.order.feign.fallback.ProductFeignClientFallback;
import com.smartmall.product.vo.ProductVO;
import com.smartmall.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 商品服务 Feign 客户端
 * 通过 Nacos 发现 product-service，调用其 HTTP 接口
 */
@FeignClient(name = "product-service", fallback = ProductFeignClientFallback.class)
public interface ProductFeignClient {

    @GetMapping("/api/product/{id}")
    Result<ProductVO> getById(@PathVariable("id") Long id);
}