package com.loginmodule.service.impl;


import com.aliyuncs.exceptions.ClientException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.loginmodule.anno.Log;
import com.loginmodule.mapper.DisplayMapper;
import com.loginmodule.mapper.ShopManageMapper;
import com.loginmodule.pojo.Good;
import com.loginmodule.pojo.PageBean;
import com.loginmodule.pojo.Result;
import com.loginmodule.pojo.Shop;
import com.loginmodule.service.ShopManageService;
import com.loginmodule.utils.AliOssUtils;
import com.loginmodule.utils.RedisLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShopManageServiceImpl implements ShopManageService {
    @Autowired
    private ShopManageMapper shopManageMapper;
    @Autowired
    private DisplayMapper displayMapper;
    @Autowired
    private AliOssUtils aliOssUtils;
    @Autowired
    private RedisLock redisLock;
    @Override
    public boolean insertGood(Good good) {
        if(shopManageMapper.selectByGoodIdAndName(good)==null) {
            /**
             * 根据时间戳生成唯一id(处理不了太高并发量）
             */

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSSS");
            String id = sdf.format(System.currentTimeMillis());
            System.out.println("id:"+id);
            good.setGoodId(id);
            shopManageMapper.insertGood(good);
            return true;
        }
        else
            return false;
    }

    @Override
    public Result deleteById(String goodId) throws Exception {
        String goodLockKey = "goodLock:" + goodId;  // 锁住商品的库存
        String goodLockValue = String.valueOf(System.currentTimeMillis());
        boolean goodLockAcquired = redisLock.lock(goodLockKey, goodLockValue, 100);  // 锁住当前商品
        if (!goodLockAcquired) {
            return Result.error("访问过于频繁，请重试");
        }
        try {
        String url=shopManageMapper.selectUrlbyId(goodId);
        aliOssUtils.deleteImg(url);
        shopManageMapper.deleteById(goodId);
        } finally {
            // 释放商品锁
            redisLock.unlock(goodLockKey, goodLockValue);
        }
        return Result.success();
    }

    @Override
    public Result updateGood(Good good) {
        String goodLockKey = "goodLock:" + good.getGoodId();  // 锁住商品的库存
        String goodLockValue = String.valueOf(System.currentTimeMillis());
        boolean goodLockAcquired = redisLock.lock(goodLockKey, goodLockValue, 100);  // 锁住当前商品
        if (!goodLockAcquired) {
            return Result.error("访问过于频繁，请重试");
        }    try {
        shopManageMapper.updateGood(good); } finally {
            // 释放商品锁
            redisLock.unlock(goodLockKey, goodLockValue);
        }
        return Result.success();
    }

    @Override
    public Shop getShop(String userName) {
        return shopManageMapper.getShop(userName);
    }

    @Override
    public Integer insertShop(Integer userId, String shopName) {
        shopManageMapper.insertShop(shopName);
        Integer shopId=shopManageMapper.selectShopIdByshopName(shopName);
        shopManageMapper.updateShopIntoUser(userId,shopId);
        return shopId;
    }
    @Override
    public PageBean page(Integer page, Integer pageSize, String goodName, Integer type, String shopName, Double plow, Double phigh, Integer userId) {
        //1.设置分页参数
        PageHelper.startPage(page,pageSize);
        //2.执行查询
        List<Good> goodList= displayMapper.list(goodName,type,shopName,plow,phigh);
        Page<Good> p=(Page<Good>)goodList;
        //3.封装PageBean对象
        PageBean pageBean=new PageBean(p.getTotal(),p.getResult());
        return pageBean;
    }
}
