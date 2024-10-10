package com.loginmodule.service.impl;

import com.loginmodule.mapper.ShopDataMapper;
import com.loginmodule.service.ShopDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ShopDataServiceImpl implements ShopDataService {
    @Autowired
    ShopDataMapper shopDataMapper;
    @Override
    public Map<String, Object> getSaleData(Integer shopId,LocalDateTime createTimeLow, LocalDateTime createTimeHigh) {
        Map<String, Object>info=shopDataMapper.selectTotalorderByShopId(shopId,createTimeLow,createTimeHigh);
        Map<String, Object>info2=shopDataMapper.selectSuccessOrderByShopId(shopId,createTimeLow,createTimeHigh);
        Map<String, Object>info3=shopDataMapper.selectSaleOrderByShopId(shopId,createTimeLow,createTimeHigh);
        info.putAll(info2);
        info.putAll(info3);
        Long orderTotalNum =(Long)info.get("orderTotalNum");//总订单量
        Long orderSuccessNum=(Long) info.get("orderSuccessNum");//成功订单量
        double returnRate=0;
        if(orderSuccessNum!=0)
            returnRate=1-(double)orderSuccessNum/(double)orderTotalNum;//退货率
        info.put("returnRate",returnRate);
        Long orderSaleNum=(Long) info.get("orderSaleNum");//销售量
        Double saleMoney=(Double) info.get("saleMoney");//销售额
        double avgSaleMoney=0;
        if(saleMoney!=0)
            avgSaleMoney=saleMoney/(double) orderSaleNum;
        info.put("avgSaleMoney",avgSaleMoney);//平均订单金额
        List<Map<String, Object>> goodsBySale = shopDataMapper.selectGoodBySale(shopId, createTimeLow, createTimeHigh);
        List<Map<String, Object>> modifiableGoodsBySale = new ArrayList<>(goodsBySale);
        info.put("goodsBySale", modifiableGoodsBySale);
//        info.put("goodsBySale", goodsBySale);
        return info;
    }
}
