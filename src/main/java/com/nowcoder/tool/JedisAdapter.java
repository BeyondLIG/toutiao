package com.nowcoder.tool;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;

@Service
public class JedisAdapter implements InitializingBean{
    private static final Logger logger = LoggerFactory.getLogger(JedisAdapter.class);

    private Jedis jedis = null;

    private JedisPool pool = null;

    @Override
    public void afterPropertiesSet() throws Exception {
        pool = new JedisPool("localhost", 6379);  // 实例化redis线程池
    }

    private Jedis getJedis(){
        return pool.getResource();
    }  // 获取redis connection

    public void set(String key, String value){
        try{
            jedis = getJedis();
            jedis.auth("workhard");  // 密码验证
            jedis.set(key, value);
        }catch (Exception e){
            logger.error("redis异常" + e.getMessage());
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
    }

    public String get(String key){
        try{
            jedis = getJedis();
            jedis.auth("workhard");
            return jedis.get(key);
        }catch (Exception e){
            logger.error("redis异常" + e.getMessage());
            return null;
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
    }

    public long sadd(String key, String value){  // 集合添加成员
        try{
            jedis = getJedis();
            jedis.auth("workhard");
            return jedis.sadd(key, value);
        }catch (Exception e){
            logger.error("redis异常" + e.getMessage());
            return 0;
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
    }

    public long srem(String key, String value){  // 集合移除成员
        try{
            jedis = getJedis();
            jedis.auth("workhard");
            return jedis.srem(key, value);
        }catch (Exception e){
            logger.error("redis异常" + e.getMessage());
            return 0;
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
    }

    public boolean sismember(String key, String value){  // 判断value是否为集合成员
        try {
            jedis = getJedis();
            jedis.auth("workhard");
            return jedis.sismember(key, value);
        }catch (Exception e){
            logger.error("redis异常" + e.getMessage());
            return false;
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
    }

    public long scard(String key){  // 统计集合成员数量
        try {
            jedis = getJedis();
            jedis.auth("workhard");
            return jedis.scard(key);
        }catch (Exception e){
            logger.error("redis异常" + e.getMessage());
            return 0;
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
    }

    public long lpush(String key, String value){ // 列表添加成员
        try{
            jedis = pool.getResource();
            jedis.auth("workhard");
            return jedis.lpush(key, value);
        }catch (Exception e){
            logger.error("发生异常" + e.getMessage());
            return 0;
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
    }

    public List<String> brpop(int timeout, String key) {  // 移出并获取列表的最后一个元素，如果列表没有元素会阻塞列表知道等待超时或发现可弹出元素为止
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.brpop(timeout, key);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public void setObject(String key, Object obj){
        set(key, JSON.toJSONString(obj));
    }  // 序列化对象

    public <T> T getObject(String key, Class<T> clazz){  // 反序列化对象
        String value = get(key);
        if (value != null){
            return JSON.parseObject(value, clazz);
        }
        return null;
    }


    public static void print(int index, Object object){
        System.out.println(String.format("%d,%s", index, object.toString()));
    }


//    public static void main(String[] args){
//        Jedis jedis = new Jedis();
//        jedis.auth("workhard");
//        jedis.flushAll();
//
//        //get, set
//        jedis.set("hello", "world");
//        print(1, jedis.get("hello"));
//        jedis.rename("hello", "newhello");
//        print(2, jedis.get("newhello"));
//        jedis.setex("hello2", 10, "world");
//
//        //数值操作
//        jedis.set("pv", "100");
//        jedis.incr("pv");  // 增值
//        print(3, jedis.get("pv"));
//        jedis.decrBy("pv", 5); // 减值
//        print(4, jedis.get("pv"));
//
//        //列表操作
//        String listname = "list1";
//        for (int i=0; i<10; ++i){
//            jedis.lpush(listname, "a" + String.valueOf(i));
//        }
//        print(5, jedis.lrange(listname,0, 10)); // 获取前10个值
//        print(6, jedis.llen(listname));  // 获取列表元素数量
//        print(7, jedis.lpop(listname));  // 左移出
//        print(8, jedis.llen(listname));
//        print(9, jedis.lindex(listname, 3));  // 特定位置的索引
//        print(10, jedis.linsert(listname, BinaryClient.LIST_POSITION.AFTER, "a4", "xx"));
//        print(11, jedis.linsert(listname, BinaryClient.LIST_POSITION.BEFORE, "a4", "bb"));
//        print(12, jedis.lrange(listname,0, 12));
//
//        //hash, 可变字段
//        String userKey = "userxx";
//        jedis.hset(userKey, "name", "Jim");  // 设置key，value
//        jedis.hset(userKey, "age", "12");
//        jedis.hset(userKey, "phone", "13800138000");
//        print(13, jedis.hget(userKey, "name"));  // 获取key对应的value
//        print(14, jedis.hgetAll(userKey));  // 获取所有key，value
//        jedis.hdel(userKey, "phone");  // 删除key对应的value
//        print(15, jedis.hgetAll(userKey));
//        print(16, jedis.hexists(userKey, "email"));  // 判断key是否存在
//        print(17, jedis.hexists(userKey, "age"));
//        print(18, jedis.hkeys(userKey));  // 获取所有的key
//        print(19, jedis.hvals(userKey));  // 获取所有的value
//        print(20, jedis.hsetnx(userKey, "school", "whut"));  // 如果key存在，操作无效，否则设置key和value
//        print(21, jedis.hsetnx(userKey, "name", "jack"));
//        print(22, jedis.hgetAll(userKey));
//
//        //集合，点赞用户群，共同好友
//        String likeKey1 = "newsLike1";
//        String likeKey2 = "newsLike2";
//        for (int i = 0; i < 10; ++i){
//            jedis.sadd(likeKey1, String.valueOf(i));  // 设置集合值
//            jedis.sadd(likeKey2, String.valueOf(i*2));
//        }
//        print(23, jedis.smembers(likeKey1));  // 获取集合的所有值
//        print(24, jedis.smembers(likeKey2));
//        print(25, jedis.sunion(likeKey1, likeKey2));  // 集合的并集
//        print(26, jedis.sdiff(likeKey1, likeKey2));  // 获取集合的不同值
//        print(27, jedis.sinter(likeKey1, likeKey2));  // 获取集合的交集
//        print(28, jedis.sismember(likeKey1, "12"));  // 判断集合是否存在某个值
//        print(29, jedis.sismember(likeKey2, "12"));
//        jedis.srem(likeKey1, "5");  // 移除集合中的某个值
//        print(30, jedis.smembers(likeKey1));
//        jedis.smove(likeKey2, likeKey1, "14");  // 将指定值从source集合移动到destination集合
//        print(31, jedis.smembers(likeKey1));
//        print(32, jedis.scard(likeKey1));  // 获取集合中元素的个数
//
//        //排序集合，有限队列， 排行榜
//        String rankKey = "rankKey";
//        jedis.zadd(rankKey, 60, "Jim");  // 有序集合添加元素
//        jedis.zadd(rankKey, 80, "Jack");
//        jedis.zadd(rankKey, 90, "Alice");
//        jedis.zadd(rankKey, 75, "Lucy");
//        jedis.zadd(rankKey, 15, "Mei");
//        print(33, jedis.zcard(rankKey));  // 获取有序集合的元素数量
//        print(34, jedis.zcount(rankKey, 61, 100));  // 获取有序集合指定区间分数的元素数量
//        print(35, jedis.zscore(rankKey, "Jack"));  // 获取元素的分数
//        jedis.zincrby(rankKey, 2, "Lucy");  // 给元素增加分数
//        print(36, jedis.zscore(rankKey, "Lucy"));
//        jedis.zincrby(rankKey, 2, "Luc");
//        print(37, jedis.zscore(rankKey, "Luc"));
//        print(38, jedis.zrange(rankKey,0, 10));  // 获取集合分数前10的元素
//        print(39, jedis.zrange(rankKey, 1, 3));
//        print(40, jedis.zrevrange(rankKey, 1, 3));  // 获取集合分数在第2到第4的元素并按照番薯递减排序
//        for (Tuple tuple : jedis.zrangeByScoreWithScores(rankKey, 60, 100)){  // 获取指定分数区间的元素列表
//            print(41, tuple.getElement() + ":" +tuple.getScore());
//        }
//        print(42, jedis.zrank(rankKey, "Jack"));  // 获取元素的排名（递增）
//        print(43, jedis.zrevrank(rankKey, "Jack"));  // 获取元素的排名（递减）
//
//        //连接池
//        JedisPool pool = new JedisPool();
//        for (int i = 0; i < 100; i++){
//            Jedis j = pool.getResource();
//            j.get("a");
//            j.close();
//        }
//
//
//    }
}
