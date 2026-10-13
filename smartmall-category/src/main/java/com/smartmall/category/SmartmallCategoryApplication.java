package com.smartmall.category;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.smartmall")
public class SmartmallCategoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartmallCategoryApplication.class, args);
    }

}