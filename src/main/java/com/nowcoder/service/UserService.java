package com.nowcoder.service;

import com.nowcoder.dao.LoginTicketDAO;
import com.nowcoder.dao.UserDAO;
import com.nowcoder.model.LoginTicket;
import com.nowcoder.model.User;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.nowcoder.tool.ToutiaoUtil;

import java.util.*;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private LoginTicketDAO loginTicketDAO;

    public User getUser(int id) {
        return userDAO.selectById(id);
    }

    private String addLoginTicket(int userId){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setStatus(0);
        loginTicket.setUserId(userId);
        Date date = new Date();
        date.setTime(date.getTime() + 1000*36000*24);
        loginTicket.setExpired(date);
        loginTicket.setTicket(UUID.randomUUID().toString().replace("-", ""));  // 生成唯一的ticket
        loginTicketDAO.addLoginTicket(loginTicket);  // 保存ticket到数据库
        return loginTicket.getTicket();
    }


    public User getUserByName(String username){
        return userDAO.selectByName(username);
    }

    public Map<String, Object> register(String username, String password){
        Map<String, Object> map = new HashMap<String, Object>();

        if (StringUtils.isBlank(username)){
            map.put("msgname", "用户名不能为空");
            return map;
        }

        if (StringUtils.isBlank(password)){
            map.put("msgpassword", "密码不能为空");
            return map;
        }

        User user = userDAO.selectByName(username);

        if (user != null){
            map.put("msgname", "用户名已被注册");
            return map;
        }

        user = new User();
        user.setName(username);
        String head = String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000));
        user.setHeadUrl(head);
        user.setSalt(UUID.randomUUID().toString().substring(0, 5));
        user.setPassword(ToutiaoUtil.MD5(password + user.getSalt()));
        userDAO.addUser(user);

        String ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket);

        return map;

    }


    public Map<String, Object> login(String username, String password){
        Map<String, Object> map = new HashMap<String, Object>();
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)){
            map.put("magname", "用户名或密码不能为空");
            return map;
        }

        User user = userDAO.selectByName(username);

        if (user == null){
            map.put("magname", "用户名不存在");
            return map;
        }

        if (!ToutiaoUtil.MD5(password+user.getSalt()).equals(user.getPassword())) {
            map.put("msgpwd", "密码不正确");
            return map;
        }
        map.put("userId", user.getId());
        String ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket);
        return map;
    }

    public void logout(String tivket){
        loginTicketDAO.updateStatus(1, tivket);
    }

}
