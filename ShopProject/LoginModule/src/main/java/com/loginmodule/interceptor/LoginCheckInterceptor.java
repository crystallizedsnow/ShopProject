package com.loginmodule.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.loginmodule.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import utils.JwtUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class LoginCheckInterceptor implements HandlerInterceptor {
    @Override//目标资源运行前，返回true,放行，false不放行
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1.获取请求url
        String url=request.getRequestURL().toString();
        log.info("请求的url{}",url);
        //2.判断请求url中是否包含login,register,若包含，则是登录操作，放行
        if(url.contains("login")||url.contains("register")||url.contains("good")||url.contains("display")||url.contains("getTypeName"))
        {
            log.info("放行");
            return true;
        }
        if (request.getMethod().equals("OPTIONS")) {
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }
            //3.获取请求头中的令牌（token）
        String jwt=request.getHeader("token");
        //4.判断令牌是否存在，若不存在，返回错误结果
        if(!StringUtils.hasLength(jwt))//若jwt为none or空，返回未登录
        {
            log.info("请求头为空，返回未登录信息");
            Result error = Result.error("NOT_LOGIN");
            //手动转换对象为json工具（controller自动转换）,响应回去
            String notLogin= JSONObject.toJSONString(error);//alibaba依赖
            response.getWriter().write(notLogin);
            return false;
        }
        //5.解析token,解析失败返回错误
        try{
            JwtUtils.parseJWT(jwt);}
        catch(Exception e){
            e.printStackTrace();
            log.info("解析令牌失败，返回未登录信息");
            Result error = Result.error("NOT_LOGIN");
            //手动转换对象为json工具（controller自动转换）,响应回去
            String notLogin= JSONObject.toJSONString(error);//alibaba依赖
            response.getWriter().write(notLogin);
            return false;
        }
        //6.放行
        log.info("令牌合法，放行");
        return true;
    }
}
