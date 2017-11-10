package com.nowcoder.async;

import com.alibaba.fastjson.JSON;
import com.nowcoder.tool.JedisAdapter;
import com.nowcoder.tool.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventProducer {// 事件产生器
    private static  final Logger logger = LoggerFactory.getLogger(EventProducer.class);
    @Autowired
    private JedisAdapter jedisAdapter;

    public boolean fireEvent(EventModel eventModel){ // 异步发送事件
        try {
            String json = JSON.toJSONString(eventModel); // 序列化eventModel
            String key = RedisKeyUtil.getEventQueueKey();
            jedisAdapter.lpush(key, json); // 添加eventModel到信息队列中
            return true;
        }catch (Exception e){
            logger.error("json序列化出错" + e.getMessage());
            return false;
        }
    }
}
