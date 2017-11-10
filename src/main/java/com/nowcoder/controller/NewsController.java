package com.nowcoder.controller;

import com.nowcoder.model.*;
import com.nowcoder.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.nowcoder.tool.ToutiaoUtil;

import javax.jws.Oneway;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class NewsController {
    private static final Logger logger = LoggerFactory.getLogger(NewsController.class);

    @Autowired
    private NewsService newsService;

    @Autowired
    private UserService userService;

    @Autowired
    private QiniuService qiniuService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(path = {"/uploadImage"}, method = {RequestMethod.POST})
    @ResponseBody
    public String uploadImage(@RequestParam("file") MultipartFile imageFile){
        try {
//            String fileUrl = newsService.saveImage(imageFile);
            String fileUrl = qiniuService.saveImage(imageFile);
            if(fileUrl == null){
                return ToutiaoUtil.getJSONString(1, "上传图片失败");
            }
            return ToutiaoUtil.getJSONString(0, fileUrl);
        }catch (Exception e){
            logger.error("上传图片失败" + e.getMessage());
            return ToutiaoUtil.getJSONString(1, "上传失败");
        }
    }

    @RequestMapping(path = {"/image"}, method = RequestMethod.GET)
    @ResponseBody
    public void getImage(@RequestParam("name") String imageName,
                           HttpServletResponse response){
        response.setContentType("image/jpeg");
        try {
            StreamUtils.copy(new FileInputStream(new File(ToutiaoUtil.IMAGE_DIR + imageName)), response.getOutputStream());
        }catch (Exception e){
            logger.error("读取图片失败" + e.getMessage());
        }
    }


    @RequestMapping(path = {"/user/addNews/"}, method = {RequestMethod.POST})
    @ResponseBody
    public String addNews(@RequestParam("image") String image,
                          @RequestParam("title") String title,
                          @RequestParam("link") String link){
        try {
            News news = new News();
            news.setCreatedDate(new Date());
            news.setImage(image);
            news.setLink(link);
            news.setTitle(title);
            if (hostHolder.getUser() != null) {
                news.setUserId(hostHolder.getUser().getId());
            } else {
                news.setUserId(3);
            }
            newsService.addNews(news);
            return ToutiaoUtil.getJSONString(0);
        }catch (Exception e){
            logger.error("添加资讯异常" + e.getMessage());
            return ToutiaoUtil.getJSONString(1, "发布失败");
        }
    }


    @RequestMapping(path = {"/news/{newsId}"}, method = {RequestMethod.GET})
    public String newsDetail(@PathVariable("newsId") int newsId, Model model){
        try {
            News news = newsService.getById(newsId);
            if (news != null) {
                int localUserId = hostHolder.getUser().getId();
                if (localUserId != 0){
                    model.addAttribute("like", likeService.getLikeStatus(localUserId, newsId, EntityType.ENTITY_NEWS) );
                }else {
                    model.addAttribute("like", 0);
                }
                List<Comment> comments = commentService.getCommentsByEntity(newsId, EntityType.ENTITY_NEWS);
                List<ViewObject> vos = new ArrayList<ViewObject>() ;
                for (Comment comment : comments){
                    ViewObject vo = new ViewObject();
                    vo.set("comment", comment);
                    vo.set("user", userService.getUser(comment.getUserId()));
                    vos.add(vo);
                }
                model.addAttribute("comments", vos);
            }
            model.addAttribute("news", news);
            model.addAttribute("owner", userService.getUser(news.getUserId()));
        }catch (Exception e){
            logger.error("获取资讯异常" + e.getMessage());
        }
        return "detail";
    }

    @RequestMapping(path = "/addComment", method = {RequestMethod.POST})
    public String addComment(@RequestParam("newsId") int newsId,
                             @RequestParam("content") String content){
        try{
            Comment comment = new Comment();
            comment.setUserId(hostHolder.getUser().getId());
            comment.setStatus(0);
            comment.setCreateDate(new Date());
            comment.setEntityType(EntityType.ENTITY_NEWS);
            comment.setEntityId(newsId);
            comment.setContent(content);
            commentService.addComment(comment);

            int count = commentService.getCommentCount(newsId, EntityType.ENTITY_NEWS);
            newsService.updateCommentCount(count, newsId);
        }catch (Exception e){
            logger.error("添加评论失败" + e.getMessage());
        }
        return "redirect:/news/" + newsId;
    }
}
