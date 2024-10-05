package com.loginmodule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan//开启对servlet组件的支持,扫描component
@SpringBootApplication
public class LoginModuleApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoginModuleApplication.class, args);
    }

}
