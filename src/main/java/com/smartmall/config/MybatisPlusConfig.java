package com.smartmall.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan({
        "com.smartmall.user.mapper",
        "com.smartmall.product.mapper",
        "com.smartmall.category.mapper",
        "com.smartmall.inventory.mapper",
        "com.smartmall.cart.mapper",
        "com.smartmall.order.mapper"
})
public class MybatisPlusConfig {

}