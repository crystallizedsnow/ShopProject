package com.loginmodule.controller;

import com.loginmodule.pojo.Result;
import com.loginmodule.pojo.User;
import com.loginmodule.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.loginmodule.utils.JwtUtils;

import java.util.HashMap;
import java.util.Map;
@RestController
@Slf4j
public class LoginController {
    @Autowired
    private LoginService loginService;
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
            return Result.success(data);
        }
        return u != null ?Result.success():Result.error("用户名或密码错误");
    }
    @PostMapping("/register")
    public Result register(@RequestBody User user){
        log.info("注册{}",user);
        boolean flag= loginService.register(user);
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
