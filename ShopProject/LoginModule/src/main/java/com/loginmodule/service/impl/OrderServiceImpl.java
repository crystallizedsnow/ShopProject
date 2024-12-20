package com.loginmodule.service.impl;

import com.loginmodule.mapper.OrderMapper;
import com.loginmodule.pojo.Order;
import com.loginmodule.pojo.OrderItem;
import com.loginmodule.pojo.OrderShop;
import com.loginmodule.pojo.Result;
import com.loginmodule.service.OrderService;
import com.loginmodule.utils.RedisLock;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    RedisLock redisLock;
    @Override
    public Result insertOrder(Order order) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSSS");
        String orderId = sdf.format(System.currentTimeMillis());
        order.setOrderId(orderId);
        order.setCreateTime(LocalDateTime.now());

        // 使用Redis锁来避免并发冲突
        String lockKey = "orderLock:" + orderId; // 锁的key，防止多次锁定同一个订单
        String lockValue = String.valueOf(System.currentTimeMillis()); // 锁的值，确保解锁时只有获得该锁的线程才能解锁
        boolean lockAcquired = redisLock.lock(lockKey, lockValue,100);
        if (!lockAcquired) {
            return Result.error("访问过于频繁，请重试");
        }
        try{
            List<String> nonegoodNames= checkNum(order);
            if(!nonegoodNames.isEmpty()){
                String error="抱歉，您下单的商品"+ Arrays.toString(nonegoodNames.toArray())+"已经售罄，下单失败";
                return Result.error(error);
            }

            OrderShop[]orderShops=order.getOrderShops();
            OrderItem[] orderItems=order.getOrderItems();
            for (OrderItem orderItem : orderItems) {
                // 为每个商品设置独立的锁，避免并发操作库存
                String goodLockKey = "goodLock:" + orderItem.getGoodId();  // 锁住商品的库存
                String goodLockValue = String.valueOf(System.currentTimeMillis());
                boolean goodLockAcquired = redisLock.lock(goodLockKey, goodLockValue, 100);  // 锁住当前商品

                if (!goodLockAcquired) {
                    return Result.error("操作过于频繁，请稍后重试");  // 如果商品锁没有获取到，说明库存正在更新
                }

                try {
                    orderMapper.insert(order);
                    // 进行订单商品插入及库存更新
                    orderMapper.insertOrderItem(orderId, orderItem.getGoodId(), orderItem.getBuyNum());
                    orderMapper.updateItemNum(orderItem.getGoodId(), orderItem.getBuyNum());
                } finally {
                    redisLock.unlock(goodLockKey, goodLockValue);
                }
            }
            // 释放商品锁
            for(OrderShop ordershop:orderShops){
                orderMapper.insertOrderShop(orderId,ordershop.getShopId(),ordershop.getTotalMoney());
            }
        }finally {
            // 释放锁
            redisLock.unlock(lockKey, lockValue);
        }
        return Result.success(order.getOrderId());
    }

    @Override
    public List<String> checkNum(Order order) {
        OrderItem[] orderItems=order.getOrderItems();
        List<String>noneGoodNames=new ArrayList<>();
        for(OrderItem orderItem:orderItems) {
            String goodName=orderMapper.selectNoneGoodName(orderItem.getGoodId());
            if(goodName!=null){
            noneGoodNames.add(goodName);}
        }
        return noneGoodNames;
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
                Map<String,Object>itemByshop=new HashMap<String, Object>();
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


}
