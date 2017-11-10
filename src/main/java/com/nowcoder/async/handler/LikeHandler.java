package com.nowcoder.async.handler;

import com.nowcoder.async.EventHandler;
import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventType;
import com.nowcoder.model.Message;
import com.nowcoder.model.User;
import com.nowcoder.service.MessageService;
import com.nowcoder.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class LikeHandler implements EventHandler{ // 点赞事件处理者
    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Override
    public void doHandle(EventModel model) {  // 处理事件的具体方法
        Message message = new Message();
        User user = userService.getUser(model.getActorId());
        message.setFromId(user.getId());
        message.setToId(model.getEntityOwnerId());
        message.setContent("用户" + user.getName() +
                " 赞了你的资讯,http://127.0.0.1:8080/news/" +
        String.valueOf(model.getEntityId()));
        message.setCreatedDate(new Date());
        messageService.addMessage(message);

        System.out.println("LIKED");
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LIKE);
    } // 定义该事件处理者所能处理的事件类型
}
