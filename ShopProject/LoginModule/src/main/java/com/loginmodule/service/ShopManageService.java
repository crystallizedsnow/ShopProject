package com.loginmodule.service;

import com.loginmodule.pojo.Good;
import com.loginmodule.pojo.Shop;
import org.apache.ibatis.annotations.Delete;
import org.springframework.stereotype.Service;

@Service
public interface ShopManageService {
    boolean insertGood(Good good);
    void deleteById(String goodId);

    void updateGood(Good good);

    Shop getShop(String userName);

    void insertShop(String userId, String shopName);
}
