package com.loginmodule.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private String orderId;//订单号
    private Integer userId;//用户号
    private Integer state;//订单状态(0未付款，1未发货，2已发货，3已收货，4取消订单）
    private LocalDateTime createTime;//创建时间
    private String address;//地址
    private Double totalPrice;//总价
    private OrderItem[] orderItems;//商品
    private OrderShop[] orderShops;//商家
}
