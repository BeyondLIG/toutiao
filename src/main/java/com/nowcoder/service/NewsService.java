package com.nowcoder.service;

import com.nowcoder.dao.NewsDAO;
import com.nowcoder.model.News;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.nowcoder.tool.ToutiaoUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class NewsService {
    @Autowired
    private NewsDAO newsDAO;

    public List<News> getLatestNews(int userId, int offset, int limit){
        return newsDAO.selectByUserIdAndOffSet(userId, offset, limit);
    }

    public int addNews(News news){
        newsDAO.addNews(news);
        return news.getId();
    }

    public void updateCommentCount(int commentCount,int id){
        newsDAO.updateCommentCountById(commentCount, id);
    }

    public News getById(int id){
        return newsDAO.selectById(id);
    }

    public void updateLikeCount(int likeCount, int id){
        newsDAO.updateLikeCount(likeCount, id);
    }

    public String saveImage(MultipartFile file) throws IOException{
        int dotPos = file.getOriginalFilename().lastIndexOf(".");
        if(dotPos < 0){
            return null;
        }

        String fileExt = file.getOriginalFilename().substring(dotPos + 1).toLowerCase();
        if(!ToutiaoUtil.isFileAllowed(fileExt)){
            return null;
        }

        String fileName = UUID.randomUUID().toString().replaceAll("-", "") + "." + fileExt;
        Files.copy(file.getInputStream(), new File(ToutiaoUtil.IMAGE_DIR + fileName ).toPath(),
                StandardCopyOption.REPLACE_EXISTING);
        return ToutiaoUtil.TOUTIAO_DOMAIN + "image?name=" + fileName;

    }
}
