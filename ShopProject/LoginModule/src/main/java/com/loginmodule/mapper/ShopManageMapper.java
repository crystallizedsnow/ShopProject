package com.loginmodule.mapper;

import com.loginmodule.pojo.Good;
import com.loginmodule.pojo.Shop;
import org.apache.ibatis.annotations.*;

@Mapper
public interface ShopManageMapper {
    @Insert("insert into good(goodId,name,price,num,shopId,image,type) " +
            "values(#{goodId},#{name},#{price},#{num},#{shopId},#{image},#{type})")
    void insertGood(Good good);
    @Select("select * from good where name=#{name} and shopId=#{shopId}")
    Good selectByGoodIdAndName(Good good);
    @Delete("delete from good where goodId=#{goodId}")
    void deleteById(String goodId);

    void updateGood(Good good);
    @Select("select shop.shopId as shopId,shop.name as name from shop,user where shop.shopId=user.shopId and user.username=#{userName}")
    Shop getShop(String userName);

    @Insert("insert into shop(name) value (#{shopName})")
    void insertShop(String shopName);
    @Select("select shopId from shop where name=#{shopName}")
    Integer selectShopIdByshopName(String shopName);
    @Insert("update user set shopId=#{shopId} where userId=#{userId}")
    void updateShopIntoUser(@Param("userId")String userId, @Param("shopId")Integer shopId);
    @Select("select image from good where goodId=#{goodId}")
    String selectUrlbyId(String goodId);
}
