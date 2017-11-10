package com.nowcoder.controller;

import com.nowcoder.model.HostHolder;
import com.nowcoder.model.Message;
import com.nowcoder.model.User;
import com.nowcoder.model.ViewObject;
import com.nowcoder.service.MessageService;
import com.nowcoder.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.nowcoder.tool.ToutiaoUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);
    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @RequestMapping(path = {"/mag/addMessage"}, method = {RequestMethod.POST})
    @ResponseBody
    public String addMessage(@RequestParam("fromId") int fromId,
                             @RequestParam("toId") int toId,
                             @RequestParam("content") String content){
        try {
            Message message= new Message();
            message.setContent(content);
            message.setCreatedDate(new Date());
            message.setFromId(fromId);
            message.setToId(toId);
            message.setHasRead(0);
            message.setConversationId(fromId < toId? String.format("%d_%d", fromId, toId):
            String.format("%d_%d", toId, fromId));
            messageService.addMessage(message);
            return ToutiaoUtil.getJSONString(0, "添加站内信成功");
        }catch (Exception e){
            logger.error("添加站内信失败" + e.getMessage());
            return ToutiaoUtil.getJSONString(1, "添加站内信失败");
        }
    }


    @RequestMapping(path = {"/mag/detail"}, method = {RequestMethod.GET})
    public String conversationDetail(@RequestParam("conversationId") String conversationId,
                                     Model model){
        try {
            List<ViewObject> messages = new ArrayList<>();
            List<Message> conversationList = messageService.getConversationDetail(conversationId, 0, 10);
            for (Message msg : conversationList) {
                ViewObject vo = new ViewObject();
                vo.set("message", msg);
                User user = userService.getUser(msg.getFromId());
                if (user == null) {
                    continue;
                }
                vo.set("headUrl", user.getHeadUrl());
                vo.set("userName", user.getName());
                messages.add(vo);
            }
            model.addAttribute("messages", messages);
        }catch (Exception e){
            logger.error("获取站内信列表失败" + e.getMessage());
        }
        return "letterDetail";

    }

    @RequestMapping(path = {"/mag/list"}, method = {RequestMethod.GET})
    public String conversationList(Model model){
        try {
            int localUserId = hostHolder.getUser().getId();
            List<Message> conversationList = messageService.getConversationList(localUserId, 0 , 10);
            List<ViewObject> vos = new ArrayList<ViewObject>();
            for (Message msg : conversationList){
                ViewObject vo = new ViewObject();
                vo.set("conversation", msg);
                int targetId = msg.getFromId() == localUserId ? msg.getToId() : msg.getFromId();
                User user = userService.getUser(targetId);
                vo.set("headUrl", user.getHeadUrl());
                vo.set("userName", user.getName());
                vo.set("targetId", targetId);
                vo.set("unreadCount", messageService.getUnreadCount(localUserId, msg.getConversationId()));
                vos.add(vo);
            }
        }catch (Exception e){
            logger.error("获取站内信列表失败" + e.getMessage());
        }
        return "letter";
    }
}
