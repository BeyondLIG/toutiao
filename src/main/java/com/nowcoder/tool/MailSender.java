package com.nowcoder.tool;

import com.nowcoder.controller.LoginController;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.util.Map;
import java.util.Properties;

@Service
public class MailSender implements InitializingBean{
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    private JavaMailSenderImpl javaMailSender;

    @Autowired
    private VelocityEngine velocityEngine;

    @Override
    public void afterPropertiesSet() throws Exception {
        javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setUsername("1192126986@qq.com");
        javaMailSender.setPassword("LI13818");
        javaMailSender.setHost("smtp.qq.com");
        javaMailSender.setPort(465);
        javaMailSender.setProtocol("smtps");
        javaMailSender.setDefaultEncoding("utf8");
        Properties javaMailProperties = new Properties();
        javaMailProperties.put("mail.smtp.ssl.enable", true);
        javaMailSender.setJavaMailProperties(javaMailProperties);
    }

    public Boolean sendWithHTMLTemplate(String to, String subject,
                                        String template, Map<String, Object> model){
        try {
            String nick = MimeUtility.encodeText("牛客中级课");
            InternetAddress from = new InternetAddress(nick + "<1192126986@qq.com>");
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
            String result = VelocityEngineUtils
                    .mergeTemplateIntoString(velocityEngine, template, "UTF-8", model);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setFrom(from);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(result, true);
            javaMailSender.send(mimeMessage);
            return true;
        }catch (Exception e){
            logger.error("发送邮件失败" + e.getMessage());
            return false;
        }
    }
}
