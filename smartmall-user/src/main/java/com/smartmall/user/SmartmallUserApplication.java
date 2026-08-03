package com.smartmall.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.smartmall")
public class SmartmallUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartmallUserApplication.class, args);
    }

}