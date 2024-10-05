package com.loginmodule.service.impl;


import com.loginmodule.anno.Log;
import com.loginmodule.mapper.ShopManageMapper;
import com.loginmodule.pojo.Good;
import com.loginmodule.pojo.Shop;
import com.loginmodule.service.ShopManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;

@Service
public class ShopManageServiceImpl implements ShopManageService {
    @Autowired
    private ShopManageMapper shopManageMapper;
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
    public void deleteById(String goodId) {
        shopManageMapper.deleteById(goodId);
    }

    @Override
    public void updateGood(Good good) {
        shopManageMapper.updateGood(good);
    }

    @Override
    public Shop getShop(String userName) {
        return shopManageMapper.getShop(userName);
    }

    @Override
    public void insertShop(String userId, String shopName) {
        Integer shopId=shopManageMapper.selectShopIdByshopName(shopName);
        if(shopId==null)
            shopManageMapper.insertShop(shopName);
        shopManageMapper.updateShopIntoUser(userId,shopId);
    }
}
