package com.smartmall.agent;

import com.alibaba.druid.spring.boot3.autoconfigure.DruidDataSourceAutoConfigure;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.redisson.spring.starter.RedissonAutoConfigurationV2;

/**
 * SmartMall LLM 智能体服务
 *
 * 只负责“LLM 编排层”，不直接连接数据库/Redis/MQ，
 * 因此显式排除由根 POM 全局依赖引入的、本服务用不到的数据源等自动配置。
 */
@EnableFeignClients(basePackages = "com.smartmall.agent.feign")
@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        DruidDataSourceAutoConfigure.class,
        MybatisPlusAutoConfiguration.class,
        RedisAutoConfiguration.class,
        RedissonAutoConfigurationV2.class
})
public class SmartmallAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartmallAgentApplication.class, args);
    }
}
