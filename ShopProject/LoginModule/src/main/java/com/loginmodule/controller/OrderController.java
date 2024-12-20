package com.loginmodule.controller;

import com.loginmodule.pojo.EmailMessage;
import com.loginmodule.pojo.Order;
import com.loginmodule.pojo.Result;
import com.loginmodule.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.mail.EmailException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/order")
public class OrderController {
    @Autowired
    OrderService orderService;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @PostMapping("/insert")
    public Result insertOrder(@RequestBody Order order){
        log.info("生成订单：{}",order);
        return orderService.insertOrder(order);
    }
    @PostMapping("/updateState")
    public Result updateState(@RequestParam String orderId,@RequestParam Integer state) {
        log.info("更新订单{}状态：{}", orderId, state);
        orderService.updateState(orderId,state);
        return Result.success();
    }
    @GetMapping("/getOrderUser")
    public Result getOrderByUserId(@RequestParam Integer userId,@RequestParam(required = false)Integer state){
        log.info("用户{}查询状态{}订单",userId,state);
        List<Map<String, Object>>data= orderService.getOrderByUserId(userId,state);
        return Result.success(data);
    }
    @GetMapping("/getOrderShop")
    public Result getOrderByShopId(@RequestParam Integer shopId,@RequestParam(required = false)Integer state){
        log.info("商家{}查询状态{}订单",shopId,state);
        List<Map<String,Object>>orders=orderService.getOrderByShopId(shopId,state);
        return Result.success(orders);
    }
    @PostMapping("/sendEmail")
    public Result sendEmail(@RequestParam String orderId, @RequestParam String shopName) {
        // 将发送邮件的请求通过消息队列异步处理
        try {
            // 发送消息到RabbitMQ队列，处理邮件发送
            //设置开启Mandatory,才能触发回调函数,无论消息推送结果怎么样都强制调用回调函数
            rabbitTemplate.setMandatory(true);
            rabbitTemplate.convertAndSend("orderExchange", "order.sendEmail", new EmailMessage(orderId, shopName), message -> {
                message.getMessageProperties().setReplyTo("responseQueue"); // 设置回复队列
                return message;
            });
            return Result.success("邮件发送请求已提交，正在异步处理...");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("邮件发送失败");
        }
    }
}
