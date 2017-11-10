package com.nowcoder.controller;

import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventProducer;
import com.nowcoder.async.EventType;
import com.nowcoder.service.UserService;
import com.nowcoder.tool.ToutiaoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private EventProducer eventProducer;

    @RequestMapping(path = {"/reg/"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String register(@RequestParam("username")String username,
                           @RequestParam("password")String password,
                           @RequestParam(value = "rember", defaultValue = "0")int rememberme,
                           HttpServletResponse response){
        Map<String, Object> map = new HashMap<String, Object>();

        try {
            map = userService.register(username, password);
            if (map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
                cookie.setPath("/");
                if(rememberme > 0){
                    cookie.setMaxAge(3600*24*5);
                }
                response.addCookie(cookie);
                return ToutiaoUtil.getJSONString(0, "注册成功");
            }else {
                return ToutiaoUtil.getJSONString(1, map);
            }
        }catch (Exception e){
            logger.error("注册异常" + e.getMessage());
            return ToutiaoUtil.getJSONString(1, "注册异常");
        }
    }


    @RequestMapping(path = {"/login/"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String login(@RequestParam("username")String username,
                        @RequestParam("password") String password,
                        @RequestParam(value = "rember", defaultValue = "0") int rememberme,
                        HttpServletResponse response){
        try {
            Map<String, Object> map = userService.login(username, password);

            if (map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", map.get("ticket").toString()); // 实例化cookie对象并添加cookie值
                cookie.setPath("/"); // 设置cookie的作用路径
                if (rememberme > 0){
                    cookie.setMaxAge(3600*24*5);  // 设置cookie的过期时间
                }
                response.addCookie(cookie);  // 向浏览器添加cookie
                EventModel eventModel = new EventModel(EventType.LOGIN);
                eventModel.setActorId((int) map.get("userId"));
                eventModel.setExt("username", username);
                eventModel.setExt("email", "zjuyxy@qq.com");
                eventProducer.fireEvent(eventModel);
                return ToutiaoUtil.getJSONString(0, "登陆成功");
            } else {
                return ToutiaoUtil.getJSONString(1, map);
            }
        }catch (Exception e){
            logger.error("登陆异常" + e.getMessage());
            return ToutiaoUtil.getJSONString(1, "登录异常");
        }

    }

    @RequestMapping(path = {"/logout/"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String logout(@CookieValue("ticket")String ticket){
        userService.logout(ticket);
        return "redirect:/";
    }



}
