package com.wuju.gmall.config;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisUtil {
    /**创建连接池
     * 初始化连接池
     * 获取连接池
     */

    private JedisPool jedisPool;

    //初始化连接池
    public void initJedisPool(String host,int port,int timeOut,String password){
        // 创建配置连接池的参数类
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        //达到最大连接数时，需要放入等待队列中！
            jedisPoolConfig.setBlockWhenExhausted(true);
        // 最小剩余数
            jedisPoolConfig.setMinIdle(10);
        // 设置等待时间
            jedisPoolConfig.setMaxWaitMillis(10*1000);
        // 核心数
            jedisPoolConfig.setMaxTotal(200);
        // 获取到连接之后，自检！
        jedisPoolConfig.setTestOnBorrow(true);
        // ,password
        jedisPool=new JedisPool(jedisPoolConfig,host,port,timeOut,password);
    }

    public Jedis getJedis(){
        Jedis jedis = jedisPool.getResource();
        return jedis;
    }
}
