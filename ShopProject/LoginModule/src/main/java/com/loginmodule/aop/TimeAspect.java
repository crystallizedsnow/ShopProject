package com.loginmodule.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
//记录操作时间
@Component
@Aspect
@Slf4j
public class TimeAspect {
    @Pointcut("execution(* com.loginmodule.service.*.*(..))")//抽取切入点表达式
   public void pt(){}//在外部类也能引入该切入点表达式
    @Around("pt()")
    public Object recordTime(ProceedingJoinPoint joinPoint) throws Throwable {
       //1.记录开始时间
        long begin=System.currentTimeMillis();
        //2.调用原始方法
        Object result=joinPoint.proceed();
        //3.记录结束时间
        long end=System.currentTimeMillis();
        log.info(joinPoint.getSignature()+"方法执行耗时:{}ms",end-begin);
        return result;
    }
}
