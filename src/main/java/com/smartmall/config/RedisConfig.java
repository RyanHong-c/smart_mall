package com.smartmall.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
//RedisConfig：告诉 Spring 怎么"连接"和"存取" Redis
public class RedisConfig {

    @Bean
    //RedisTemplate 是 Spring 提供的操作 Redis 的工具类,你可以理解成操作 Redis 的遥控器
    public RedisTemplate<String,String> redisTemplate(RedisConnectionFactory factory){
        //new 出一个空白的 RedisTemplate 实例,接下来要一步步"装配"它
        RedisTemplate<String,String> template = new RedisTemplate<>();
        //把连接工厂塞给 template,这样它才知道该连哪个 Redis 服务器
        //(host、port、密码等信息都在 factory 里,而这些信息来自 application.yml 的配置)
        template.setConnectionFactory(factory);
        //设置 key 的序列化方式为纯字符串。也就是说你在 Java 里写 "product:detail:1" 这个 key,
        //存到 Redis 里就是原封不动的 product:detail:1(不会被加上多余的引号或转义符),
        //这也是为什么你在 redis-cli 里能直接用 get product:detail:1 查到,而不需要猜测什么奇怪的编码
        template.setKeySerializer(new StringRedisSerializer());
        //redisTemplate不负责任何将对象转化为 json 字符串的作用
        template.setValueSerializer(new StringRedisSerializer());
        //这是 Spring 提供的一个生命周期方法,手动触发一次"属性设置完毕后的初始化检查"
        template.afterPropertiesSet();

        return template;
    }
}