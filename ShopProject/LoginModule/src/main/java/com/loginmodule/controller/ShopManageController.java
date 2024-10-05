package com.loginmodule.controller;

import com.loginmodule.anno.Log;
import com.loginmodule.pojo.Good;
import com.loginmodule.pojo.Result;
import com.loginmodule.pojo.Shop;
import com.loginmodule.service.ShopManageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/manageGood")
public class ShopManageController {
    @Autowired
    private ShopManageService shopManageService;
    @PostMapping("/applyShop")
    public Result insertShop(@RequestParam String userId,@RequestParam String shopName){
        log.info("user{}插入shop{}",userId,shopName);
        shopManageService.insertShop(userId,shopName);
        return Result.success();
    }
//    @Log
    //增加商品
    @PostMapping("/insertGood")
    public Result insertGood(@RequestBody Good good)
    {
        log.info("增加商品：{}",good);
        boolean flag=shopManageService.insertGood(good);
        if(flag)
            return Result.success();
        else
            return Result.error("本店已存在该商品，请直接修改数目。");
    }
//    @Log
    //删除商品
    @DeleteMapping("/deleteGood")
    public Result deleteGood(@RequestParam (required=true)String goodId)
    {
        log.info("删除商品，编号：{}",goodId);
        shopManageService.deleteById(goodId);
        return Result.success();
    }
//    @Log
    @PostMapping("/updateGood")
    public Result updateGood(@RequestBody Good good)
    {
        log.info("修改商品：{}",good);
        shopManageService.updateGood(good);
        return Result.success();
    }
    @GetMapping("/getShop")
    public Result getShop(@RequestParam (required=true)String userName)
    {
        Shop shop=shopManageService.getShop(userName);
        log.info("用户{}请求其店铺{}",userName,shop);
        return Result.success(shop);
    }
}
