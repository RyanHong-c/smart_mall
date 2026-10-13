package com.smartmall.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.smartmall")
public class SmartmallInventoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartmallInventoryApplication.class, args);
    }

}