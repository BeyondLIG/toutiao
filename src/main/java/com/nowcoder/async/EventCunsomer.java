package com.nowcoder.async;

import com.alibaba.fastjson.JSON;
import com.nowcoder.tool.JedisAdapter;
import com.nowcoder.tool.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EventCunsomer implements InitializingBean, ApplicationContextAware{ // 事件处理， 继承InitializingBean，可以对bean初始化操作
    private static final Logger logger = LoggerFactory.getLogger(EventCunsomer.class);

    private Map<EventType, List<EventHandler>> config = new HashMap<EventType, List<EventHandler>>();  // 记录每个事件类型的处理者列表

    private ApplicationContext applicationContext;

    @Autowired
    private JedisAdapter jedisAdapter;

    @Override
    public void afterPropertiesSet() throws Exception { // 获取所有事件类型的处理者列表
        Map<String, EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);  // 获取应用程序的所有的事件处理者
        if(beans != null){
            for (Map.Entry<String, EventHandler> entry : beans.entrySet()){ // 遍历所有事件处理者
                List<EventType> eventTypes = entry.getValue().getSupportEventTypes(); // 获取每一个事件处理者所能处理的事件类型
                for (EventType type : eventTypes){
                    if (!config.containsKey(type)){
                        config.put(type, new ArrayList<EventHandler>());  // 添加事件类型
                    }
                    // 注册每类事件的处理者
                    config.get(type).add(entry.getValue());
                }
            }
        }

        // 启动线程去消费事件
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){// 死循环，一直在跑
                    String key = RedisKeyUtil.getEventQueueKey();
                    List<String> messages = jedisAdapter.brpop(0, key); // 阻塞队列
                    for (String message : messages){
                        if (message.equals(key)){
                            continue;
                        }
                        EventModel eventModel = JSON.parseObject(message, EventModel.class); // 反序列化对象
                        if (!config.containsKey(eventModel.getEventType())){
                            logger.error("不能识别的事件");
                            continue;
                        }

                        for (EventHandler handler : config.get(eventModel.getEventType())){// 获取每个事件类型的所有处理者
                            handler.doHandle(eventModel); // 开始处理
                        }

                    }
                }
            }
        });

        thread.start();

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
