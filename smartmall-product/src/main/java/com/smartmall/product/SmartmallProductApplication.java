package com.smartmall.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.smartmall")
public class SmartmallProductApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartmallProductApplication.class, args);
	}

}
