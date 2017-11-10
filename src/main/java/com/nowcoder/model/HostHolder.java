package com.nowcoder.model;

import org.springframework.stereotype.Component;

@Component
public class HostHolder { // 保存当前请求的用户的对象
    private static ThreadLocal<User> users = new ThreadLocal<User>();  // 保存当前线程的用户对象

    public User getUser(){
        return users.get();
    }  // 获取当前线程的用户

    public void setUser(User user){
        users.set(user);
    }  // 设置当前线程的用户

    public void clear(){
        users.remove();  // 清楚当前线程的用户
    }
}
