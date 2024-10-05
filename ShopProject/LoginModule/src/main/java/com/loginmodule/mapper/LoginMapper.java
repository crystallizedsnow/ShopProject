package com.loginmodule.mapper;

import com.loginmodule.pojo.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface LoginMapper {
    @Select("select * from user where email=#{email} and password=#{password}")
    User getByEmailAndPassword(User user);
    @Insert("insert into user(username,email,password,type) values(#{username},#{email},#{password},#{type})")
    boolean insertUser(User user);

    @Select("select * from user where email=#{email}")
    User getByemail(User user);
}
