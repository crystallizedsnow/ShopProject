package com.loginmodule.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    // 声明 orderQueue 队列
    @Bean
    public Queue orderQueue() {
        return new Queue("orderQueue", true);
    }

    // 声明 emailQueue 队列
    @Bean
    public Queue emailQueue() {
        return new Queue("emailQueue", true);  // 声明 emailQueue 队列，持久化
    }

    // 声明交换机
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange("orderExchange");
    }

    // 绑定队列与交换机
    @Bean
    public Binding binding(Queue orderQueue, TopicExchange exchange) {
        return BindingBuilder.bind(orderQueue).to(exchange).with("order.#");
    }

    // 绑定 emailQueue 队列与交换机
    @Bean
    public Binding emailQueueBinding(Queue emailQueue, TopicExchange exchange) {
        return BindingBuilder.bind(emailQueue).to(exchange).with("email.#");
    }
}

