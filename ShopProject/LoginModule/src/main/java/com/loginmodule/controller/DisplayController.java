package com.loginmodule.controller;

import com.alibaba.fastjson.JSON;
import com.loginmodule.pojo.PageBean;
import com.loginmodule.pojo.Result;
import com.loginmodule.pojo.Type;
import com.loginmodule.service.DisplayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import com.loginmodule.utils.JwtUtils;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
//@RequestMapping
public class DisplayController {
    @Autowired
    private DisplayService displayService;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
//    @Log
    //展示商品列表

    @GetMapping("/goods")
    public Result page(@RequestParam(required = false, defaultValue = "1") Integer page,
                       @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                       @RequestParam(required = false) String goodName, @RequestParam(required = false) Integer type,
                       @RequestParam(required = false) String shopName,
                       @RequestParam(required = false) Double plow, @RequestParam(required = false) Double phigh,
                       @RequestParam(required = false) Integer userId) {

        // 生成一个唯一的缓存键
        String cacheKey = "goods:" + page + ":" + pageSize + ":" + goodName + ":" + type + ":" + shopName + ":" + plow + ":" + phigh + ":" + userId;

        // 尝试从缓存中获取商品数据
        String cachedData = redisTemplate.opsForValue().get(cacheKey);

//        if (cachedData != null) {
//            // 如果缓存存在，直接返回缓存数据
//            log.info("从缓存中获取商品数据，参数：页数：{}, 页面大小：{}", page, pageSize);
//            PageBean pageBean = JSON.parseObject(cachedData, PageBean.class);
//            return Result.success(pageBean);
//        } else {
            // 如果缓存不存在，查询数据库
            log.info("查询商品，参数：页数：{}, 页面大小：{}, 商品名称：{}, 商品类型：{}, 商店名称：{}, 价格范围：{}-{}, 查询用户：{}",
                    page, pageSize, goodName, type, shopName, plow, phigh, userId);

            // 执行数据库查询
            PageBean pageBean = displayService.page(page, pageSize, goodName, type, shopName, plow, phigh, userId);

            // 将查询结果存储到 Redis，并设置过期时间（例如 1 小时）
            redisTemplate.opsForValue().set(cacheKey, JSON.toJSONString(pageBean), 1, TimeUnit.HOURS);

            return Result.success(pageBean);
//        }
    }
    @GetMapping("/getGoodsByIds")
    public Result getGoodsByIds(@RequestParam String goodIdsStr){
        String[] goodIds = goodIdsStr.split(",");
        log.info("请求的goodsId:{}", (Object) goodIds);
       List<Map<String,Object>>goods=displayService.getGoodsByIds(goodIds);
        return Result.success(goods);
    }
    //展示用户名
    @GetMapping("/getUsernameAndId")
    public Result getUsernameAndIdFromToken(@RequestHeader("token") String token) {
        // 从JWT中解析用户名
        Map<String,Object> data = JwtUtils.parseJWT(token);
        String name=(String)data.get("username");
        Integer userId=(Integer)data.get("userId");
        log.info("请求的username：{},userId:{}",name,userId);
        return Result.success(data);
    }
    @GetMapping("/getTypeName")
    public Result getTypeName(@RequestParam(required=true)Integer type)
    {
        String typeName=displayService.getTypeName(type);
        log.info("请求type:{}.{}",type,typeName);
        return Result.success(typeName);
    }
    @GetMapping("/getTypeName/all")
    public Result getAllTypes()
    {
        log.info("请求所有typeName");
        List<Type> types=displayService.getTypes();
        return Result.success(types);
    }

}
