package com.loginmodule.service;

import com.loginmodule.pojo.EmailMessage;
import com.rabbitmq.client.Channel;
import org.apache.commons.mail.EmailException;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface EmailService {
    public boolean sendEmail(EmailMessage emailMessage, Message message, Channel channel) throws EmailException, IOException;
    String findEmail(String orderId);
}
