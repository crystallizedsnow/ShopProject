package com.loginmodule.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisLock {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    // 获取锁
    public boolean lock(String lockKey, String value,int timeout) {
        Boolean success = redisTemplate.opsForValue().setIfAbsent(lockKey, value, timeout, TimeUnit.SECONDS); // 设置锁有效期为1秒
        return success != null && success;
    }

    // 释放锁
    public void unlock(String lockKey, String value) {
        String currentValue = redisTemplate.opsForValue().get(lockKey);
        if (value.equals(currentValue)) {
            redisTemplate.delete(lockKey); // 删除锁
        }
    }
}
