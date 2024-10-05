package com.loginmodule.service;

import com.loginmodule.pojo.User;
import org.springframework.stereotype.Service;

@Service
public interface LoginService {
    User login(User user);

    boolean register(User user);

}
