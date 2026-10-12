package com.smartmall.agent.feign;

import com.smartmall.product.vo.ProductVO;
import com.smartmall.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * 商品服务 Feign 客户端
 * 通过 Nacos 发现 product-service，真实调用商品微服务。
 */
@FeignClient(name = "product-service")
public interface ProductFeignClient {

    @GetMapping("/api/product/list")
    Result<List<ProductVO>> list();

    @GetMapping("/api/product/{id}")
    Result<ProductVO> getById(@PathVariable("id") Long id);
}
