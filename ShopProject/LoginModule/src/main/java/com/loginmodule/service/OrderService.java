package com.loginmodule.service;

import com.loginmodule.pojo.Order;
import com.loginmodule.pojo.Result;
import org.apache.commons.mail.EmailException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


@Service
public interface OrderService {

    Result insertOrder(Order order);

    void updateState(String orderId, Integer state);

    List<Map<String, Object>>getOrderByUserId(Integer userId,Integer state);

    List<Map<String, Object>> getOrderByShopId(Integer shopId,Integer state);


    List<String> checkNum(Order order);
}
