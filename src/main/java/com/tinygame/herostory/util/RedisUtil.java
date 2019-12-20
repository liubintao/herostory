package com.tinygame.herostory.util;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * redis 连接工具类
 */
@Slf4j
public final class RedisUtil {

    /**
     * redis 连接池
     */
    private static JedisPool _jedisPool = null;

    /**
     * 私有化类默认构造器
     */
    private RedisUtil() {
    }

    /**
     * 初始化连接池
     */
    public static void init() {
        try {
            _jedisPool = new JedisPool("127.0.0.1", 6379);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    /**
     * 获取 redis 实例
     * @return redis 实例
     */
    public static Jedis getJedis() {
        if (null == _jedisPool) {
            throw new RuntimeException("Redis 连接池尚未初始化");
        }
        Jedis jedis = _jedisPool.getResource();
        /**
         * 设置 redis 密码
         */
//        jedis.auth("");
        return jedis;
    }
}
