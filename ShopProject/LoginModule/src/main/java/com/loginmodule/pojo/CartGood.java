package com.loginmodule.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
//加入
public class CartGood {
    private Integer userId;//用户号
    private String goodId;//商品号
    private Integer num;//购买个数
//    private Integer typeId;//购买类型
}
