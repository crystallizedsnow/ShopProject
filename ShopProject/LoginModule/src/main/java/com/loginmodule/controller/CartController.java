package com.loginmodule.controller;

import com.loginmodule.mapper.UserLogMapper;
import com.loginmodule.pojo.CartGood;
import com.loginmodule.pojo.Good;
import com.loginmodule.pojo.Result;
import com.loginmodule.service.CartService;
import com.loginmodule.service.UserLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/cart")
public class CartController {
    @Autowired
    CartService cartService;
    @PostMapping("/insert")
    public Result insertCart(@RequestBody CartGood cartGood){
        log.info("加入购物车{}",cartGood);
        cartService.insertCart(cartGood);
        return Result.success();
    }
    @DeleteMapping("/delete")
    public Result deleteCart(@RequestParam Integer userId,@RequestParam String goodId){
        log.info("{}从购物车中移除货物:{}",userId,goodId);
        cartService.deleteCart(userId,goodId);
        return Result.success();
    }
    @GetMapping("/getGoods")
    public Result getCartGoods(@RequestParam Integer userId){
        log.info("用户{}请求其购物车货物",userId);
        List<Map<String, Object>> cartGoods=cartService.getCartGoods(userId);
        return Result.success(cartGoods);
    }
    @DeleteMapping("/clear")
    public Result clearCart(@RequestParam Integer userId){
        log.info("用户{}清空其购物车",userId);
        cartService.clearCart(userId);
        return Result.success();
    }//修改成删除选择的商品
    @PostMapping("/updateNum")
    public Result updateNum(@RequestBody Map<String,Object>updateRequest){
        CartGood cartGood=new CartGood((Integer)updateRequest.get("userId"),
                (String) updateRequest.get("goodId"),
                (Integer) updateRequest.get("num"));
        log.info("用户{}更新其{}商品数量为{}",cartGood.getUserId(),cartGood.getGoodId(),cartGood.getNum());
        cartService.updateNum(cartGood);
        return Result.success();
    }
}
