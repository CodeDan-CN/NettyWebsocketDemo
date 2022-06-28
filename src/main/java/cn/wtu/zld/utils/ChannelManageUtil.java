package cn.wtu.zld.utils;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 用作通信通道管理工具类
 * @author CodeDan
 * @time 2022年05月10日
 * **/
public class ChannelManageUtil {

    private static Map<String, ChannelHandlerContext> map = new HashMap<>();

    /**
     * 用于存储以及向Redis上传用户标识和ChannelId
     * @param key 用户标识
     * @param channelHandlerContext 用户通信通道
     * @return void
     * */
    public static void add(String key, ChannelHandlerContext channelHandlerContext){
        //将用户标识和用户对应通道放入Map中进行存储
        String id = channelHandlerContext.channel().id().asLongText();
        map.put(id,channelHandlerContext);
        System.out.println(key+"----"+map);
        //向redis中插入存储的用户标识---channelId的键值对
        Jedis jedisClient = RedisClient.getJedisPool().getResource();
        jedisClient.hset("communication:user:channel",key,id);
        jedisClient.close();
    }

    /**
     * 用于删除以及向Redis删除用户标识和ChannelId
     * @param id 通道标识
     * @return void
     * */
    public static void delete(String id){
        map.remove(id);
        Jedis jedisClient = RedisClient.getJedisPool().getResource();
        String resultKey = null;
        Set<Map.Entry<String, String>> entries = jedisClient.hgetAll("communication:user:channel").entrySet();
        for(Map.Entry<String, String> list : entries){
            String key = list.getKey();
            String value = list.getValue();
            if( value.equals(id) ){
                resultKey = key;
            }
        }
        if( resultKey != null ){
            jedisClient.hdel("communication:user:channel", resultKey );
        }
        jedisClient.close();
    }

    /**
     * 用于检测存储用户标识和Channel的Map中是否存在对应的用户标识和其映射
     * @param key 用户标识
     * @return void
     * */
    public static boolean containsKey(String key){
        Jedis jedisClient = RedisClient.getJedisPool().getResource();
        String result = jedisClient.hget("communication:user:channel", key);
        jedisClient.close();
        if( result != null ){
            if(map.containsKey(result)){
                return true;
            }else{
                return false;
            }
        }
        return false;
    }

    /**
     * 用于检测存储用户标识和Channel的Map中是否存在对应的用户标识和其映射
     * @param key 用户标识
     * @return void
     * */
    public static ChannelHandlerContext get(String key){
        Jedis jedisClient = RedisClient.getJedisPool().getResource();
        String result = jedisClient.hget("communication:user:channel", key);
        jedisClient.close();
        if( result != null ){
            if(map.containsKey(result)){
                return map.get(result);
            }
        }
        return null;
    }

    /**
     * 用于创建通道心跳初始以及重置次数
     * @param userAccount 用户标识
     * @return void
     * */
    public static void initPingNumber(String userAccount){
        Jedis jedisClient = RedisClient.getJedisPool().getResource();
        jedisClient.hset("communication:user:ping",userAccount,"0");
        jedisClient.close();
    }

    /**
     * 用于获取心跳无应答次数
     * @param userAccount 用户标识
     * @return int
     * */
    public static int getPingNumber(String userAccount){
        Jedis jedisClient = RedisClient.getJedisPool().getResource();
        int result = Integer.parseInt(jedisClient.hget("communication:user:ping",userAccount));
        jedisClient.close();
        return result;
    }

    /**
     * 用于增加一次心跳无应答检测次数
     * @param userAccount 用户标识
     * @return void
     * */
    public static void addPingNumber(String userAccount){
        Jedis jedisClient = RedisClient.getJedisPool().getResource();
        jedisClient.hincrBy("communication:user:ping",userAccount,1);
        jedisClient.close();
    }

    /**
     * 用于删除心跳应答检测次数
     * @param userAccount 用户标识
     * @return void
     * */
    public static void deletePingNumber(String userAccount){
        Jedis jedisClient = RedisClient.getJedisPool().getResource();
        jedisClient.hdel("communication:user:ping",userAccount);
        jedisClient.close();
    }

}
