package com.nowcoder.service;

import com.nowcoder.tool.JedisAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.nowcoder.tool.RedisKeyUtil;

@Service
public class LikeService {
    @Autowired
    private JedisAdapter jedisAdapter;

    public int getLikeStatus(int userId, int entityId, int entityType){
        String likeKey = RedisKeyUtil.getLikeKey(entityId, entityType);
        if (jedisAdapter.sismember(likeKey, String.valueOf(userId))){
            return 1;
        }

        String diskLikeKey = RedisKeyUtil.getDisLikeKey(entityId, entityType);
        return jedisAdapter.sismember(diskLikeKey, String.valueOf(userId)) ? -1 : 0;
    }

    public long like(int userId, int entityId, int entityType){
        String likeKey = RedisKeyUtil.getLikeKey(entityId, entityType);
        jedisAdapter.sadd(likeKey, String.valueOf(userId));

        String diskLikeKey = RedisKeyUtil.getDisLikeKey(entityId, entityType);
        jedisAdapter.srem(diskLikeKey, String.valueOf(userId));

        return jedisAdapter.scard(likeKey);
    }

    public long disLike(int userId, int entityId, int entityType){
        //在不喜欢集合里添加
        String diskLikeKey = RedisKeyUtil.getDisLikeKey(entityId, entityType);
        jedisAdapter.sadd(diskLikeKey, String.valueOf(userId));

        //在喜欢集合里删除
        String likeKey = RedisKeyUtil.getLikeKey(entityId, entityType);
        jedisAdapter.srem(likeKey, String.valueOf(userId));

        //返回喜欢人数
        return jedisAdapter.scard(likeKey);
    }


}
