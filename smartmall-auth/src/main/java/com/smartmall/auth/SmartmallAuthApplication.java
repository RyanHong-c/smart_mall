package com.smartmall.auth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.smartmall")
@MapperScan("com.smartmall.**.mapper")
public class SmartmallAuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartmallAuthApplication.class, args);
	}

}