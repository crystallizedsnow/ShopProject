package com.loginmodule.service.impl;

import com.loginmodule.mapper.UserLogMapper;
import com.loginmodule.service.UserLogService;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserLogServiceimpl implements UserLogService {
    @Autowired
    UserLogMapper userLogMapper;
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
}
