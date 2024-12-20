package com.loginmodule.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLog {
    private Integer userId;
    private String goodId;
    private Integer action;
    private LocalDateTime createTime;
}
