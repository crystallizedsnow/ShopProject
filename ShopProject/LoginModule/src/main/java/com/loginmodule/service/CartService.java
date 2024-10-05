package com.loginmodule.service;

import com.loginmodule.pojo.CartGood;
import com.loginmodule.pojo.Good;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface CartService {
    void insertCart(CartGood cartGood);

    void deleteCart(Integer userId,String goodId);

    List<Map<String, Object>>getCartGoods(Integer userId);

    void clearCart(Integer userId);

    void updateNum(CartGood cartGood);
}
