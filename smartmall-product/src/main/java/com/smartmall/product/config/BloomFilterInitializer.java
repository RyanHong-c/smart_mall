package com.smartmall.product.config;

import com.smartmall.product.entity.Product;
import com.smartmall.product.mapper.ProductMapper;
import com.smartmall.utils.BloomFilterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BloomFilterInitializer implements CommandLineRunner {

    private final ProductMapper productMapper;
    private final BloomFilterService bloomFilterService;

    @Override
    public void run(String... args) {
        log.info("开始初始化商品布隆过滤器...");
        List<Product> products = productMapper.selectList(null);
        for (Product product : products) {
            bloomFilterService.add(product.getId());
        }
        log.info("商品布隆过滤器初始化完成，共加载 {} 个商品ID", products.size());
    }
}