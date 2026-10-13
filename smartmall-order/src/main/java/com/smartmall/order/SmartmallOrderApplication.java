package com.smartmall.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.smartmall.order", "com.smartmall.exception", "com.smartmall.config", "com.smartmall.utils"})
@EnableFeignClients(basePackages = "com.smartmall.order.feign")
@MapperScan("com.smartmall.order.mapper")
public class SmartmallOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartmallOrderApplication.class, args);
    }

}