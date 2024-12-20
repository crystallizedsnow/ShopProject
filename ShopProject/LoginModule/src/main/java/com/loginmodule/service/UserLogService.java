package com.loginmodule.service;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public interface UserLogService {
    Map<String, Object> getUserLog(Integer shopId,LocalDateTime createTimeLow,LocalDateTime createTimeHigh);
    public void batchInsertLogs();
    public void recordUserLog(Integer userId, String goodId, Integer type, LocalDateTime time);
}
