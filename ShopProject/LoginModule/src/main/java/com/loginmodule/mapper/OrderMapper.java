package com.loginmodule.mapper;

import com.loginmodule.pojo.Order;
import com.loginmodule.pojo.OrderItem;
import com.loginmodule.pojo.OrderShop;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {
    @Insert("insert into orders (orderId,userId,state,createTime,address,totalPrice) values" +
            "(#{orderId},#{userId},#{state},#{createTime},#{address},#{totalPrice})")
    void insert(Order order);
    @Update("update orders set state=#{state} where orderId=#{orderId}")
    void update(@Param("orderId") String orderId, @Param("state") Integer state);
//    @Select("select orders.orderId,orders.totalPrice,orders.address,order_state.stateName,orders.createTime,orders.userId from orders,order_state " +
//            "where userId=#{userId} and order_state.state=orders.state and order_state.state=#{state}")
    List<Map<String, Object>> selectByUserId(@Param("userId")Integer userId,@Param("state")Integer state);
//    @Select("select orders.orderId,orders.address,order_state.stateName,orders.createTime,order_shops.totalMoney,user.userName from orders,order_shops,user,order_state " +
//            "where order_shops.shopId=#{shopId} and orders.orderId=order_shops.orderId and user.userId=orders.userId and order_state.state=orders.state")
    List<Map<String, Object>> selectByShopId(@Param("shopId") Integer shopId,@Param("state") Integer state);

    @Insert("insert into order_items (orderId, goodId,buyNum) values (#{orderId},#{goodId},#{buyNum})")
    void insertOrderItem(@Param("orderId")String orderId, @Param("goodId")String goodId,@Param("buyNum")Integer buyNum);
    @Insert("insert into order_shops(orderId,shopId,totalMoney) values (#{orderId},#{shopId},#{totalMoney})")
    void insertOrderShop(@Param("orderId")String orderId,@Param("shopId")Integer shopId,@Param("totalMoney")double totalMoney );

    @Select("select distinct good.shopId from good,order_items " +
            "where order_items.orderId=#{orderId} and good.goodId=order_items.goodId")
    List<Integer> selectshopByorderId(@Param("orderId")String orderId);

    @Select("select distinct userId from orders " +
            "where orders.orderId=#{orderId}")
    List<Integer> selectuserByorderId(@Param("orderId")String orderId);

    @Select("select good.name as goodName,good.image,order_items.buyNum,good.price from order_items,good " +
            "where good.shopId=#{shopId} and order_items.orderId=#{orderId} and good.goodId=order_items.goodId")
    List<Map<String, Object>> selectItemByshopIdorderId(@Param("orderId") String orderId,@Param("shopId") Integer shopId);


    @Select("select good.name as goodName,good.image,order_items.buyNum,good.price from order_items,good " +
            "where order_items.orderId=#{orderId} and good.goodId=order_items.goodId and good.shopId=#{shopId}")
    List<Map<String, Object>> selectItemByuserIdOrderId(@Param("orderId") String orderId,@Param("shopId")Integer shopId);
//    @Select("select username from user where userid=#{userId}")
//    String getUserNameByuserId(@Param("userId") Integer userId);
    @Select("select shop.name from shop where shopId=#{shopId}")
    String getShopNameByShopId(@Param("shopId") Integer ShopId);
    @Select("select email from user,orders where orderId=#{orderId} and user.userId=orders.userId")
    String selectEmailByuserId(String orderId);
}
