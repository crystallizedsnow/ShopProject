package com.loginmodule.service.impl;

import com.loginmodule.mapper.LoginMapper;
import com.loginmodule.pojo.User;
import com.loginmodule.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private LoginMapper loginMapper;

    @Override
    public boolean register(User user) {
        //判断邮箱是否合法，用户名，密码是否为空
        if(user.getEmail()==null||user.getEmail().isEmpty()||user.getUsername()==null||user.getUsername().isEmpty()||user.getPassword()==null||user.getPassword().isEmpty()) {
            return false;
        }
//        else if(!Pattern.matches("^(\\w+([-.][A-Za-z0-9]+)*){3,18}@\\w+([-.][A-Za-z0-9]+)*\\.\\w+([-.][A-Za-z0-9]+)*$",user.getEmail())){
//            return false;
//        }
        //判断邮箱是否注册过
        if(loginMapper.getByemail(user)==null) {
            return loginMapper.insertUser(user);
        }
        else {
            return false;
        }
    }

    @Override
    public User login(User user) {
        return loginMapper.getByEmailAndPassword(user);
    }
}
