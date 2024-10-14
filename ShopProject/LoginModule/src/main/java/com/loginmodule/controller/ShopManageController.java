package com.loginmodule.controller;

import com.aliyuncs.exceptions.ClientException;
import com.loginmodule.anno.Log;
import com.loginmodule.pojo.Good;
import com.loginmodule.pojo.PageBean;
import com.loginmodule.pojo.Result;
import com.loginmodule.pojo.Shop;
import com.loginmodule.service.ShopManageService;
import com.loginmodule.utils.AliOssUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@Slf4j
@RequestMapping("/manageGood")
public class ShopManageController {
    @Autowired
    private ShopManageService shopManageService;
    @Autowired
    private AliOssUtils aliOssUtils;
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
    public Result deleteGood(@RequestParam (required=true)String goodId) throws Exception {
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
    @PostMapping("/uploadImage")
    public Result uploadImage(MultipartFile image) throws IOException, ClientException {
        log.info("图片上传,文件名:{}",image.getOriginalFilename());
        String url= aliOssUtils.upload(image);
        log.info("图片上传完成，访问的url:{}",url);
        return Result.success(url);
    }
    @GetMapping("/goods")
    public Result page(@RequestParam(required=false,defaultValue="1")Integer page,
                       @RequestParam(required=false,defaultValue ="10")Integer pageSize,
                       @RequestParam(required = false) String goodName,@RequestParam(required = false) Integer type,
                       @RequestParam(required = false) String shopName,
                       @RequestParam(required = false) Double plow,@RequestParam(required = false) Double phigh,
                       @RequestParam(required=false) Integer userId) {
        log.info("查询商品，参数：页数：{},页面大小：{},商品名称：{},商品类型{}，商店编号：{},价格范围：{}-{},查询用户{}",page,pageSize,goodName,type,shopName,plow,phigh,userId);
        PageBean pageBean=shopManageService.page(page,pageSize,goodName,type,shopName,plow,phigh,userId);
        return Result.success(pageBean);
    }
}
