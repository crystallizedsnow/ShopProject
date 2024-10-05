package com.loginmodule.mapper;

import com.loginmodule.pojo.CartGood;
import com.loginmodule.pojo.Good;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface CartMapper {
    @Insert("insert into cart(userId,goodId,num) VALUES(#{userId},#{goodId},#{num})")
    void insert(CartGood cartGood);
    @Delete("delete from cart where userId=#{userId} AND goodId=#{goodId}")
    void delete(@Param("userId") Integer userId,@Param("goodId") String goodId);
    @Select("SELECT good.goodId, good.name, price,good.num as num, shop.name AS shopName, image, type, cart.num as cartnum " +
            "FROM cart, good, shop " +
            "WHERE userId = #{userId} AND good.goodId = cart.goodId AND good.shopId = shop.shopId")
    List<Map<String, Object>> selectbyId(Integer userId);
    @Delete("delete from cart where userId=#{userId}")
    void deleteAll(Integer userId);
    @Update("update cart set num=#{num} where userId=#{userId} and goodId=#{goodId}")
    void updateNum(CartGood cartGood);

    @Select("select * from cart where userId=#{userId} and goodId=#{goodId}")
    CartGood selectIfExists(CartGood cartGood);
}
