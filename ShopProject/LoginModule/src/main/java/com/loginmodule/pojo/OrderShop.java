package com.loginmodule.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderShop {
private String orderId;
private Integer shopId;
    private double totalMoney;
}
