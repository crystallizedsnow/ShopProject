package com.loginmodule.service;

import com.loginmodule.pojo.PageBean;
import com.loginmodule.pojo.Type;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface DisplayService {
    PageBean page(Integer page, Integer pageSize, String goodName, Integer type, String shopName,Double plow,Double phigh,Integer userId);

    String getTypeName(Integer type);

    List<Type> getTypes();

    List<Map<String, Object>> getGoodsByIds(String[] goodIds);

}
