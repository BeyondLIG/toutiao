package com.nowcoder.dao;

import com.nowcoder.model.News;
import org.apache.ibatis.annotations.*;


import java.util.List;

@Mapper
public interface NewsDAO {
    String TABLE_NAME = "news";
    String INSET_FIELDS = " title, link, image, like_count, comment_count, created_date, user_id ";
    String SELECT_FIELDS = " id, " + INSET_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSET_FIELDS, ") values (#{title},#{link},#{image},#{likeCount},#{commentCount},#{createdDate},#{userId})" })
    int addNews(News news);

    @Select({"select", SELECT_FIELDS ,"from ", TABLE_NAME, " where id = #{id}"})
    News selectById(int id);

    @Update({"update ", TABLE_NAME, "set comment_count = #{commentCount} where id = #{id}"})
    void updateCommentCountById(@Param("commentCount")int commentCount, @Param("id") int id);

    @Update({"update ", TABLE_NAME, " set like_count = #{likeCount} where id = #{id}"})
    void updateLikeCount(@Param("likeCount") int likeCount, @Param("id") int id);

    List<News> selectByUserIdAndOffSet(@Param("userId") int userId, @Param("offset") int offset, @Param("limit") int limit);
}
