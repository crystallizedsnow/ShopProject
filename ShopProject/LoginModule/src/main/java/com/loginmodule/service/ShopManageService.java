package com.loginmodule.service;

import com.aliyuncs.exceptions.ClientException;
import com.loginmodule.pojo.Good;
import com.loginmodule.pojo.PageBean;
import com.loginmodule.pojo.Shop;
import org.apache.ibatis.annotations.Delete;
import org.springframework.stereotype.Service;

@Service
public interface ShopManageService {
    boolean insertGood(Good good);
    void deleteById(String goodId) throws Exception;

    void updateGood(Good good);

    Shop getShop(String userName);

    void insertShop(String userId, String shopName);

    PageBean page(Integer page, Integer pageSize, String goodName, Integer type, String shopName, Double plow, Double phigh, Integer userId);
}
