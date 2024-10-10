package com.loginmodule.controller;

import com.loginmodule.pojo.Result;
import com.loginmodule.service.ShopDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/shopdata")
public class ShopDataController {
    @Autowired
    ShopDataService shopDataService;
    @GetMapping("/getsale")
    public Result getSale(@RequestParam Integer shopId,   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createTimeLow,
                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createTimeHigh){
        log.info("商店{}请求报表,时间从{}到{}",shopId,createTimeLow,createTimeHigh);
        Map<String,Object> shopSale=shopDataService.getSaleData(shopId,createTimeLow, createTimeHigh);
        return Result.success(shopSale);
    }
}
