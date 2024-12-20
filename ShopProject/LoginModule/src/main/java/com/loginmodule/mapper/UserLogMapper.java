package com.loginmodule.mapper;

import com.loginmodule.pojo.UserLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface UserLogMapper {
    @Insert("insert into user_log(userId,goodId,action,createTime) values (#{userId},#{goodId},#{action},#{createTime})")
    void insertUserLog(@Param("userId") Integer userId,@Param("goodId") String goodId,@Param("action") Integer action,@Param("createTime")LocalDateTime createTime);
    List<Map<String,Object>>selectUserLog(@Param("shopId") Integer shopId, @Param("action") Integer action, @Param("createTimeLow") LocalDateTime createTimeLow, @Param("createTimeHigh") LocalDateTime createTimeHigh);

    void batchInsertUserLogs(@Param("userLogs")List<UserLog> userLogs);
}
