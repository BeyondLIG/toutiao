package com.nowcoder.async;

import java.util.List;

public interface EventHandler { // 事件处理接口
    public void doHandle(EventModel model); // 处理事件的方法
    public List<EventType> getSupportEventTypes(); // 获取该事件处理者所能处理的所有事件类型
}
