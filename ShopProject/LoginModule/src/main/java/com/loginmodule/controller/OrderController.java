package com.loginmodule.controller;

import com.loginmodule.pojo.Order;
import com.loginmodule.pojo.Result;
import com.loginmodule.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.mail.EmailException;
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
    @PostMapping("/insert")
    public Result insertOrder(@RequestBody Order order){
        log.info("生成订单：{}",order);
        List<String> nonegoodNames= orderService.checkNum(order);;
        if(!nonegoodNames.isEmpty()){
        String error="抱歉，您下单的商品"+Arrays.toString(nonegoodNames.toArray())+"已经售罄，下单失败";
        return Result.error(error);
        };
        orderService.insertOrder(order);
        return Result.success(order.getOrderId());
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
    public Result sendEmail(@RequestParam String orderId,@RequestParam String shopName) throws EmailException {
        String email=orderService.findEmail(orderId);
        if(orderService.sendEmail(email,shopName)) {
            return Result.success();
        }
        else{
            return Result.error("未能成功发送邮件");
        }
    }
}
