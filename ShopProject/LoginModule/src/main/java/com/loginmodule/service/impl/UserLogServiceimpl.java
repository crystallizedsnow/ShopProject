package com.loginmodule.service.impl;

import com.alibaba.fastjson.JSON;
import com.loginmodule.mapper.UserLogMapper;
import com.loginmodule.pojo.UserLog;
import com.loginmodule.service.UserLogService;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserLogServiceimpl implements UserLogService {
    @Autowired
    UserLogMapper userLogMapper;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Override
    public Map<String, Object> getUserLog(Integer shopId, LocalDateTime createTimeLow, LocalDateTime createTimeHigh) {
        List<Map<String,Object>> userLogView=userLogMapper.selectUserLog(shopId,1,createTimeLow, createTimeHigh);
        List<Map<String,Object>>userLogCart=userLogMapper.selectUserLog(shopId,2,createTimeLow, createTimeHigh);
        List<Map<String,Object>>userLogCartDelete=userLogMapper.selectUserLog(shopId,3,createTimeLow, createTimeHigh);
        Map<String,Object>userLog=new HashMap<>();
        userLog.put("userLogView",userLogView);
        userLog.put("userLogCart",userLogCart);
        userLog.put("userLogCartdelete",userLogCartDelete);
        return userLog;
    }
    // 每分钟检查一次日志缓存
    @Override
    @Scheduled(fixedDelay = 60000)  // 每60秒执行一次
    public void batchInsertLogs() {
        // 从 Redis 列表中获取日志
        List<String> logs = redisTemplate.opsForList().range("userLogs", 0, -1);

        if (logs != null && !logs.isEmpty()) {
            // 解析日志数据
            List<UserLog> userLogs = logs.stream()
                    .map(log -> JSON.parseObject(log, UserLog.class))
                    .collect(Collectors.toList());//java stream流，转为UserLog,处理到list

            // 批量插入数据库
            if (!userLogs.isEmpty()) {
                log.info("插入数据库");
                userLogMapper.batchInsertUserLogs(userLogs);
                // 清空 Redis 中的缓存
                redisTemplate.delete("userLogs");
            }
        }
    }
    public void recordUserLog(Integer userId, String goodId, Integer type, LocalDateTime time) {
        // 构建用户日志数据
        UserLog userLog = new UserLog(userId, goodId, type,time);

        // 将日志数据序列化为 JSON 字符串
        String logData = JSON.toJSONString(userLog);

        // 存入 Redis 列表中
        redisTemplate.opsForList().rightPush("userLogs", logData);
        // 设置 Redis 键的过期时间为 1 小时
        redisTemplate.expire("userLogs", 1, TimeUnit.HOURS);  // 设置过期时间为 1 小时
    }
}
