package com.loginmodule.controller;

import com.loginmodule.mapper.LoginMapper;
import com.loginmodule.mapper.ShopManageMapper;
import com.loginmodule.pojo.Result;
import com.loginmodule.pojo.User;
import com.loginmodule.service.LoginService;
import com.loginmodule.service.ShopManageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import com.loginmodule.utils.JwtUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
public class LoginController {
    @Autowired
    private LoginService loginService;
    @Autowired
    private ShopManageService shopManageService;
    @Autowired
    private ShopManageMapper shopManageMapper;
    @Autowired
    private LoginMapper loginMapper;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @PostMapping("/login")
    public Result login(@RequestBody User user){
        log.info("登录{}",user);
        User u=loginService.login(user);
        //登录成功，发放令牌
        if(u!=null){
            Map<String,Object> claims=new HashMap<>();
            claims.put("userId",u.getUserId());
            claims.put("username",u.getUsername());
            String jwt= JwtUtils.generateJwt(claims);
            Integer type=u.getType();
            // 返回JWT和用户类型
            Map<String, Object> data = new HashMap<>();
            data.put("token", jwt);
            data.put("type", type);
            // 将JWT存储到Redis，设置过期时间（例如：1小时）
            redisTemplate.opsForValue().set("JWT:" + jwt, u.getUserId().toString(), 1, TimeUnit.HOURS);
            return Result.success(data);
        }
        return u != null ?Result.success():Result.error("用户名或密码错误");
    }
    @PostMapping("/register")
    public Result register(@RequestBody User user,@RequestParam(required = false)String shopName){
        log.info("注册{}",user);
        boolean flag =false;
        if(user.getType()==0) {
           flag=loginService.register(user);
        }
        else{
            Integer shopId=shopManageMapper.selectShopIdByshopName(shopName);
            if(shopId!=null){
                return Result.error("该商店名已被占用");
            }
            flag=loginService.register(user);
            user=loginMapper.getByemail(user);
            log.info("user{}插入shop{}", user.getUserId(), shopName);
            shopManageService.insertShop(user.getUserId(),shopName);
        }
        if(flag) {
            return Result.success("成功注册，请登录");
        }
        else {
            return Result.error("该邮箱已经注册，请直接登录");
        }
    }
    @PostMapping("/logout")
    public Result logout() {
        log.info("退出登录");
        return Result.success();
    }
}
