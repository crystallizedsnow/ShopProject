package com.loginmodule.config;  // 使用你项目的实际包名

import com.loginmodule.interceptor.LoginCheckInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private LoginCheckInterceptor loginCheckInterceptor;
    @Override//配置拦截器
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginCheckInterceptor)
                .addPathPatterns("/**") // 拦截所有路径
                .excludePathPatterns("/goods","display","/login","/register");
    }
    @Override//配置cors，解决不同源问题
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // 匹配所有路径
                .allowedOrigins("http://8.155.18.88:7001")  // 允许所有来源的请求
                .allowedMethods("*")  // 允许的请求方法
                .allowedHeaders("*")  // 允许的请求头
                .allowCredentials(true)// 允许发送凭据（如 Cookie）
                .maxAge(30*1000);//跨域请求不预先检查
//        registry.addMapping("/**")  // 匹配所有路径
//                .allowedOrigins("*")  // 允许所有来源的请求
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // 允许的请求方法
//                .allowedHeaders("*");  // 允许的请求头
    }

}