package com.loginmodule.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Good {
    private String goodId;//商品ID
    private Integer shopId;//商店ID
    private String name;//商品名称
    private Double price;//商品价格
    private Integer num;//商品数量
    private String image;//图像url
    private Integer type;//商品类型
}
