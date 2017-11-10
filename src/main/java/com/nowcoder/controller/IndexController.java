package com.nowcoder.controller;

import com.nowcoder.model.User;
import com.nowcoder.service.ToutiaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

//@Controller
public class IndexController {
    @Autowired
    private ToutiaoService toutiaoService;


    @RequestMapping(value = {"/", "/index"})
    @ResponseBody
    public String index(HttpSession session){
        return "hello nowcoder, " + session.getAttribute("msg") + "<br> Say:" +
                toutiaoService.say();
    }


    @RequestMapping(path = {"/profile/{groudId}/{userId}"})
    @ResponseBody
    public String profile(@PathVariable("groudId")Integer groudId,
                          @PathVariable("userId")Integer userId,
                          @RequestParam(value = "type", defaultValue = "type")String type,
                          @RequestParam(value = "key", defaultValue = "key")String key){
        return String.format("GID{%d} UID{%d} TYPE{%s} KEY{%s}", groudId, userId, type, key);
    }


    @RequestMapping(path = {"/vm"})
    public String news(Model model){
        model.addAttribute("value1", "vv1");
        List<String> colors = Arrays.asList(new String[]{"RED", "GREEN", "BLUE"});

        Map<String, String> map = new HashMap<String, String>();
        for (int i =0; i < 4; i++){
            map.put(String.valueOf(i), String.valueOf(i * i));
        }

        model.addAttribute("colors", colors);
        model.addAttribute("map", map);
        model.addAttribute("user", new User("nowcoder"));

        return "news";
    }


    @RequestMapping(path = {"/request"})
    @ResponseBody
    public String request(HttpServletRequest request,
                          HttpServletResponse response,
                          HttpSession session){
        StringBuilder sb = new StringBuilder();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()){
            String name = headerNames.nextElement();
            sb.append(name + ":" + request.getHeader(name) + "<br>");
        }

        for (Cookie cookie : request.getCookies()){
            sb.append("Cookie:");
            sb.append(cookie.getName());
            sb.append(":");
            sb.append(cookie.getValue());
            sb.append("<br");
        }

        sb.append("method:" + request.getMethod() + "<br>");
        sb.append("getPathInfo:" + request.getPathInfo() + "<br>");
        sb.append("getQueryString:" + request.getQueryString() + "<br>");
        sb.append("getRequestURI:" + request.getRequestURI() + "<br>");
        sb.append("getRequestURL:" + request.getRequestURL() + "<br>");

        return sb.toString();
    }

    @RequestMapping(value = {"/response"})
    @ResponseBody
    public String response(@CookieValue(value = "nowcoder", defaultValue = "a")String nowcoder,
                           @RequestParam(value = "key", defaultValue = "hello")String key,
                           @RequestParam(value = "value", defaultValue = "world" )String value,
                           HttpServletResponse response){
        response.addCookie(new Cookie(key, value));
        response.addHeader(key, value);
        return "nowcoder from cookie" + nowcoder;
    }


    @RequestMapping(path = {"/redirect"})
    public String redirect(HttpSession session){
        session.setAttribute("mag", "Jump from redirect");
        return "redirect:/";
    }

    @RequestMapping(path = {"/redirectview/{code}"})
    public RedirectView redirectView(@PathVariable("code")Integer code){
        RedirectView rv = new RedirectView("/", true);
        if (code == 301){
            rv.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        }
        return rv;
    }


    @RequestMapping(path = {"/admin"})
    @ResponseBody
    public String admin(@RequestParam(value = "key", required = false)String key){
        if ("admin".equals(key)){
            return "hello admin";
        }
        throw new IllegalArgumentException("KEY 错误");
    }

    @ExceptionHandler()
    @ResponseBody
    public String error(Exception e){
        return "error: " + e.getMessage();
    }
}
