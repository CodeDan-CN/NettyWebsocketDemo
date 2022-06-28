package cn.wtu.zld.utils;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * Redis客户端工具类
 * @author CodeDan
 * @time 2022年04月11日
 * **/
public class RedisClient {
    //单例客户端对象
    private static JedisPool jedisPool;

    static {
        //同各国ResourceBundle类来获取resources文件下的指定配置文件中内容
        ResourceBundle resourceBundle = ResourceBundle.getBundle("redisConfig");
        //获取redis连接配置
        int maxTotal = Integer.parseInt(resourceBundle.getString("redis.maxTotal"));
        long maxWaitMillis = Long.parseLong(resourceBundle.getString("redis.maxWatiMillis"));
        int minIdle = Integer.parseInt(resourceBundle.getString("redis.minIdle"));
        int maxIdle = Integer.parseInt(resourceBundle.getString("redis.maxIdle"));
        boolean testOnReturn = Boolean.parseBoolean(resourceBundle.getString("redis.testOnReturn"));
        boolean testOnBorrow = Boolean.parseBoolean(resourceBundle.getString("redis.testOnBorrow"));
        String password = resourceBundle.getString("redis.password");
        String ip = resourceBundle.getString("redis.server.ip");
        int port = Integer.parseInt(resourceBundle.getString("redis.server.port"));
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(maxTotal);
        jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMinIdle(minIdle);
        jedisPoolConfig.setTestOnBorrow(testOnBorrow);
        jedisPoolConfig.setTestOnReturn(testOnReturn);
        jedisPool = new JedisPool(jedisPoolConfig,ip,port,10000,password);

         }

    public static JedisPool getJedisPool(){
        return jedisPool;
    }

}
