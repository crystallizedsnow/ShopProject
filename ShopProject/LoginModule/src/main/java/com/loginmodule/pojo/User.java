package com.loginmodule.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Integer userId;//ID
    private String username;//用户名
    private String password;//密码
    private String email;//邮箱
    private int type;//客户账号0，商家账号1
    private Integer shop_id;//商家账号绑定商家
}
