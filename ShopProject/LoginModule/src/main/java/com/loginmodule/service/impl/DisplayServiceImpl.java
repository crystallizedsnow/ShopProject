package com.loginmodule.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.loginmodule.mapper.DisplayMapper;
import com.loginmodule.mapper.UserLogMapper;
import com.loginmodule.pojo.Good;
import com.loginmodule.pojo.PageBean;
import com.loginmodule.pojo.Type;
import com.loginmodule.pojo.UserLog;
import com.loginmodule.service.DisplayService;
import com.loginmodule.service.UserLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class DisplayServiceImpl implements DisplayService {
    @Autowired
    private DisplayMapper displayMapper;
    @Autowired
    private UserLogService userLogService;
    @Override
    public List<Type> getTypes() {
        List<Type>types=displayMapper.getTypes();
        return types;
    }

    @Override
    public PageBean page(Integer page, Integer pageSize, String goodName, Integer type, String shopName,Double plow,Double phigh,Integer userId) {
        //1.设置分页参数
        PageHelper.startPage(page,pageSize);
        //2.执行查询
        List<Good>goodList= displayMapper.list(goodName,type,shopName,plow,phigh);
       //    goodList.forEach(good->userLogMapper.insertUserLog(userId,good.getGoodId(),1, LocalDateTime.now()));
        goodList.forEach(good->userLogService.recordUserLog(userId,good.getGoodId(),1,LocalDateTime.now()));
        Page<Good> p=(Page<Good>)goodList;
        //3.封装PageBean对象
        PageBean pageBean=new PageBean(p.getTotal(),p.getResult());
        return pageBean;
    }



    @Override
    public String getTypeName(Integer type) {
        return displayMapper.getTypeName(type);
    }

    @Override
    public List<Map<String, Object>> getGoodsByIds(String[] goodIds) {
        List<Map<String, Object>>goods=new ArrayList<>();
        for(String goodId:goodIds){
            goods.add(displayMapper.selectById(goodId));
        }
        return goods;
    }


}
