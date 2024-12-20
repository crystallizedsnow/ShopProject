package com.loginmodule.service.impl;

import com.loginmodule.mapper.OrderMapper;
import com.loginmodule.pojo.EmailMessage;
import com.loginmodule.service.EmailService;
import com.rabbitmq.client.Channel;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    private OrderMapper orderMapper;


    @Override
    public String findEmail(String orderId) {
        return orderMapper.selectEmailByuserId(orderId);
    }

    @RabbitListener(queues = "emailQueue",ackMode = "MANUAL")
    @Override
    public boolean sendEmail(EmailMessage emailMessage, Message message, Channel channel) throws EmailException, IOException {
        try {
        String orderId = emailMessage.getOrderId();
        String shopName = emailMessage.getShopName();
        String emailUrl= findEmail(orderId);
        HtmlEmail email = new HtmlEmail(); // 创建对象
        email.setCharset("utf-8"); // 字符类型
        email.setHostName("smtp.qq.com"); // 邮箱的SMTP服务器地址
        email.setSmtpPort(465); // 配置端口号为465
        email.setSSLOnConnect(true); // 启用SSL
        email.setAuthentication("2511275284@qq.com", "gamuwuiltwofdjdi"); // 发件人邮箱;密码(授权码)
        email.setFrom("2511275284@qq.com", "购物系统客服");
        email.addTo(emailUrl); // 收件人邮箱
        email.setSubject("购物网站订单发货通知"); // 邮件标题
        email.setMsg("亲，您在" + shopName + "购买的商品发货啦!"); // 邮件内容
        email.send(); // 发送邮件
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        return true;
        }
        catch (Exception e) {
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            throw new RuntimeException("邮件发送失败", e);
        }
    }
}//消息队列重复工作发送邮件问题
