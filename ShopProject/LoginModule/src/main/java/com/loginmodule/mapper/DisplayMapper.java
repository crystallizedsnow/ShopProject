package com.loginmodule.mapper;

import com.loginmodule.pojo.Good;
import com.loginmodule.pojo.PageBean;
import com.loginmodule.pojo.Type;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface DisplayMapper {
    List<Good> list(@Param("name") String goodName, @Param("type") Integer type,
                    @Param("shopId") Integer shopId, @Param("plow") Double plow, @Param("phigh") Double phigh);
    @Select("select typeName from good_type where good_type.type=#{type}")
    String getTypeName(Integer type);
    @Select("select * from good_type")
    List<Type> getTypes();
    @Select(" select good.goodId as goodId,good.name,type,shop.name as shopName,shop.shopId as shopId,price,num,image" +
            " from good,shop where good.goodId=#{goodId} and good.shopId=shop.shopId")
    Map<String, Object> selectById(String goodId);
}
