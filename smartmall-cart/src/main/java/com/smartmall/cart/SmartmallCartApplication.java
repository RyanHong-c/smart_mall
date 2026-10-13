package com.smartmall.cart;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.smartmall")
@MapperScan("com.smartmall.cart.mapper")
public class SmartmallCartApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartmallCartApplication.class, args);
    }

}