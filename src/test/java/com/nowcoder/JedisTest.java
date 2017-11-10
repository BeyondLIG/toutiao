package com.nowcoder;

import com.nowcoder.model.User;
import com.nowcoder.tool.JedisAdapter;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.xml.parsers.SAXParser;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ToutiaoApplication.class)
public class JedisTest {
    @Autowired
    private JedisAdapter jedisAdapter;

    @Test
    public void testObject(){
        User user =new User();
        user.setHeadUrl("http://images.nowcoder.com/head/100t.png");
        user.setName("user1");
        user.setPassword("abc");
        user.setSalt("def");
        jedisAdapter.setObject("user1", user);

        User u = jedisAdapter.getObject("user1", User.class);
        System.out.println(ToStringBuilder.reflectionToString(u));
    }
}
