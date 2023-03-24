package com.wms.gateway.mapper;


import com.wms.api.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    @Select("select username,password,permission from user where username = #{username} limit 1")
    User findUserByUsername(String username);
}
