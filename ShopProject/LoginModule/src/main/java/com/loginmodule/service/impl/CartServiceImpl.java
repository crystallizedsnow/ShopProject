package com.loginmodule.service.impl;

import com.loginmodule.mapper.CartMapper;
import com.loginmodule.mapper.UserLogMapper;
import com.loginmodule.pojo.CartGood;
import com.loginmodule.pojo.Good;
import com.loginmodule.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    CartMapper cartMapper;
    @Autowired
    UserLogMapper userLogMapper;
    @Override
    public void insertCart(CartGood cartGood) {
        CartGood cartGood1=cartMapper.selectIfExists(cartGood);
        if(cartGood1==null) {
            cartMapper.insert(cartGood);
            userLogMapper.insertUserLog(cartGood.getUserId(),cartGood.getGoodId(),2, LocalDateTime.now());
        }
        else{
            cartGood.setNum(cartGood1.getNum()+cartGood.getNum());
            cartMapper.updateNum(cartGood);
        }
    }

    @Override
    public void deleteCart(Integer userId,String goodId) {
        CartGood cartGood1=cartMapper.selectIfExists(new CartGood(userId,goodId,null));
        if(cartGood1!=null) {
            userLogMapper.insertUserLog(userId, goodId, 3,LocalDateTime.now());
            cartMapper.delete(userId, goodId);
        }
    }

    @Override
    public  List<Map<String, Object>>  getCartGoods(Integer userId) {
        return cartMapper.selectbyId(userId);
    }

    @Override
    public void clearCart(Integer userId) {
        cartMapper.deleteAll(userId);
    }

    @Override
    public void updateNum(CartGood cartGood) {
        cartMapper.updateNum(cartGood);
    }
}
