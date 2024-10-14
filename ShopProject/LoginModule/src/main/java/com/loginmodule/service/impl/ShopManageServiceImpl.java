package com.loginmodule.service.impl;


import com.aliyuncs.exceptions.ClientException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.loginmodule.anno.Log;
import com.loginmodule.mapper.DisplayMapper;
import com.loginmodule.mapper.ShopManageMapper;
import com.loginmodule.pojo.Good;
import com.loginmodule.pojo.PageBean;
import com.loginmodule.pojo.Shop;
import com.loginmodule.service.ShopManageService;
import com.loginmodule.utils.AliOssUtils;
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
    public void deleteById(String goodId) throws Exception {
        String url=shopManageMapper.selectUrlbyId(goodId);
        aliOssUtils.deleteImg(url);
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
