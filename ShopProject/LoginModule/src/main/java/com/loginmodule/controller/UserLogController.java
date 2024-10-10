package com.loginmodule.controller;

import com.loginmodule.pojo.Result;
import com.loginmodule.service.UserLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/userlog")
public class UserLogController {
    @Autowired
    UserLogService userLogService;
    @GetMapping("/get")
    public Result getUserLog(@RequestParam Integer shopId,@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createTimeLow,
                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createTimeHigh){
        log.info("商店{}查询其商店用户日志",shopId);
        Map<String,Object> userLog=userLogService.getUserLog(shopId,createTimeLow,createTimeHigh);
        return Result.success(userLog);
        
    }
}
