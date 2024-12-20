package com.loginmodule.aop;

import com.alibaba.fastjson.JSONObject;
import com.loginmodule.mapper.OperateLogMapper;
import com.loginmodule.pojo.OperateLog;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import com.loginmodule.utils.JwtUtils;

import javax.naming.AuthenticationException;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Arrays;

//AOP登录日志
@Slf4j
@Component//注入IOC
@Aspect//切面类
public class LogAspect {
    @Autowired
    private HttpServletRequest request;//拿令牌
    @Autowired
    private OperateLogMapper operateLogMapper;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Around("@annotation(com.loginmodule.anno.Log)")//环绕通知
    public Object recordLog(ProceedingJoinPoint joinPoint) throws Throwable {
        //插入记录
        String jwt=request.getHeader("token");
        Integer operateUser=null;
//        if(jwt!=null) {
//            Claims claims = JwtUtils.parseJWT(jwt);
//            operateUser = (Integer) claims.get("userId");
//        }
        if (jwt != null) {
            // 检查Redis中是否存在JWT
            String userIdFromRedis = redisTemplate.opsForValue().get("JWT:" + jwt);

            if (userIdFromRedis != null) {
                // 如果JWT有效，解析并获取用户信息
                Claims claims = JwtUtils.parseJWT(jwt);
                operateUser = (Integer) claims.get("userId");
            } else {
                // JWT无效，可能已经过期或不存在
                throw new AuthenticationException("Token is invalid or expired");
            }
        }
        LocalDateTime operateTime=LocalDateTime.now();
        String className=joinPoint.getTarget().getClass().getName();
        String methodName=joinPoint.getSignature().getName();
        Object[]args= joinPoint.getArgs();
        String methodParms= Arrays.toString(args);
        Long begin=System.currentTimeMillis();
        //运行原始方法
        Object result=joinPoint.proceed();
        Long end=System.currentTimeMillis();
        //将返回值转成字符串
        String returnValue= JSONObject.toJSONString(result);
        Long costTime=end-begin;
        //记录插入日志
        OperateLog operateLog = new OperateLog(null, operateUser, operateTime, className, methodName, methodParms, returnValue, costTime);
        operateLogMapper.insert(operateLog);
        log.info("AOP记录操作日志{}",operateLog);
        return result;
    }
}
