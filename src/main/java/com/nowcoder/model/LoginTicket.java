package com.nowcoder.model;

import java.util.Date;

public class LoginTicket { // 登录ticket
    private int id;

    private int userId;  // 用户id

    private String ticket;  // cookie保存的ticket值，用于记录每个用户身份

    private Date expired;  // ticket过期的时间

    private int status; // ticket的状态

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public Date getExpired() {
        return expired;
    }

    public void setExpired(Date expired) {
        this.expired = expired;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }



}
