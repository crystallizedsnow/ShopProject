package com.loginmodule.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface ShopDataMapper {

    Map<String, Object> selectTotalorderByShopId(@Param("shopId") Integer shopId,@Param("createTimeLow") LocalDateTime createTimeLow,@Param("createTimeHigh") LocalDateTime createTimeHigh);//订单总数
    Map<String,Object> selectSaleOrderByShopId(@Param("shopId") Integer shopId,@Param("createTimeLow") LocalDateTime createTimeLow,@Param("createTimeHigh") LocalDateTime createTimeHigh);//成交订单（总销售额，订单总数）
    Map<String,Object> selectSuccessOrderByShopId(@Param("shopId") Integer shopId,@Param("createTimeLow") LocalDateTime createTimeLow,@Param("createTimeHigh") LocalDateTime createTimeHigh);//成功订单（算退款率）
    List<Map<String,Object>>selectGoodBySale(@Param("shopId") Integer shopId,@Param("createTimeLow") LocalDateTime createTimeLow,@Param("createTimeHigh") LocalDateTime createTimeHigh);//商品销量

}
