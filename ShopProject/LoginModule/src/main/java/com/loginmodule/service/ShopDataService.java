package com.loginmodule.service;

import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public interface ShopDataService {
    public Map<String, Object> getSaleData(Integer shopId,LocalDateTime createTimeLow, LocalDateTime createTimeHigh);
}
