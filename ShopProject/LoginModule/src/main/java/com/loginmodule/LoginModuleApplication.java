package com.loginmodule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@ServletComponentScan//开启对servlet组件的支持,扫描component
@EnableScheduling  // 启用定时任务
@SpringBootApplication
public class LoginModuleApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoginModuleApplication.class, args);
    }

}
