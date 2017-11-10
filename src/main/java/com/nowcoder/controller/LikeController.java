package com.nowcoder.controller;

import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventProducer;
import com.nowcoder.async.EventType;
import com.nowcoder.model.EntityType;
import com.nowcoder.model.HostHolder;
import com.nowcoder.model.News;
import com.nowcoder.service.LikeService;
import com.nowcoder.service.NewsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.nowcoder.tool.JedisAdapter;
import com.nowcoder.tool.ToutiaoUtil;

@Controller
public class LikeController {
    private static final Logger logger = LoggerFactory.getLogger(JedisAdapter.class);

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private NewsService newsService;

    @Autowired
    private EventProducer eventProducer;

    @RequestMapping(path = {"/like"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String like(@RequestParam("newsId") int newsId){
        try {
            long likeCount = likeService.like(hostHolder.getUser().getId(), newsId, EntityType.ENTITY_NEWS);
//            long likeCount = likeService.like(10, newsId, EntityType.ENTITY_NEWS);
            newsService.updateLikeCount((int) likeCount, newsId);
            News news = newsService.getById(newsId);
            EventModel eventModel = new EventModel(EventType.LIKE);
            eventModel.setActorId(hostHolder.getUser().getId());
//            eventModel.setActorId(10);
            eventModel.setEntityId(newsId);
            eventModel.setEntityType(EntityType.ENTITY_NEWS);
            eventModel.setEntityOwnerId(news.getUserId());
            eventProducer.fireEvent(eventModel);
            return ToutiaoUtil.getJSONString(0, String.valueOf(likeCount));
        }catch (Exception e){
            logger.error("点赞发生异常" + e.getMessage());
            return ToutiaoUtil.getJSONString(1, "点赞发生异常");
        }
    }

    @RequestMapping(path = {"/dislike"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String dislike(@RequestParam("newsId") int newsId){
        try {
            long disLikeCount = likeService.disLike(hostHolder.getUser().getId(), newsId, EntityType.ENTITY_NEWS);
            newsService.updateLikeCount((int) disLikeCount, newsId);
            return ToutiaoUtil.getJSONString(0);
        }catch (Exception e){
            logger.error("踩发生异常" + e.getMessage());
            return ToutiaoUtil.getJSONString(1, "踩发生异常");
        }
    }
}
