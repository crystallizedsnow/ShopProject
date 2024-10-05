package com.loginmodule.service.impl;

import com.loginmodule.mapper.OrderMapper;
import com.loginmodule.pojo.Order;
import com.loginmodule.pojo.OrderItem;
import com.loginmodule.pojo.OrderShop;
import com.loginmodule.service.OrderService;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    OrderMapper orderMapper;

    @Override
    public void insertOrder(Order order) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSSS");
        String orderId = sdf.format(System.currentTimeMillis());
        order.setOrderId(orderId);
        order.setCreateTime(LocalDateTime.now());
        orderMapper.insert(order);
        OrderShop[]orderShops=order.getOrderShops();
        OrderItem[] orderItems=order.getOrderItems();
        for(OrderItem orderItem:orderItems){
            orderMapper.insertOrderItem(orderId,orderItem.getGoodId(),orderItem.getBuyNum());
        }
        for(OrderShop ordershop:orderShops){
            orderMapper.insertOrderShop(orderId,ordershop.getShopId(),ordershop.getTotalMoney());
        }
    }

    @Override
    public void updateState(String orderId, Integer state) {
        orderMapper.update(orderId,state);
    }

    @Override
    public List<Map<String, Object>> getOrderByUserId(Integer userId, Integer state) {
        List<Map<String,Object>> orders=orderMapper.selectByUserId(userId,state);
        for (Map<String,Object> order : orders) {
            List<Integer>shopIds=new ArrayList<>();
            String orderId=(String)order.get("orderId");
            shopIds=orderMapper.selectshopByorderId(orderId);
            List<Map<String,Object>>itemsByshop=new ArrayList<>();
            for(Integer shopId:shopIds){
                Map<String,Object>itemByshop=new HashMap<String, Object>();;
                String shopName=orderMapper.getShopNameByShopId(shopId);
                itemByshop.put("shopName",shopName);
                List<Map<String,Object>>items=new ArrayList<>();
                items=orderMapper.selectItemByshopIdorderId(orderId,shopId);
                itemByshop.put("items",items);
                itemsByshop.add(itemByshop);
            }
            order.put("itemsByshop",itemsByshop);
        }
        return orders;
    }


    @Override
    public List<Map<String, Object>> getOrderByShopId(Integer shopId,Integer state) {
        List<Map<String,Object>> orders=orderMapper.selectByShopId(shopId,state);
        for (Map<String,Object> order : orders) {
            List<Integer>userIds=new ArrayList<>();
            String orderId=(String)order.get("orderId");
            userIds=orderMapper.selectuserByorderId(orderId);
            List<Map<String,Object>>items=new ArrayList<>();
            items=orderMapper.selectItemByuserIdOrderId(orderId,shopId);
            order.put("items",items);
        }
        return orders;
    }
    @Override
    public String findEmail(String orderId) {
        return orderMapper.selectEmailByuserId(orderId);
    }

    @Override
    public boolean sendEmail(String emailUrl, String shopName) throws EmailException {
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
        return true;
    }
}
