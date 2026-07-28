package com.smartmall.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
//RedisUtils —— 把 Java 对象和字符串互相转换的"翻译官"
public class RedisUtils {

    private final RedisTemplate<String,String> redisTemplate;
    //持有一份 ObjectMapper。注意:这里没有自己 new,也没有自定义 Bean,
    //所以用的是 Spring Boot 自动配置的默认 ObjectMapper(前提是项目里有 jackson-databind 依赖,Spring Boot Web 场景默认就带)。
    //这个默认的 ObjectMapper 如果 classpath 里还有 jackson-datatype-jsr310 依赖,Spring Boot 会自动帮你注册好 JavaTimeModule,
    //所以你的 LocalDateTime 字段才能正常序列化(这也解释了为什么你之前没遇到 LocalDateTime 报错的问题)
    private final ObjectMapper objectMapper;

    //定义一个 set 方法:传入 key、要存的对象(任意类型,Object)、以及过期时间(秒)
    public void set(String key,Object value,long timeout){
        try {
            //把传进来的 Java 对象(比如一个 Product)转换成 JSON 字符串
            String json = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key,json,Duration.ofSeconds(timeout));
        } catch (Exception e) {
            throw new RuntimeException(e);
            //如果 writeValueAsString 转换失败(比如对象里有 Jackson 不认识的类型),就捕获异常,
            //重新包装成 RuntimeException 抛出去。这里有个隐患:异常被"重新包装"抛出后,
            //如果调用方(ProductServiceImpl)没有专门 catch 处理,这个异常会一路往上抛到 Controller,
            //最终应该会被全局异常处理器捕获返回给前端。但如果项目里的全局异常处理器把这类异常"吃掉"只返回一个笼统的错误提示、没打日志,
            //你在后台就完全看不到报错信息——这也是之前我怀疑"缓存写入静默失败"的原因。
        }
    }

    public <T>T get(String key,Class<T> clazz){
        try {
            //从 Redis 里按 key 取出对应的字符串
            String json = redisTemplate.opsForValue().get(key);

            if(json == null){
                return null;
            }

            return objectMapper.readValue(json,clazz);

        } catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public void delete(String key){
        //删除指定 key 对应的缓存,RedisTemplate 提供的现成方法,直接调用即可
        redisTemplate.delete(key);
    }
}