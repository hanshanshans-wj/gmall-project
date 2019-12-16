package com.wuju.gmall.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {
// 实现软编码： @Value 表示从application.properties 中获取数据
    // :disabled 表示如果从配置文件中没有找到对应的数据则给 一个默认值 “disabled”
    @Value("${spring.redis.host:disabled}")
    private String host;
    @Value("${spring.redis.port:0}")
    private int port;
    @Value("${spring.redis.timeOut:10000}")
    private int timeOut;
    @Value("${spring.redis.password:10086}")
    private String password;
    // 将host,port,timeOut 给 initJedisPool 方法使用！
    // @Bean 表示一个bean 标签 RedistUtil 注入到spring 容器\
    /*
        <bean class="com.atguigu.gmall0624.config.RedistUtil">
        </bean>
     */
    @Bean
    public RedisUtil getRedisUtil(){
        if ("disabled".equals(host)){
            return null;
        }
        RedisUtil redisUtil = new RedisUtil();
        redisUtil.initJedisPool(host,port,timeOut,password);
        return redisUtil;
    }

}
